package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.core.YesNoResponse
import com.wingedsheep.engine.core.ZoneChangeEvent
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.CommanderComponent
import com.wingedsheep.engine.state.components.stack.SpellOnStackComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Format
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * The normal spell-additional-cost path is distinct from activated ability payment: the return
 * happens after other cost atoms may already have been paid, but before mana payment and stack
 * insertion. These cases prove the Commander replacement pauses precisely there.
 */
class CastSpellReturnToHandCommanderReplacementTest : ScenarioTestBase() {
    init {
    cardRegistry.register(card("Postcost Commander Test Spell") {
        manaCost = "{U}"
        typeLine = "Sorcery"
        oracleText = "As an additional cost to cast this spell, pay 1 life and return a permanent you control to its owner's hand."
        additionalCost(Costs.additional.Composite(listOf(
            Costs.additional.PayLife(1),
            Costs.additional.ReturnToHand()
        )))
    })

    fun game() = scenario()
        .withPlayers("Player1", "Player2")
        .withCardInHand(1, "Fear of Isolation")
        .withCardOnBattlefield(1, "Grizzly Bears")
        .withLandsOnBattlefield(1, "Island", 2)
        .withActivePlayer(1)
        .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
        .build()

    fun ids(game: ScenarioTestBase.TestGame): Pair<com.wingedsheep.sdk.model.EntityId, com.wingedsheep.sdk.model.EntityId> {
        val spell = game.state.getHand(game.player1Id).first {
            game.state.getEntity(it)?.get<CardComponent>()?.name == "Fear of Isolation"
        }
        val commander = game.state.getBattlefield(game.player1Id).first {
            game.state.getEntity(it)?.get<CardComponent>()?.name == "Grizzly Bears"
        }
        return spell to commander
    }

    fun begin(accept: Boolean): Triple<ScenarioTestBase.TestGame, com.wingedsheep.sdk.model.EntityId, com.wingedsheep.engine.core.ExecutionResult> {
        val game = game()
        val (spell, commander) = ids(game)
        game.state = game.state.copy(format = Format.Commander()).updateEntity(commander) {
            it.with(CommanderComponent(game.player1Id))
        }
        val paused = game.execute(
            CastSpell(game.player1Id, spell, additionalCostPayment = AdditionalCostPayment(bouncedPermanents = listOf(commander)))
        )
        paused.isPaused shouldBe true
        paused.state.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        paused.state.getBattlefield(game.player1Id) shouldContain commander
        paused.state.stack shouldBe emptyList()
        val decision = paused.state.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        val completed = game.submitDecision(YesNoResponse(decision.id, accept))
        return Triple(game, commander, completed)
    }

    test("accepting commander replacement during a spell return cost moves once then puts spell on stack once") {
        val (game, commander, completed) = begin(accept = true)
        game.state.getZone(ZoneKey(game.player1Id, Zone.COMMAND)) shouldContain commander
        game.state.getHand(game.player1Id) shouldNotContain commander
        game.state.stack.size shouldBe 1
        game.state.getEntity(game.state.stack.single())!!.has<SpellOnStackComponent>() shouldBe true
        completed.events.filterIsInstance<ZoneChangeEvent>().count { it.entityId == commander } shouldBe 1
        game.state.pendingDecision.shouldBeNull()
    }

    test("declining commander replacement during a spell return cost moves once to hand then puts spell on stack") {
        val (game, commander, completed) = begin(accept = false)
        game.state.getHand(game.player1Id) shouldContain commander
        game.state.getZone(ZoneKey(game.player1Id, Zone.COMMAND)) shouldNotContain commander
        game.state.stack.size shouldBe 1
        completed.events.filterIsInstance<ZoneChangeEvent>().count { it.entityId == commander } shouldBe 1
        game.state.pendingDecision.shouldBeNull()
    }

    test("a prior life additional cost is paid exactly once across the commander replacement pause") {
        val game = scenario()
            .withPlayers("Player1", "Player2")
            .withCardInHand(1, "Postcost Commander Test Spell")
            .withCardOnBattlefield(1, "Grizzly Bears")
            .withLandsOnBattlefield(1, "Island", 1)
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            .build()
        val spell = game.state.getHand(game.player1Id).single()
        val commander = game.state.getBattlefield(game.player1Id).first {
            game.state.getEntity(it)?.get<CardComponent>()?.name == "Grizzly Bears"
        }
        game.state = game.state.copy(format = Format.Commander()).updateEntity(commander) {
            it.with(CommanderComponent(game.player1Id))
        }

        val paused = game.execute(CastSpell(
            game.player1Id, spell,
            additionalCostPayment = AdditionalCostPayment(bouncedPermanents = listOf(commander))
        ))
        paused.isPaused shouldBe true
        game.state.lifeTotal(game.player1Id) shouldBe 19
        val decision = game.state.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        val completed = game.submitDecision(YesNoResponse(decision.id, true))

        game.state.lifeTotal(game.player1Id) shouldBe 19
        game.state.getZone(ZoneKey(game.player1Id, Zone.COMMAND)) shouldContain commander
        game.state.stack.size shouldBe 1
        completed.events.filterIsInstance<ZoneChangeEvent>().count { it.entityId == commander } shouldBe 1
    }
    }
}
