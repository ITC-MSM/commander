package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import io.kotest.matchers.shouldBe

/**
 * Hare Apparent — "{1}{W} 2/2. When this creature enters, create a number of 1/1 white Rabbit
 * creature tokens equal to the number of OTHER creatures you control named Hare Apparent."
 *
 * Proves the `excludeSelf` name-count: the entering copy never counts itself, so the token count
 * is the number of pre-existing Hare Apparents you control.
 */
class HareApparentScenarioTest : ScenarioTestBase() {

    init {
        test("ETB makes one Rabbit per OTHER Hare Apparent you control") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Hare Apparent")
                .withCardOnBattlefield(1, "Hare Apparent")
                .withLandsOnBattlefield(1, "Plains", 2)
                .withCardInHand(1, "Hare Apparent")
                .withCardInLibrary(1, "Plains")
                .build()

            game.castSpell(1, "Hare Apparent").error shouldBe null
            game.resolveStack()

            // Two other Hare Apparents already in play → two Rabbit tokens.
            game.findPermanents("Rabbit Token").size shouldBe 2
            game.findPermanents("Hare Apparent").size shouldBe 3
        }

        test("ETB with no other Hare Apparent makes no tokens") {
            val game = scenario()
                .withPlayers()
                .withLandsOnBattlefield(1, "Plains", 2)
                .withCardInHand(1, "Hare Apparent")
                .withCardInLibrary(1, "Plains")
                .build()

            game.castSpell(1, "Hare Apparent").error shouldBe null
            game.resolveStack()

            game.findPermanents("Rabbit Token").size shouldBe 0
        }
    }
}
