package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Kalastria Healer — the first *rally* card in the corpus.
 *
 * Rally is an ability word, not a keyword: "Whenever this creature **or another** Ally you control
 * enters" is an ANY-bound enters trigger filtered to Allies you control. The binding is the whole
 * mechanic — under `OTHER` the Healer's own arrival would silently not fire it, which is the half
 * of rally that is easiest to get wrong and invisible on the card text.
 */
class KalastriaHealerScenarioTest : ScenarioTestBase() {

    init {
        context("Kalastria Healer") {

            test("rally fires on the Healer's own arrival") {
                val game = scenario()
                    .withPlayers()
                    .withCardInHand(1, "Kalastria Healer")
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .build()

                val startingOpponentLife = game.getLifeTotal(2)
                val startingOwnLife = game.getLifeTotal(1)

                game.castSpell(1, "Kalastria Healer").error shouldBe null
                game.resolveStack()

                withClue("the Healer is an Ally, so its own entry triggers rally") {
                    game.getLifeTotal(2) shouldBe startingOpponentLife - 1
                    game.getLifeTotal(1) shouldBe startingOwnLife + 1
                }
            }

            test("rally fires again when another Ally enters") {
                val game = scenario()
                    .withPlayers()
                    .withCardOnBattlefield(1, "Kalastria Healer")
                    .withCardInHand(1, "Cliffside Lookout")
                    .withLandsOnBattlefield(1, "Plains", 1)
                    .build()

                val startingOpponentLife = game.getLifeTotal(2)
                val startingOwnLife = game.getLifeTotal(1)

                game.castSpell(1, "Cliffside Lookout").error shouldBe null
                game.resolveStack()

                withClue("Cliffside Lookout is a Kor Scout Ally, so it re-triggers the Healer") {
                    game.getLifeTotal(2) shouldBe startingOpponentLife - 1
                    game.getLifeTotal(1) shouldBe startingOwnLife + 1
                }
            }

            test("a non-Ally creature entering does not trigger rally") {
                val game = scenario()
                    .withPlayers()
                    .withCardOnBattlefield(1, "Kalastria Healer")
                    .withCardInHand(1, "Cloud Manta")
                    .withLandsOnBattlefield(1, "Island", 4)
                    .build()

                val startingOpponentLife = game.getLifeTotal(2)
                val startingOwnLife = game.getLifeTotal(1)

                game.castSpell(1, "Cloud Manta").error shouldBe null
                game.resolveStack()

                withClue("Cloud Manta is a Fish, not an Ally") {
                    game.getLifeTotal(2) shouldBe startingOpponentLife
                    game.getLifeTotal(1) shouldBe startingOwnLife
                }
            }
        }
    }
}
