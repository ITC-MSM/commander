package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

class WrathOfTheBloodmaneScenarioTest : ScenarioTestBase() {
    init {
        test("costs {1} less with a legendary creature and deals 4 damage") {
            val game = scenario().withPlayers()
                .withCardInHand(1, "Wrath of the Bloodmane")
                .withCardOnBattlefield(1, "Yargle, Glutton of Urborg")
                .withCardOnBattlefield(2, "Hill Giant")
                .withLandsOnBattlefield(1, "Mountain", 2)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            game.castSpell(1, "Wrath of the Bloodmane", game.findPermanent("Hill Giant")!!).error shouldBe null
            game.resolveStack()
            game.isInGraveyard(2, "Hill Giant") shouldBe true
        }

        test("a nonlegendary creature does not reduce the cost") {
            val game = scenario().withPlayers()
                .withCardInHand(1, "Wrath of the Bloodmane")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withCardOnBattlefield(2, "Hill Giant")
                .withLandsOnBattlefield(1, "Mountain", 2)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            game.castSpell(1, "Wrath of the Bloodmane", game.findPermanent("Hill Giant")!!).error.shouldNotBeNull()
        }
    }
}
