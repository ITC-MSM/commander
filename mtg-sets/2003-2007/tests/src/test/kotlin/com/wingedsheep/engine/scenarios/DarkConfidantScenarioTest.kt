package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Dark Confidant (canonical printing in Ravnica: City of Guilds,
 * reprinted in Final Fantasy).
 *
 * Dark Confidant — {1}{B} Creature — Human Wizard 2/1.
 *   "At the beginning of your upkeep, reveal the top card of your library and put that
 *    card into your hand. You lose life equal to its mana value."
 */
class DarkConfidantScenarioTest : ScenarioTestBase() {

    init {
        test("upkeep moves the top card to hand and loses life equal to its mana value") {
            val game = scenario()
                .withPlayers()
                .withActivePlayer(1)
                .inPhase(Phase.BEGINNING, Step.UNTAP)
                .withCardOnBattlefield(1, "Dark Confidant")
                .withCardInLibrary(1, "Laughing Mad") // mana value 3 ({2}{R})
                .build()

            game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
            game.resolveStack()

            withClue("the revealed top card is now in hand") {
                game.isInHand(1, "Laughing Mad") shouldBe true
            }
            withClue("controller loses life equal to the revealed card's mana value (3)") {
                game.getLifeTotal(1) shouldBe 17
            }
        }
    }
}
