package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Dashing Bloodsucker (DSK #90) — {3}{B} 2/5 Creature — Vampire Warrior.
 *
 * "Eerie — Whenever an enchantment you control enters and whenever you fully unlock a Room, this
 *  creature gets +2/+0 and gains lifelink until end of turn."
 *
 * Eerie is two triggered abilities; the enchantment-enters half is exercised here. Each fire
 * pumps the source +2/+0 and grants lifelink until end of turn.
 */
class DashingBloodsuckerScenarioTest : ScenarioTestBase() {

    init {
        context("Dashing Bloodsucker — Eerie (enchantment enters)") {

            test("an enchantment you control entering pumps it +2/+0 and grants lifelink") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Dashing Bloodsucker")
                    .withCardInHand(1, "Test Enchantment") // {1}{W}
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bloodsucker = game.findPermanent("Dashing Bloodsucker")!!
                withClue("Base stats before any Eerie trigger") {
                    game.state.projectedState.getPower(bloodsucker) shouldBe 2
                    game.state.projectedState.getToughness(bloodsucker) shouldBe 5
                    game.state.projectedState.hasKeyword(bloodsucker, Keyword.LIFELINK) shouldBe false
                }

                val cast = game.castSpell(1, "Test Enchantment")
                withClue("Casting Test Enchantment should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                withClue("Eerie pumped Dashing Bloodsucker +2/+0 and granted lifelink") {
                    game.state.projectedState.getPower(bloodsucker) shouldBe 4
                    game.state.projectedState.getToughness(bloodsucker) shouldBe 5
                    game.state.projectedState.hasKeyword(bloodsucker, Keyword.LIFELINK) shouldBe true
                }
            }

            test("an opponent's enchantment entering does NOT trigger Eerie") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Dashing Bloodsucker")
                    .withCardInHand(2, "Test Enchantment") // controlled by the opponent
                    .withLandsOnBattlefield(2, "Plains", 2)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bloodsucker = game.findPermanent("Dashing Bloodsucker")!!

                val cast = game.castSpell(2, "Test Enchantment")
                withClue("Opponent casting Test Enchantment should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                withClue("No Eerie trigger — the enchantment isn't the Bloodsucker controller's") {
                    game.hasPendingDecision() shouldBe false
                    game.state.projectedState.getPower(bloodsucker) shouldBe 2
                    game.state.projectedState.hasKeyword(bloodsucker, Keyword.LIFELINK) shouldBe false
                }
            }
        }
    }
}
