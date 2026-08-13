package com.wingedsheep.engine.handlers.effects.library

import com.wingedsheep.engine.core.EngineServices
import com.wingedsheep.engine.core.LibraryShuffledEvent
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
import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Format
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.references.Player
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/** Regression coverage for the generic batch executor's CR 903.9b pause/resume path. */
class MoveCollectionCommanderReplacementTest : FunSpec({
    val services = EngineServices(CardRegistry())
    val executor = MoveCollectionExecutor(services.cardRegistry)
    val owner = EntityId.generate()
    val commander = EntityId.generate()
    val ordinary = EntityId.generate()

    fun card(name: String, id: EntityId, isCommander: Boolean) = ComponentContainer.of(
        CardComponent(name, name, ManaCost.ZERO, TypeLine(cardTypes = setOf(CardType.CREATURE)), ownerId = owner),
        OwnerComponent(owner), ControllerComponent(owner)
    ).let { if (isCommander) it.with(CommanderComponent(owner)) else it }

    fun state() = GameState(format = Format.Commander())
        .withEntity(owner, ComponentContainer.EMPTY)
        .withEntity(commander, card("Commander", commander, true))
        .withEntity(ordinary, card("Ordinary", ordinary, false))
        .addToZone(ZoneKey(owner, Zone.BATTLEFIELD), commander)
        .addToZone(ZoneKey(owner, Zone.BATTLEFIELD), ordinary)
        .copy(turnOrder = listOf(owner))

    fun execute(destination: CardDestination.ToZone) = executor.execute(
        state(),
        MoveCollectionEffect("chosen", destination),
        EffectContext(null, owner, pipeline = PipelineState(storedCollections = mapOf("chosen" to listOf(commander, ordinary))))
    )

    fun resume(paused: com.wingedsheep.engine.core.EffectResult, accept: Boolean) = services.continuationHandler.resume(
        paused.state.clearPendingDecision(),
        YesNoResponse(paused.pendingDecision.shouldBeInstanceOf<YesNoDecision>().id, accept)
    )

    test("accepting a batch bounce replacement moves commander once and continues remaining cards") {
        val paused = execute(CardDestination.ToZone(Zone.HAND, Player.You))
        paused.isPaused shouldBe true
        val resolved = resume(paused, true)

        resolved.state.getZone(ZoneKey(owner, Zone.COMMAND)).shouldContainExactly(commander)
        resolved.state.getZone(ZoneKey(owner, Zone.HAND)).shouldContainExactly(ordinary)
        resolved.events.filterIsInstance<ZoneChangeEvent>().map { it.entityId }.toSet() shouldBe setOf(commander, ordinary)
    }

    test("declining a batch tuck replacement puts every card in library and shuffles once") {
        val paused = execute(CardDestination.ToZone(Zone.LIBRARY, Player.You, ZonePlacement.Shuffled))
        paused.isPaused shouldBe true
        val resolved = resume(paused, false)

        resolved.state.getZone(ZoneKey(owner, Zone.LIBRARY)).toSet() shouldBe setOf(commander, ordinary)
        resolved.state.getZone(ZoneKey(owner, Zone.COMMAND)).shouldContainExactly()
        resolved.events.filterIsInstance<LibraryShuffledEvent>().count { it.playerId == owner } shouldBe 1
        resolved.events.filterIsInstance<ZoneChangeEvent>().count() shouldBe 2
    }

    test("ordinary MoveCollection remains synchronous") {
        val normal = executor.execute(
            state(), MoveCollectionEffect("chosen", CardDestination.ToZone(Zone.HAND, Player.You)),
            EffectContext(null, owner, pipeline = PipelineState(storedCollections = mapOf("chosen" to listOf(ordinary))))
        )
        normal.isSuccess shouldBe true
        normal.state.getZone(ZoneKey(owner, Zone.HAND)).shouldContainExactly(ordinary)
    }
})
