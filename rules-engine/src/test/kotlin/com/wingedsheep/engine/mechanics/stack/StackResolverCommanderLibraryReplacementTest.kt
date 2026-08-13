package com.wingedsheep.engine.mechanics.stack

import com.wingedsheep.engine.core.CardEntityFactory
import com.wingedsheep.engine.core.EngineServices
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.core.YesNoResponse
import com.wingedsheep.engine.core.ZoneChangeEvent
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.ComponentContainer
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.CommanderComponent
import com.wingedsheep.engine.state.components.identity.OwnerComponent
import com.wingedsheep.engine.state.components.stack.SpellOnStackComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.state.components.stack.TargetsComponent
import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Format
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Supertype
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.RedirectZoneChange
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * StackResolver must treat a stack -> library path as a real, pre-move
 * Commander replacement opportunity.  In particular, resolution/countering may
 * not leave a second copy on stack or run the shuffle/rider after command was chosen.
 */
class StackResolverCommanderLibraryReplacementTest : FunSpec({
    val owner = EntityId.generate()
    val spell = EntityId.generate()
    val omen = card("Commander Omen") {
        manaCost = "{1}{U}"
        typeLine = "Legendary Creature — Dragon"
        power = 2
        toughness = 2
        // An Omen face is a spell face.  Supplying an empty spell block keeps this
        // fixture effect-free while satisfying the CardDefinition invariant that
        // instant/sorcery faces have a spell ability.
        omen("Omen Spell") {
            typeLine = "Instant — Omen"
            spell { effect = Effects.DrawCards(0) }
        }
    }
    val tucked = card("Tuck Commander") {
        manaCost = "{1}{U}"
        typeLine = "Legendary Creature — Wizard"
        power = 2
        toughness = 2
        replacementEffect(RedirectZoneChange(
            newDestination = Zone.LIBRARY,
            appliesTo = EventPattern.ZoneChangeEvent(to = Zone.GRAVEYARD),
            selfOnly = true,
            shuffleIntoLibrary = true,
        ))
    }

    fun servicesFor(definition: com.wingedsheep.sdk.model.CardDefinition): EngineServices =
        EngineServices(CardRegistry().also { it.register(definition) })

    fun stateFor(definition: com.wingedsheep.sdk.model.CardDefinition, faceIndex: Int? = null): GameState =
        GameState(format = Format.Commander(), turnOrder = listOf(owner))
            .withEntity(owner, ComponentContainer.EMPTY)
            // The fixture builds its own entity rather than casting through the normal
            // pipeline, so install the definition-derived decorations explicitly.  In
            // particular this gives `tucked` its intrinsic SelfZoneRedirectComponent,
            // which must function while the card is on the stack.
            .withEntity(spell, CardEntityFactory.applyDefinitionDecorations(
                ComponentContainer.of(
                    CardComponent(
                        cardDefinitionId = definition.name,
                        name = definition.name,
                        manaCost = ManaCost.ZERO,
                        typeLine = TypeLine(setOf(Supertype.LEGENDARY), setOf(CardType.CREATURE)),
                        ownerId = owner,
                    ),
                    OwnerComponent(owner),
                    CommanderComponent(owner),
                    SpellOnStackComponent(casterId = owner, faceIndex = faceIndex)
                ),
                definition
            ))
            .pushToStack(spell)

    fun resumeYesNo(services: EngineServices, state: GameState, choice: Boolean) = state.pendingDecision
        .shouldBeInstanceOf<YesNoDecision>()
        .let { services.continuationHandler.resume(state.clearPendingDecision(), YesNoResponse(it.id, choice)) }

    test("Omen resolution offers stack-to-library Commander replacement; accept moves once to command without shuffle") {
        val services = servicesFor(omen)
        val result = StackResolver(services.cardRegistry).resolveTop(stateFor(omen, faceIndex = 0))

        result.isPaused shouldBe true
        result.state.stack shouldBe emptyList()
        val resolved = resumeYesNo(services, result.state, true)
        resolved.state.getZone(ZoneKey(owner, Zone.COMMAND)).shouldContainExactly(spell)
        resolved.state.getZone(ZoneKey(owner, Zone.LIBRARY)) shouldBe emptyList()
        resolved.events.filterIsInstance<ZoneChangeEvent>().also { it.shouldHaveSize(1) }.single().toZone shouldBe Zone.COMMAND
    }

    test("Omen resolution decline shuffles exactly once into library") {
        val services = servicesFor(omen)
        val paused = StackResolver(services.cardRegistry).resolveTop(stateFor(omen, faceIndex = 0))
        val resolved = resumeYesNo(services, paused.state, false)

        resolved.state.getZone(ZoneKey(owner, Zone.LIBRARY)).shouldContainExactly(spell)
        resolved.state.getZone(ZoneKey(owner, Zone.COMMAND)) shouldBe emptyList()
        resolved.events.filterIsInstance<ZoneChangeEvent>().also { it.shouldHaveSize(1) }.single().toZone shouldBe Zone.LIBRARY
    }

    test("counter redirect to library offers Commander replacement after the intrinsic redirect applies") {
        val services = servicesFor(tucked)
        val resolver = StackResolver(services.cardRegistry)
        val initial = resolver.counterSpell(stateFor(tucked), spell)
        initial.isPaused shouldBe true
        val resolved = resumeYesNo(services, initial.state, false)
        resolved.state.stack shouldBe emptyList()
        resolved.state.getZone(ZoneKey(owner, Zone.LIBRARY)).shouldContainExactly(spell)
        resolved.events.filterIsInstance<ZoneChangeEvent>().also { it.shouldHaveSize(1) }.single().toZone shouldBe Zone.LIBRARY
    }

    test("fizzle redirect to library offers Commander replacement after the intrinsic redirect applies") {
        val services = servicesFor(tucked)
        val resolver = StackResolver(services.cardRegistry)
        val missingTarget = EntityId.generate()
        val initial = resolver.resolveTop(
            stateFor(tucked).updateEntity(spell) {
                it.with(TargetsComponent(listOf(ChosenTarget.Permanent(missingTarget))))
            }
        )
        initial.isPaused shouldBe true
        val resolved = resumeYesNo(services, initial.state, false)
        resolved.state.stack shouldBe emptyList()
        resolved.state.getZone(ZoneKey(owner, Zone.LIBRARY)).shouldContainExactly(spell)
        resolved.events.filterIsInstance<ZoneChangeEvent>().also { it.shouldHaveSize(1) }.single().toZone shouldBe Zone.LIBRARY
    }
})
