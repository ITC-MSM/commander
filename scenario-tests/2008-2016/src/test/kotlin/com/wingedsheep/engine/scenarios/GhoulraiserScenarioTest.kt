package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class GhoulraiserScenarioTest : ScenarioTestBase() {
    init {
        test("ETB returns a random Zombie from graveyard to hand") {
            val game = scenario()
                .withPlayers("P1", "P2")
                .withCardInGraveyard(1, "Diregraf Ghoul")
                .withCardInHand(1, "Ghoulraiser")
                .withLandsOnBattlefield(1, "Swamp", 3)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val handBefore = game.state.getZone(game.player1Id, Zone.HAND).size
            game.castSpell(1, "Ghoulraiser").error shouldBe null
            game.resolveStack()
            if (game.state.stack.isNotEmpty()) game.resolveStack()

            game.findPermanent("Ghoulraiser") shouldNotBe null
            // put(+1 via cast from hand accounted) cast(-1) return(+1) => handBefore
            // handBefore includes Ghoulraiser; after cast+ETB return: handBefore - 1 + 1 = handBefore
            game.state.getZone(game.player1Id, Zone.HAND).size shouldBe handBefore
            game.state.getZone(game.player1Id, Zone.GRAVEYARD).isEmpty() shouldBe true
        }
    }
}
