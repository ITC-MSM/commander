package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Odric, Blood-Cursed (VOW #243) — {1}{R}{W} Legendary Creature — Vampire Soldier 3/3.
 *
 *   When Odric enters, create X Blood tokens, where X is the number of abilities from among
 *   flying, first strike, double strike, deathtouch, haste, hexproof, indestructible, lifelink,
 *   menace, reach, trample, and vigilance found among creatures you control. (Count each ability
 *   only once.)
 *
 * The ruling is the whole test: abilities are counted, not creatures. Two fliers make one Blood;
 * one creature with two listed keywords makes two.
 */
class OdricBloodCursedScenarioTest : ScenarioTestBase() {

    init {
        context("Odric, Blood-Cursed") {

            test("no keyworded creatures — no Blood tokens") {
                val game = odricGame().build()
                game.castSpell(1, "Odric, Blood-Cursed").error shouldBe null
                game.resolveStack()

                withClue("Odric has none of the listed abilities itself") {
                    game.findPermanents("Blood").size shouldBe 0
                }
            }

            test("each listed ability counts once, however many creatures carry it") {
                // Serra Angel: flying + vigilance. Wind Drake: flying (already counted).
                val game = odricGame()
                    .withCardOnBattlefield(1, "Serra Angel")
                    .withCardOnBattlefield(1, "Wind Drake")
                    .build()
                game.castSpell(1, "Odric, Blood-Cursed").error shouldBe null
                game.resolveStack()

                withClue("flying + vigilance = 2, the second flier adds nothing") {
                    game.findPermanents("Blood").size shouldBe 2
                }
            }

            test("opponents' creatures are not counted") {
                val game = odricGame()
                    .withCardOnBattlefield(2, "Serra Angel")
                    .build()
                game.castSpell(1, "Odric, Blood-Cursed").error shouldBe null
                game.resolveStack()

                game.findPermanents("Blood").size shouldBe 0
            }
        }
    }

    private fun odricGame(): ScenarioBuilder = scenario()
        .withPlayers("Player1", "Player2")
        .withCardInHand(1, "Odric, Blood-Cursed")
        .withLandsOnBattlefield(1, "Mountain", 2)
        .withLandsOnBattlefield(1, "Plains", 1)
        .withActivePlayer(1)
        .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
}
