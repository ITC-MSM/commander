package com.wingedsheep.engine.handlers.effects.library

import com.wingedsheep.engine.core.EngineServices
import com.wingedsheep.engine.core.OrderedResponse
import com.wingedsheep.engine.core.ReorderLibraryDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.core.YesNoResponse
import com.wingedsheep.engine.core.ZoneChangeEvent
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.PipelineState
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.ComponentContainer
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.CommanderComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.state.components.identity.OwnerComponent
import com.wingedsheep.engine.state.components.identity.RevealedToComponent
import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Format
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.references.Player
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/** Covers the ordering-continuation branch rather than the ordinary MoveCollection branch. */
class MoveCollectionOrderCommanderReplacementTest : FunSpec({
    val services = EngineServices(CardRegistry())
    val executor = MoveCollectionExecutor(services.cardRegistry)
    val owner = EntityId.generate()
    val commander = EntityId.generate()
    val ordinary = EntityId.generate()

    fun card(name: String, id: EntityId, commanderCard: Boolean) = ComponentContainer.of(
        CardComponent(name, name, ManaCost.ZERO, TypeLine(cardTypes = setOf(CardType.CREATURE)), ownerId = owner),
        OwnerComponent(owner), ControllerComponent(owner)
    ).let { if (commanderCard) it.with(CommanderComponent(owner)) else it }

    fun state(includeCommander: Boolean = true) = GameState(format = Format.Commander())
        .withEntity(owner, ComponentContainer.EMPTY)
        .withEntity(commander, card("Commander", commander, true))
        .withEntity(ordinary, card("Ordinary", ordinary, false))
        .addToZone(ZoneKey(owner, Zone.BATTLEFIELD), ordinary)
        .let { if (includeCommander) it.addToZone(ZoneKey(owner, Zone.BATTLEFIELD), commander) else it }
        .copy(turnOrder = listOf(owner))

    fun orderPause(initial: GameState, cards: List<EntityId>) = executor.execute(
        initial,
        MoveCollectionEffect("chosen", CardDestination.ToZone(Zone.LIBRARY, Player.You), order = CardOrder.ControllerChooses),
        EffectContext(null, owner, pipeline = PipelineState(storedCollections = mapOf("chosen" to cards)))
    )

    fun order(initial: GameState, cards: List<EntityId>) = orderPause(initial, cards).let { paused ->
        val decision = paused.pendingDecision.shouldBeInstanceOf<ReorderLibraryDecision>()
        services.continuationHandler.resume(
            paused.state.clearPendingDecision(), OrderedResponse(decision.id, cards)
        )
    }

    test("ordered tuck accepts Commander replacement before the move and preserves remaining order") {
        val replacementPause = order(state(), listOf(ordinary, commander))
        replacementPause.isPaused shouldBe true
        val choice = replacementPause.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        // The Commander replacement pauses before its own movement; the other ordered card
        // has not moved either because top insertion processes the selected order in reverse.
        replacementPause.state.getZone(ZoneKey(owner, Zone.BATTLEFIELD)).shouldContainExactly(ordinary, commander)

        val result = services.continuationHandler.resume(
            replacementPause.state.clearPendingDecision(), YesNoResponse(choice.id, true)
        )

        result.state.getZone(ZoneKey(owner, Zone.COMMAND)).shouldContainExactly(commander)
        result.state.getZone(ZoneKey(owner, Zone.LIBRARY)).shouldContainExactly(ordinary)
        result.state.getEntity(ordinary)?.get<RevealedToComponent>()?.playerIds shouldBe setOf(owner)
        result.events.filterIsInstance<ZoneChangeEvent>().also { it.size shouldBe 2 }.map { it.entityId }.toSet() shouldBe setOf(commander, ordinary)
    }

    test("ordered tuck decline puts Commander in the selected library order with one move each") {
        val replacementPause = order(state(), listOf(ordinary, commander))
        val choice = replacementPause.pendingDecision.shouldBeInstanceOf<YesNoDecision>()

        val result = services.continuationHandler.resume(
            replacementPause.state.clearPendingDecision(), YesNoResponse(choice.id, false)
        )

        result.state.getZone(ZoneKey(owner, Zone.COMMAND)).shouldContainExactly()
        result.state.getZone(ZoneKey(owner, Zone.LIBRARY)).shouldContainExactly(ordinary, commander)
        result.state.getEntity(commander)?.get<RevealedToComponent>()?.playerIds shouldBe setOf(owner)
        result.events.filterIsInstance<ZoneChangeEvent>().also { it.size shouldBe 2 }.map { it.toZone }.toSet() shouldBe setOf(Zone.LIBRARY)
    }

    test("ordinary ordered library move remains synchronous and retains the chosen order") {
        val result = order(state(includeCommander = false), listOf(ordinary))

        result.isPaused shouldBe false
        result.state.getZone(ZoneKey(owner, Zone.LIBRARY)).shouldContainExactly(ordinary)
        result.events.filterIsInstance<ZoneChangeEvent>().also { it.size shouldBe 1 }.single().toZone shouldBe Zone.LIBRARY
    }
})
