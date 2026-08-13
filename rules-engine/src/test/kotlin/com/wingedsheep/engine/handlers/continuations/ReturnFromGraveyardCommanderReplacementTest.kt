package com.wingedsheep.engine.handlers.continuations

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.DecisionContext
import com.wingedsheep.engine.core.DecisionPhase
import com.wingedsheep.engine.core.EngineServices
import com.wingedsheep.engine.core.ReturnFromGraveyardContinuation
import com.wingedsheep.engine.core.SelectCardsDecision
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
import com.wingedsheep.sdk.scripting.effects.SearchDestination
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/** Covers the graveyard-selection continuation, not the direct zone service entry point. */
class ReturnFromGraveyardCommanderReplacementTest : FunSpec({
    val services = EngineServices(CardRegistry())
    val owner = EntityId.generate()
    val commander = EntityId.generate()

    fun state(): GameState = GameState(format = Format.Commander())
        .withEntity(owner, ComponentContainer.EMPTY)
        .withEntity(commander, ComponentContainer.of(
            CardComponent(
                cardDefinitionId = "GraveyardCommander", name = "Graveyard Commander",
                manaCost = ManaCost.ZERO,
                typeLine = TypeLine(setOf(Supertype.LEGENDARY), setOf(CardType.CREATURE)),
                oracleText = "", baseStats = CreatureStats(2, 2), colors = setOf(Color.BLUE), ownerId = owner
            ),
            OwnerComponent(owner), CommanderComponent(owner)
        ))
        .addToZone(ZoneKey(owner, Zone.GRAVEYARD), commander)
        .copy(turnOrder = listOf(owner))

    fun selectCommander(initial: GameState) = run {
        val decision = SelectCardsDecision(
            id = "return-from-graveyard", playerId = owner, prompt = "Choose a card",
            context = DecisionContext(phase = DecisionPhase.RESOLUTION), options = listOf(commander),
            minSelections = 0, maxSelections = 1
        )
        val withContinuation = initial
            .withPendingDecision(decision)
            .pushContinuation(ReturnFromGraveyardContinuation(
                decisionId = decision.id,
                playerId = owner,
                sourceId = null,
                sourceName = null,
                destination = SearchDestination.HAND
            ))
        services.continuationHandler.resume(
            withContinuation.clearPendingDecision(),
            CardsSelectedResponse(decision.id, listOf(commander))
        )
    }

    test("selected commander return to hand pauses before moving and acceptance performs one command-zone move") {
        val replacementPause = selectCommander(state())
        replacementPause.isPaused shouldBe true
        replacementPause.state.getZone(ZoneKey(owner, Zone.GRAVEYARD)).shouldContainExactly(commander)
        val choice = replacementPause.pendingDecision.shouldBeInstanceOf<YesNoDecision>()

        val result = services.continuationHandler.resume(
            replacementPause.state.clearPendingDecision(), YesNoResponse(choice.id, true)
        )

        result.state.getZone(ZoneKey(owner, Zone.COMMAND)).shouldContainExactly(commander)
        result.state.getZone(ZoneKey(owner, Zone.HAND)).shouldContainExactly()
        result.events.filterIsInstance<ZoneChangeEvent>().single().toZone shouldBe Zone.COMMAND
    }

    test("selected commander return to hand declines to one hand move without a post-move prompt") {
        val replacementPause = selectCommander(state())
        val choice = replacementPause.pendingDecision.shouldBeInstanceOf<YesNoDecision>()

        val result = services.continuationHandler.resume(
            replacementPause.state.clearPendingDecision(), YesNoResponse(choice.id, false)
        )

        result.state.getZone(ZoneKey(owner, Zone.HAND)).shouldContainExactly(commander)
        result.state.getZone(ZoneKey(owner, Zone.COMMAND)).shouldContainExactly()
        result.state.pendingDecision shouldBe null
        result.events.filterIsInstance<ZoneChangeEvent>().single().toZone shouldBe Zone.HAND
    }
})
