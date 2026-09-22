package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe
import io.kotest.matchers.nulls.shouldNotBeNull

class RefuteDestinyScenarioTest : ScenarioTestBase() {
    init {
        for ((victim, allowed) in listOf("Grizzly Bears" to true, "Goblin Piker" to false)) {
            test("target restriction for $victim") {
                val game = scenario().withPlayers()
                    .withCardInHand(1, "Refute Destiny")
                    .withCardOnBattlefield(2, victim)
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
                val result = game.castSpell(1, "Refute Destiny", game.findPermanent(victim)!!)
                if (allowed) {
                    result.error shouldBe null
                    game.resolveStack()
                    game.isInExile(2, victim) shouldBe true
                } else {
                    result.error.shouldNotBeNull()
                }
            }
        }
    }
}
