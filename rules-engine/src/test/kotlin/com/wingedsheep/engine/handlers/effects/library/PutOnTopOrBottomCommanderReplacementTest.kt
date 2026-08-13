package com.wingedsheep.engine.handlers.effects.library

import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.EngineServices
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.core.YesNoResponse
import com.wingedsheep.engine.core.ZoneChangeEvent
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.ComponentContainer
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.CommanderComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.state.components.identity.OwnerComponent
import com.wingedsheep.engine.state.components.identity.RevealedToComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.state.components.stack.SpellOnStackComponent
import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Format
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Supertype
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.CreatureStats
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.effects.LibraryChoicePosition
import com.wingedsheep.sdk.scripting.effects.PutOnLibraryPositionOfChoiceEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Exercises the existing position-choice continuation through the Commander
 * hand/library replacement pipeline.  This is deliberately not a direct
 * ZoneTransitionService test: it proves the parent continuation's reveal work
 * survives the nested replacement decision and runs only after the final move.
 */
class PutOnTopOrBottomCommanderReplacementTest : FunSpec({
    val services = EngineServices(CardRegistry())
    val executor = PutOnTopOrBottomOfLibraryExecutor()
    val owner = EntityId.generate()
    val commander = EntityId.generate()

    fun state() = GameState(format = Format.Commander())
        .withEntity(owner, ComponentContainer.EMPTY)
        .withEntity(commander, ComponentContainer.of(
            CardComponent(
                cardDefinitionId = "TestCommander", name = "Test Commander",
                manaCost = ManaCost.ZERO,
                typeLine = TypeLine(setOf(Supertype.LEGENDARY), setOf(CardType.CREATURE)),
                oracleText = "", baseStats = CreatureStats(2, 2), colors = setOf(Color.BLUE), ownerId = owner
            ),
            OwnerComponent(owner), ControllerComponent(owner), CommanderComponent(owner)
        ))
        .addToZone(ZoneKey(owner, Zone.BATTLEFIELD), commander)
        .copy(turnOrder = listOf(owner))

    fun spellState(isCommander: Boolean) = GameState(format = Format.Commander(), turnOrder = listOf(owner))
        .withEntity(owner, ComponentContainer.EMPTY)
        .withEntity(
            commander,
            if (isCommander) {
                ComponentContainer.of(
                    CardComponent(
                        cardDefinitionId = "TestCommander", name = "Test Commander",
                        manaCost = ManaCost.ZERO,
                        typeLine = TypeLine(setOf(Supertype.LEGENDARY), setOf(CardType.CREATURE)),
                        oracleText = "", baseStats = CreatureStats(2, 2), colors = setOf(Color.BLUE), ownerId = owner
                    ),
                    OwnerComponent(owner), CommanderComponent(owner), SpellOnStackComponent(casterId = owner)
                )
            } else {
                ComponentContainer.of(
                    CardComponent(
                        cardDefinitionId = "TestSpell", name = "Test Spell",
                        manaCost = ManaCost.ZERO,
                        typeLine = TypeLine(cardTypes = setOf(CardType.INSTANT)),
                        oracleText = "", ownerId = owner
                    ),
                    OwnerComponent(owner), SpellOnStackComponent(casterId = owner)
                )
            }
        )
        .pushToStack(commander)

    fun chooseLibraryPosition(initial: GameState) = executor.execute(
        initial,
        PutOnLibraryPositionOfChoiceEffect(
            target = EffectTarget.ContextTarget(0),
            positions = listOf(LibraryChoicePosition.Top, LibraryChoicePosition.Bottom)
        ),
        EffectContext(null, owner, targets = listOf(ChosenTarget.Permanent(commander)))
    ).let { positionResult ->
        val position = positionResult.pendingDecision.shouldBeInstanceOf<ChooseOptionDecision>()
        services.continuationHandler.resume(
            positionResult.state.clearPendingDecision(),
            OptionChosenResponse(position.id, 0)
        )
    }

    fun chooseLibraryPositionForSpell(initial: GameState) = executor.execute(
        initial,
        PutOnLibraryPositionOfChoiceEffect(
            target = EffectTarget.ContextTarget(0),
            positions = listOf(LibraryChoicePosition.Top, LibraryChoicePosition.Bottom)
        ),
        EffectContext(null, owner, targets = listOf(ChosenTarget.Spell(commander)))
    ).let { positionResult ->
        val position = positionResult.pendingDecision.shouldBeInstanceOf<ChooseOptionDecision>()
        services.continuationHandler.resume(
            positionResult.state.clearPendingDecision(),
            OptionChosenResponse(position.id, 0)
        )
    }

    test("tuck commander accept waits for replacement then does not reveal a card outside the library") {
        val replacementPause = chooseLibraryPosition(state())
        replacementPause.isPaused shouldBe true
        val commanderChoice = replacementPause.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        replacementPause.state.getZone(ZoneKey(owner, Zone.BATTLEFIELD)).shouldContainExactly(commander)

        val result = services.continuationHandler.resume(
            replacementPause.state.clearPendingDecision(), YesNoResponse(commanderChoice.id, true)
        )

        result.state.getZone(ZoneKey(owner, Zone.COMMAND)).shouldContainExactly(commander)
        result.state.getZone(ZoneKey(owner, Zone.LIBRARY)).shouldContainExactly()
        result.state.getEntity(commander)?.get<RevealedToComponent>() shouldBe null
        result.events.filterIsInstance<ZoneChangeEvent>().size shouldBe 1
        result.events.filterIsInstance<ZoneChangeEvent>().single().toZone shouldBe Zone.COMMAND
    }

    test("tuck commander decline resumes reveal bookkeeping after exactly one library move") {
        val replacementPause = chooseLibraryPosition(state())
        val commanderChoice = replacementPause.pendingDecision.shouldBeInstanceOf<YesNoDecision>()

        val result = services.continuationHandler.resume(
            replacementPause.state.clearPendingDecision(), YesNoResponse(commanderChoice.id, false)
        )

        result.state.getZone(ZoneKey(owner, Zone.LIBRARY)).shouldContainExactly(commander)
        result.state.getZone(ZoneKey(owner, Zone.COMMAND)).shouldContainExactly()
        result.state.getEntity(commander)?.get<RevealedToComponent>()?.playerIds shouldBe setOf(owner)
        result.events.filterIsInstance<ZoneChangeEvent>().size shouldBe 1
        result.events.filterIsInstance<ZoneChangeEvent>().single().toZone shouldBe Zone.LIBRARY
    }

    test("tuck commander spell accepts the stack-to-library replacement with one stack-to-command event") {
        val paused = chooseLibraryPositionForSpell(spellState(isCommander = true))
        paused.isPaused shouldBe true
        paused.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        paused.state.stack.shouldContainExactly(commander)

        val decision = paused.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        val result = services.continuationHandler.resume(
            paused.state.clearPendingDecision(), YesNoResponse(decision.id, true)
        )

        result.error shouldBe null
        result.state.stack shouldBe emptyList()
        result.state.getZone(ZoneKey(owner, Zone.COMMAND)).shouldContainExactly(commander)
        result.state.getZone(ZoneKey(owner, Zone.LIBRARY)) shouldBe emptyList()
        result.state.getEntity(commander)?.has<SpellOnStackComponent>() shouldBe false
        result.events.filterIsInstance<ZoneChangeEvent>().also { it.size shouldBe 1 }.single().let { event ->
            event.fromZone shouldBe Zone.STACK
            event.toZone shouldBe Zone.COMMAND
        }
    }

    test("tuck commander spell declines replacement with one stack-to-library event") {
        val paused = chooseLibraryPositionForSpell(spellState(isCommander = true))
        val decision = paused.pendingDecision.shouldBeInstanceOf<YesNoDecision>()

        val result = services.continuationHandler.resume(
            paused.state.clearPendingDecision(), YesNoResponse(decision.id, false)
        )

        result.error shouldBe null
        result.state.stack shouldBe emptyList()
        result.state.getZone(ZoneKey(owner, Zone.LIBRARY)).shouldContainExactly(commander)
        result.state.getZone(ZoneKey(owner, Zone.COMMAND)) shouldBe emptyList()
        result.state.getEntity(commander)?.has<SpellOnStackComponent>() shouldBe false
        result.events.filterIsInstance<ZoneChangeEvent>().also { it.size shouldBe 1 }.single().let { event ->
            event.fromZone shouldBe Zone.STACK
            event.toZone shouldBe Zone.LIBRARY
        }
    }

    test("ordinary spell tuck still leaves the stack, reaches library and does not request a commander choice") {
        val result = chooseLibraryPositionForSpell(spellState(isCommander = false))

        result.error shouldBe null
        result.isPaused shouldBe false
        result.state.stack shouldBe emptyList()
        result.state.getZone(ZoneKey(owner, Zone.LIBRARY)).shouldContainExactly(commander)
        result.state.getEntity(commander)?.has<SpellOnStackComponent>() shouldBe false
        result.events.filterIsInstance<ZoneChangeEvent>().also { it.size shouldBe 1 }.single().let { event ->
            event.fromZone shouldBe Zone.STACK
            event.toZone shouldBe Zone.LIBRARY
        }
    }

    test("test fixture removes a stack object by entity id") {
        spellState(isCommander = false).removeFromStack(commander).stack shouldBe emptyList()
    }
})
