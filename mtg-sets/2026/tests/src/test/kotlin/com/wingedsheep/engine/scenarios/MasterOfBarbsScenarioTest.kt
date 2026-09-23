package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Master of Barbs (FRA #88) — noncombat damage to an opponent pumps your creatures +1/+0.
 */
class MasterOfBarbsScenarioTest : ScenarioTestBase() {
    init {
        fun game() = scenario().withPlayers()
            .withCardOnBattlefield(1, "Master of Barbs")
            .withCardOnBattlefield(1, "Grizzly Bears")
            .withCardOnBattlefield(2, "Hill Giant")
            .withCardInHand(1, "Shock")
            .withLandsOnBattlefield(1, "Mountain", 1)
            .withCardInLibrary(1, "Mountain")
            .withCardInLibrary(2, "Mountain")
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            .build()

        test("noncombat damage to an opponent gives your creatures +1/+0") {
            val game = game()
            val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()
            val giant = game.findPermanent("Hill Giant").shouldNotBeNull()

            game.castSpellTargetingPlayer(1, "Shock", 2).error shouldBe null
            game.resolveStack()

            game.state.projectedState.getPower(bears) shouldBe 3
            game.state.projectedState.getToughness(bears) shouldBe 2
            game.state.projectedState.getPower(game.findPermanent("Master of Barbs")!!) shouldBe 3
            game.state.projectedState.getPower(giant) shouldBe 3
        }

        test("damage to a creature doesn't trigger it") {
            val game = game()
            val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()
            val giant = game.findPermanent("Hill Giant").shouldNotBeNull()

            game.castSpell(1, "Shock", giant).error shouldBe null
            game.resolveStack()

            game.state.projectedState.getPower(bears) shouldBe 2
        }
    }
}
