package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Attack-in-the-Box (DSK #242) — {3} 2/4 Artifact Creature — Toy.
 *
 * "Whenever this creature attacks, you may have it get +4/+0 until end of turn. If you do,
 *  sacrifice it at the beginning of the next end step."
 *
 * The attack trigger's whole body is gated by one "you may"; choosing yes pumps it +4/+0 and arms
 * a delayed trigger that sacrifices it at the next end step. Declining does neither.
 */
class AttackInTheBoxScenarioTest : ScenarioTestBase() {

    init {
        context("Attack-in-the-Box — attack may-pump with delayed sacrifice") {

            test("choosing to pump grants +4/+0 and sacrifices it at the next end step") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Attack-in-the-Box", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
                    .build()

                val box = game.findPermanent("Attack-in-the-Box")!!
                withClue("Base power before the attack trigger") {
                    game.state.projectedState.getPower(box) shouldBe 2
                }

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Attack-in-the-Box" to 2)).error shouldBe null
                game.resolveStack()

                withClue("The 'you may' decision should be pending after attacking") {
                    game.hasPendingDecision() shouldBe true
                }
                game.answerYesNo(true).error shouldBe null
                game.resolveStack()

                withClue("It got +4/+0 until end of turn") {
                    game.state.projectedState.getPower(box) shouldBe 6
                    game.state.projectedState.getToughness(box) shouldBe 4
                }

                // Advance to the end step; the delayed sacrifice fires.
                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("Sacrificed at the beginning of the next end step") {
                    game.isOnBattlefield("Attack-in-the-Box") shouldBe false
                    game.isInGraveyard(1, "Attack-in-the-Box") shouldBe true
                }
            }

            test("declining leaves it unpumped and on the battlefield") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Attack-in-the-Box", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
                    .build()

                val box = game.findPermanent("Attack-in-the-Box")!!

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Attack-in-the-Box" to 2)).error shouldBe null
                game.resolveStack()

                game.answerYesNo(false).error shouldBe null
                game.resolveStack()

                withClue("No pump when declining") {
                    game.state.projectedState.getPower(box) shouldBe 2
                }

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("Not sacrificed — the 'if you do' never armed the delayed trigger") {
                    game.isOnBattlefield("Attack-in-the-Box") shouldBe true
                }
            }
        }
    }
}
