package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Kavaron Harrier (EOE #139, {R}, 2/1 Artifact Creature — Robot Soldier).
 *
 * Oracle: "Whenever this creature attacks, you may pay {2}. If you do, create a 2/2 colorless
 * Robot artifact creature token that's tapped and attacking. Sacrifice that token at end of combat."
 *
 * Regression: the original card def created the Robot token with `exileAtStep = Step.END_COMBAT`,
 * routing it to *exile* at end of combat. Per oracle the token must be *sacrificed* (battlefield →
 * graveyard) so dies / sacrifice triggers fire. SBA 704.5d removes tokens from any non-battlefield
 * zone, so the final board can't distinguish exile from graveyard. We use a witness that ONLY
 * triggers on a graveyard zone-change: South Wind Avatar (TMT) — "Whenever another creature you
 * control dies, you gain life equal to its toughness." A 2/2 Robot Token going to graveyard makes
 * Alice gain 2; exile leaves her at 20. Alice's life total is the clean tell.
 */
class KavaronHarrierTest : ScenarioTestBase() {

    init {
        context("Kavaron Harrier") {

            test("attack-trigger token is sacrificed (not exiled) at end of combat — witnessed by South Wind Avatar") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Kavaron Harrier", tapped = false, summoningSickness = false)
                    .withCardOnBattlefield(1, "South Wind Avatar", tapped = false, summoningSickness = false)
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withLifeTotal(1, 20)
                    .withLifeTotal(2, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                // Move into combat and attack Bob with Kavaron Harrier.
                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                val attack = game.declareAttackers(mapOf("Kavaron Harrier" to 2))
                withClue("Declaring Kavaron Harrier as attacker should succeed: ${attack.error}") {
                    attack.error shouldBe null
                }

                // The "Whenever this creature attacks, you may pay {2}" trigger goes on the stack
                // and pauses for the may-pay yes/no.
                game.resolveStack()
                withClue("Attack trigger should be waiting for the may-pay yes/no") {
                    game.hasPendingDecision() shouldBe true
                }
                game.answerYesNo(true)
                game.submitManaSourcesAutoPay()
                game.resolveStack()

                withClue("Paying {2} should have created the 2/2 Robot token") {
                    game.findPermanents("Robot Token").size shouldBe 1
                }

                // Auto-advance through declare-blockers (no blockers), combat damage, and end of
                // combat (where the delayed trigger sacrifices or exiles the token), landing in
                // postcombat main. passUntilPhase auto-resolves the AssignDamage / CombatResolution
                // decisions and the Avatar's life-gain triggers (no choices on those).
                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)

                // South Wind Avatar's "Whenever another creature you control dies" only fires on
                // a battlefield→graveyard zone change, never on battlefield→exile. Sacrificing the
                // 2/2 Robot Token makes Alice gain 2 life. Exiling it leaves her at 20.
                withClue(
                    "Oracle: 'Sacrifice that token at end of combat'. South Wind Avatar's dies-trigger " +
                        "should fire for the 2/2 Robot Token, gaining Alice 2 life (→ 22). Bug: card uses " +
                        "exileAtStep instead of sacrificeAtStep, the token is exiled, the witness never " +
                        "fires, and Alice stays at 20."
                ) {
                    game.getLifeTotal(1) shouldBe 22
                }
            }
        }
    }
}
