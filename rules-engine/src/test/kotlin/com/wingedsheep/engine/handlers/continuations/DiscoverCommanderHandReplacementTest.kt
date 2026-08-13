package com.wingedsheep.engine.handlers.continuations

import com.wingedsheep.engine.core.DecisionContext
import com.wingedsheep.engine.core.DecisionPhase
import com.wingedsheep.engine.core.DiscoverMayCastContinuation
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
import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Format
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Supertype
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.CreatureStats
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Covers the Discover continuation's declined-cast hand route, rather than a
 * direct zone service call.  The commander starts in the zone Discover puts
 * its hit into, so both answers exercise the exact nested continuation chain.
 */
class DiscoverCommanderHandReplacementTest : FunSpec({
    val services = EngineServices(CardRegistry())
    val owner = EntityId.generate()
    val commander = EntityId.generate()

    fun state(): GameState = GameState(format = Format.Commander())
        .withEntity(owner, ComponentContainer.EMPTY)
        .withEntity(commander, ComponentContainer.of(
            CardComponent(
                cardDefinitionId = "DiscoverCommander", name = "Discover Commander",
                manaCost = ManaCost.ZERO,
                typeLine = TypeLine(setOf(Supertype.LEGENDARY), setOf(CardType.CREATURE)),
                oracleText = "", baseStats = CreatureStats(2, 2), colors = setOf(Color.BLUE), ownerId = owner
            ),
            OwnerComponent(owner), CommanderComponent(owner)
        ))
        .addToZone(ZoneKey(owner, Zone.EXILE), commander)
        .copy(turnOrder = listOf(owner))

    fun declineFreeCast(initial: GameState) = run {
        val decision = YesNoDecision(
            id = "discover-may-cast", playerId = owner,
            prompt = "Cast for free?", yesText = "Cast", noText = "Put into hand",
            context = DecisionContext(phase = DecisionPhase.RESOLUTION)
        )
        val withContinuation = initial
            .withPendingDecision(decision)
            .pushContinuation(
                DiscoverMayCastContinuation(
                    decisionId = decision.id,
                    playerId = owner,
                    sourceId = null,
                    exiledCards = listOf(commander),
                    discoveredCardId = commander
                )
            )
        services.continuationHandler.resume(
            withContinuation.clearPendingDecision(), YesNoResponse(decision.id, false)
        )
    }

    test("declining Discover free cast pauses before a commander moves from exile to hand, then acceptance moves it once to command") {
        val replacementPause = declineFreeCast(state())
        replacementPause.isPaused shouldBe true
        replacementPause.state.getZone(ZoneKey(owner, Zone.EXILE)).shouldContainExactly(commander)
        val choice = replacementPause.pendingDecision.shouldBeInstanceOf<YesNoDecision>()

        val result = services.continuationHandler.resume(
            replacementPause.state.clearPendingDecision(), YesNoResponse(choice.id, true)
        )

        result.state.getZone(ZoneKey(owner, Zone.COMMAND)).shouldContainExactly(commander)
        result.state.getZone(ZoneKey(owner, Zone.HAND)).shouldContainExactly()
        result.events.filterIsInstance<ZoneChangeEvent>().single().let { event ->
            event.fromZone shouldBe Zone.EXILE
            event.toZone shouldBe Zone.COMMAND
        }
    }

    test("declining both Discover prompts performs exactly one exile-to-hand move") {
        val replacementPause = declineFreeCast(state())
        val choice = replacementPause.pendingDecision.shouldBeInstanceOf<YesNoDecision>()

        val result = services.continuationHandler.resume(
            replacementPause.state.clearPendingDecision(), YesNoResponse(choice.id, false)
        )

        result.state.getZone(ZoneKey(owner, Zone.HAND)).shouldContainExactly(commander)
        result.state.getZone(ZoneKey(owner, Zone.COMMAND)).shouldContainExactly()
        result.state.pendingDecision shouldBe null
        result.events.filterIsInstance<ZoneChangeEvent>().single().let { event ->
            event.fromZone shouldBe Zone.EXILE
            event.toZone shouldBe Zone.HAND
        }
    }
})
