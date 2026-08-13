package com.wingedsheep.engine.handlers.effects.drawing

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.EngineServices
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.core.YesNoResponse
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.ComponentContainer
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.CommanderComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
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
import com.wingedsheep.sdk.scripting.effects.EachPlayerReturnsPermanentToHandEffect
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * The group pattern deliberately lowers its selected-card step through a pause-aware
 * ForEach + MoveToZone pipeline.  These cases prove a Commander replacement is offered
 * after the player selects the permanent and before the physical hand move.
 */
class EachPlayerReturnsPermanentCommanderReplacementTest : FunSpec({
    val services = EngineServices(CardRegistry())
    val owner = EntityId.generate()
    val commander = EntityId.generate()
    val ordinaryPermanent = EntityId.generate()

    fun state() = GameState(
        format = Format.Commander(),
        activePlayerId = owner,
        turnOrder = listOf(owner)
    )
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
        .withEntity(ordinaryPermanent, ComponentContainer.of(
            CardComponent(
                cardDefinitionId = "TestPermanent", name = "Test Permanent",
                manaCost = ManaCost.ZERO,
                typeLine = TypeLine(cardTypes = setOf(CardType.CREATURE)),
                oracleText = "", baseStats = CreatureStats(1, 1), colors = setOf(Color.BLUE), ownerId = owner
            ),
            OwnerComponent(owner), ControllerComponent(owner)
        ))
        .addToZone(ZoneKey(owner, Zone.BATTLEFIELD), commander)
        .addToZone(ZoneKey(owner, Zone.BATTLEFIELD), ordinaryPermanent)

    fun selectCommander(initial: GameState) = services.effectExecutorRegistry.execute(
        initial,
        EachPlayerReturnsPermanentToHandEffect,
        EffectContext(sourceId = null, controllerId = owner)
    ).let { result ->
        result.isPaused shouldBe true
        val select = result.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        services.continuationHandler.resume(
            result.state.clearPendingDecision(),
            CardsSelectedResponse(select.id, listOf(commander))
        ).state
    }

    fun answerCommanderReplacement(state: GameState, accept: Boolean) = state.pendingDecision
        .shouldBeInstanceOf<YesNoDecision>()
        .let { decision ->
            services.continuationHandler.resume(
                state.clearPendingDecision(), YesNoResponse(decision.id, accept)
            ).state
        }

    test("each-player bounce offers the commander owner a pre-move accept choice") {
        val afterSelection = selectCommander(state())
        afterSelection.getZone(ZoneKey(owner, Zone.BATTLEFIELD)).shouldContainExactly(commander, ordinaryPermanent)
        afterSelection.pendingDecision.shouldBeInstanceOf<YesNoDecision>()

        val resolved = answerCommanderReplacement(afterSelection, accept = true)
        resolved.getZone(ZoneKey(owner, Zone.COMMAND)).shouldContainExactly(commander)
        resolved.getZone(ZoneKey(owner, Zone.HAND)).shouldContainExactly()
        resolved.getZone(ZoneKey(owner, Zone.BATTLEFIELD)).shouldContainExactly(ordinaryPermanent)
        resolved.pendingDecision.shouldBeNull()
    }

    test("each-player bounce honors a commander owner's decline and moves once to hand") {
        val resolved = answerCommanderReplacement(selectCommander(state()), accept = false)
        resolved.getZone(ZoneKey(owner, Zone.HAND)).shouldContainExactly(commander)
        resolved.getZone(ZoneKey(owner, Zone.COMMAND)).shouldContainExactly()
        resolved.getZone(ZoneKey(owner, Zone.BATTLEFIELD)).shouldContainExactly(ordinaryPermanent)
        resolved.pendingDecision.shouldBeNull()
    }
})
