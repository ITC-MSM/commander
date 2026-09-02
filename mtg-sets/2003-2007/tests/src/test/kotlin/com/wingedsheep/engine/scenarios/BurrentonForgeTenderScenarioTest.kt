package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.BurrentonForgeTender
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Burrenton Forge-Tender (LRW #7) — "Protection from red. Sacrifice this creature: Prevent all
 * damage a red source of your choice would deal this turn."
 *
 * Player 1 attacks with two creatures — a red Goblin Guide (2/1) and a white Savannah Lions (1/1) —
 * and player 2 sacrifices the Forge-Tender in the declare-blockers step. Two creatures rather than
 * one is the point: a shield that ignored its `ChosenSourceMatching` colour filter would stop all
 * three damage, so the Lions' 1 still landing is the assertion that the filter is doing work.
 *
 * The 2017-11-17 ruling that the ability doesn't target — the source is chosen *as it resolves*, and
 * it can be activated with nothing to choose — falls out of modelling the colour clause as an
 * eligibility filter on a resolution-time choice rather than as a target requirement. The first
 * test reads that choice's option list directly.
 */
class BurrentonForgeTenderScenarioTest : FunSpec({

    val sacAbility = BurrentonForgeTender.activatedAbilities.single().id

    /**
     * Player 1 attacks with a red Goblin Guide (2/1) and a white Savannah Lions (1/1);
     * player 2 takes no blocks and holds priority in the declare-blockers step with an untapped
     * Forge-Tender. Returns the Forge-Tender, the Goblin, and the Lions.
     */
    fun attackingBoard(): Triple<GameTestDriver, EntityId, Pair<EntityId, EntityId>> {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + BurrentonForgeTender)
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true, startingPlayer = 0)

        val goblin = d.putCreatureOnBattlefield(d.player1, "Goblin Guide")
        val lions = d.putCreatureOnBattlefield(d.player1, "Savannah Lions")
        d.removeSummoningSickness(goblin)
        d.removeSummoningSickness(lions)
        val tender = d.putCreatureOnBattlefield(d.player2, "Burrenton Forge-Tender")

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(d.player1, listOf(goblin, lions), d.player2).error shouldBe null
        d.passPriorityUntil(Step.DECLARE_BLOCKERS)
        d.declareNoBlockers(d.player2).error shouldBe null

        return Triple(d, tender, goblin to lions)
    }

    /** Sacrifice the Forge-Tender and pause on the source choice. */
    fun GameTestDriver.sacrificeForgeTender(tender: EntityId) {
        submit(ActivateAbility(player2, tender, sacAbility)).error shouldBe null
        var guard = 0
        while (guard++ < 10 && state.pendingDecision == null && state.stack.isNotEmpty()) bothPass()
    }

    test("only red sources are offered as the choice — and the ability never targeted") {
        val (d, tender, creatures) = attackingBoard()
        val (goblin, lions) = creatures

        d.sacrificeForgeTender(tender)

        val decision = d.state.pendingDecision
        withClue("the ability pauses on resolution to choose a source: $decision") {
            (decision is SelectCardsDecision) shouldBe true
        }
        val options = (decision as SelectCardsDecision).options
        withClue("the red Goblin Guide qualifies") {
            (goblin in options) shouldBe true
        }
        withClue("the white Savannah Lions is not a red source, so it is never offered") {
            (lions in options) shouldBe false
        }
    }

    test("the chosen red source deals no combat damage; the white attacker still connects") {
        val (d, tender, creatures) = attackingBoard()
        val (goblin, _) = creatures

        d.sacrificeForgeTender(tender)
        d.submitCardSelection(d.player2, listOf(goblin)).error shouldBe null

        d.passPriorityUntil(Step.END_COMBAT)

        withClue("the Goblin's 2 is prevented; the Lions' 1 is not") {
            d.getLifeTotal(d.player2) shouldBe 19
        }
    }

    test("without the sacrifice, all three damage lands") {
        val (d, _, _) = attackingBoard()

        d.passPriorityUntil(Step.END_COMBAT)

        withClue("the control case — nothing is preventing anything") {
            d.getLifeTotal(d.player2) shouldBe 17
        }
    }
})
