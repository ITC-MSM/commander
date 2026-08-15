package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Karakyk Guardian (TDM #198) — {3}{G}{U}{R} Dragon, 6/5.
 *
 * Flying, vigilance, trample.
 * "This creature has hexproof if it hasn't dealt damage yet."
 *
 * Exercises the [com.wingedsheep.sdk.scripting.ConditionalStaticAbility] gating a self-targeted
 * hexproof grant behind `Not(SourceHasDealtDamage)`. The condition is read from the
 * `HasDealtDamageComponent` the combat-damage manager stamps onto a creature once it deals
 * damage — so the projected hexproof keyword must be present before combat and absent after.
 */
class KarakykGuardianScenarioTest : ScenarioTestBase() {

    init {
        context("Karakyk Guardian conditional hexproof") {

            test("has flying, vigilance, trample, and hexproof before it has dealt damage") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Karakyk Guardian", tapped = false, summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val guardian = game.findPermanent("Karakyk Guardian")!!
                val projected = game.state.projectedState

                withClue("Flying is an innate keyword") {
                    projected.hasKeyword(guardian, Keyword.FLYING) shouldBe true
                }
                withClue("Vigilance is an innate keyword") {
                    projected.hasKeyword(guardian, Keyword.VIGILANCE) shouldBe true
                }
                withClue("Trample is an innate keyword") {
                    projected.hasKeyword(guardian, Keyword.TRAMPLE) shouldBe true
                }
                withClue("Hexproof is granted while it hasn't dealt damage yet") {
                    projected.hasKeyword(guardian, Keyword.HEXPROOF) shouldBe true
                }
            }

            test("loses hexproof after dealing combat damage") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Karakyk Guardian", tapped = false, summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                val guardian = game.findPermanent("Karakyk Guardian")!!

                withClue("Hexproof is present before combat damage") {
                    game.state.projectedState.hasKeyword(guardian, Keyword.HEXPROOF) shouldBe true
                }

                val attack = game.declareAttackers(mapOf("Karakyk Guardian" to 2))
                withClue("Karakyk Guardian should attack the opponent: ${attack.error}") {
                    attack.error shouldBe null
                }

                // No blockers, then resolve combat damage (auto-resolved by passUntilPhase).
                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)

                withClue("Player2 took 6 combat damage") {
                    game.getLifeTotal(2) shouldBe 14
                }
                withClue("Hexproof drops once Karakyk Guardian has dealt damage") {
                    game.state.projectedState.hasKeyword(guardian, Keyword.HEXPROOF) shouldBe false
                }
            }
        }
    }
}
