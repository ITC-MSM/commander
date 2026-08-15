package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Cult Healer (DSK #2) — {2}{W} 3/3 Creature — Human Doctor.
 *
 * "Eerie — Whenever an enchantment you control enters and whenever you fully unlock a Room,
 *  this creature gains lifelink until end of turn."
 *
 * Exercises the enchantment-enters half of the Eerie ability granting lifelink to itself.
 */
class CultHealerScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    init {
        context("Cult Healer — Eerie grants lifelink") {

            test("has no lifelink until an enchantment you control enters") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Cult Healer")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val healer = game.findPermanent("Cult Healer")!!
                withClue("No enchantment has entered — no lifelink yet") {
                    projector.project(game.state).hasKeyword(healer, Keyword.LIFELINK) shouldBe false
                }
            }

            test("an enchantment you control entering grants lifelink") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Cult Healer")
                    .withCardInHand(1, "Test Enchantment") // {1}{W}
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpell(1, "Test Enchantment")
                withClue("Casting Test Enchantment should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                val healer = game.findPermanent("Cult Healer")!!
                withClue("Eerie fired — Cult Healer has lifelink") {
                    projector.project(game.state).hasKeyword(healer, Keyword.LIFELINK) shouldBe true
                }
            }

            test("an opponent's enchantment entering does NOT grant lifelink") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Cult Healer")
                    .withCardInHand(2, "Test Enchantment") // controlled by the opponent
                    .withLandsOnBattlefield(2, "Plains", 2)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(2, "Test Enchantment").error shouldBe null
                game.resolveStack()

                val healer = game.findPermanent("Cult Healer")!!
                withClue("Enchantment isn't yours — no lifelink") {
                    projector.project(game.state).hasKeyword(healer, Keyword.LIFELINK) shouldBe false
                }
            }
        }
    }
}
