package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe
import io.kotest.matchers.nulls.shouldNotBeNull

class RestoreWithEmpathyScenarioTest : ScenarioTestBase() {
    init {
        for (card in listOf("Forest", "Grizzly Bears", "Jace Beleren")) {
            test("returns $card and gains four life") {
                val game = scenario().withPlayers()
                    .withCardInHand(1, "Restore with Empathy")
                    .withCardInGraveyard(1, card)
                    .withLandsOnBattlefield(1, "Forest", 3)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
                game.castSpellTargetingGraveyardCard(1, "Restore with Empathy", 1, card).error shouldBe null
                game.resolveStack()
                game.isInHand(1, card) shouldBe true
                game.getLifeTotal(1) shouldBe 24
            }
        }
        test("cannot target an instant or an opponent's permanent card") {
            val game = scenario().withPlayers()
                .withCardInHand(1, "Restore with Empathy")
                .withCardInGraveyard(1, "Giant Growth")
                .withCardInGraveyard(2, "Forest")
                .withLandsOnBattlefield(1, "Forest", 3)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            game.castSpellTargetingGraveyardCard(1, "Restore with Empathy", 1, "Giant Growth").error.shouldNotBeNull()
            game.castSpellTargetingGraveyardCard(1, "Restore with Empathy", 2, "Forest").error.shouldNotBeNull()
            game.getLifeTotal(1) shouldBe 20
        }
    }
}
