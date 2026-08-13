package com.wingedsheep.engine.handlers.continuations

import com.wingedsheep.engine.core.*
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.ComponentContainer
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.identity.*
import com.wingedsheep.sdk.core.*
import com.wingedsheep.sdk.model.CreatureStats
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Commander replacements inside Cascade/Discover's randomized bottoming must preserve
 * the already chosen random order and resume their enclosing flow exactly once.
 */
class CascadeDiscoverCommanderLibraryReplacementTest : FunSpec({
    val services = EngineServices(CardRegistry())

    data class Fixture(val owner: EntityId, val commander: EntityId, val first: EntityId, val second: EntityId, val state: GameState)

    fun fixture(): Fixture {
        val owner = EntityId.generate()
        val commander = EntityId.generate()
        val first = EntityId.generate()
        val second = EntityId.generate()
        fun card(id: EntityId, name: String, commanderCard: Boolean = false) = id to ComponentContainer.of(
            CardComponent(name, name, ManaCost.ZERO,
                TypeLine(setOf(Supertype.LEGENDARY), setOf(CardType.CREATURE)), "",
                baseStats = CreatureStats(2, 2), colors = setOf(Color.BLUE), ownerId = owner),
            OwnerComponent(owner),
            *if (commanderCard) arrayOf(CommanderComponent(owner)) else emptyArray()
        )
        val state = GameState(format = Format.Commander())
            .withEntity(owner, ComponentContainer.of(PlayerComponent("Owner", 20), LifeTotalComponent(20)))
            .withEntity(card(commander, "Commander", true).first, card(commander, "Commander", true).second)
            .withEntity(card(first, "First").first, card(first, "First").second)
            .withEntity(card(second, "Second").first, card(second, "Second").second)
            .addToZone(ZoneKey(owner, Zone.EXILE), commander)
            .addToZone(ZoneKey(owner, Zone.EXILE), first)
            .addToZone(ZoneKey(owner, Zone.EXILE), second)
            .copy(turnOrder = listOf(owner))
        return Fixture(owner, commander, first, second, state)
    }

    fun resume(state: GameState, response: YesNoResponse) =
        services.continuationHandler.resume(state.clearPendingDecision(), response)

    test("Cascade decline pauses on a commander, retains the randomized remainder, and bottoms every noncommander once") {
        val f = fixture()
        val decision = YesNoDecision(
            "cascade", f.owner, "Cast?", DecisionContext(phase = DecisionPhase.RESOLUTION), "Cast", "Decline"
        )
        val initial = f.state.withPendingDecision(decision).pushContinuation(
            CascadeMayCastContinuation(decision.id, f.owner, null, listOf(f.commander, f.first, f.second), f.first)
        )
        val paused = resume(initial, YesNoResponse(decision.id, false))
        paused.isPaused shouldBe true
        val remainder = paused.state.continuationStack.filterIsInstance<BottomLibraryMoveRemainderContinuation>().single()
        val expectedAfterCommander = remainder.remainingMoveOrder.filter { it != f.commander }
        val choice = paused.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        val completed = resume(paused.state, YesNoResponse(choice.id, true))

        completed.state.getZone(ZoneKey(f.owner, Zone.COMMAND)).shouldContainExactly(f.commander)
        completed.state.getZone(ZoneKey(f.owner, Zone.LIBRARY)).shouldContainExactlyInAnyOrder(f.first, f.second)
        completed.events.filterIsInstance<ZoneChangeEvent>().filter { it.toZone == Zone.LIBRARY }
            .map { it.entityId }.shouldContainExactly(expectedAfterCommander)
    }

    test("Discover decline resumes after a commander bottom replacement and runs its follow-up once") {
        val f = fixture()
        val decision = YesNoDecision(
            "discover", f.owner, "Cast?", DecisionContext(phase = DecisionPhase.RESOLUTION), "Cast", "Hand"
        )
        val initial = f.state.withPendingDecision(decision).pushContinuation(
            DiscoverMayCastContinuation(
                decision.id, f.owner, null, listOf(f.commander, f.first), f.first,
                thenEffect = com.wingedsheep.sdk.scripting.effects.GainLifeEffect(1)
            )
        )
        val paused = resume(initial, YesNoResponse(decision.id, false))
        paused.isPaused shouldBe true
        val choice = paused.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        val completed = resume(paused.state, YesNoResponse(choice.id, true))

        completed.state.getZone(ZoneKey(f.owner, Zone.COMMAND)).shouldContainExactly(f.commander)
        completed.state.getZone(ZoneKey(f.owner, Zone.HAND)).shouldContainExactly(f.first)
        completed.state.lifeTotal(f.owner) shouldBe 21
    }
})
