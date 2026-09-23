package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.AbilityCost
import io.kotest.matchers.shouldBe

/**
 * Gideon the Oathless (FRA #230) — "Whenever a creature an opponent controls enters, Gideon deals
 * 1 damage to that player. Whenever an opponent activates a loyalty ability, Gideon deals 1 damage
 * to that player."
 *
 * Pins the loyalty flag on `AbilityActivatedEvent`: an opponent's loyalty ability fires it, the
 * controller's own does not.
 */
class GideonTheOathlessScenarioTest : ScenarioTestBase() {
    init {
        val ajaniPlusOne = cardRegistry.getCard("Ajani Goldmane")!!.script.activatedAbilities
            .single { (it.cost as? AbilityCost.Loyalty)?.change == 1 }.id

        fun game(ajaniController: Int) = scenario().withPlayers()
            .withCardOnBattlefield(1, "Gideon the Oathless")
            .withCardOnBattlefield(ajaniController, "Ajani Goldmane")
            .withCardInHand(2, "Grizzly Bears")
            .withLandsOnBattlefield(2, "Forest", 2)
            .withCardInLibrary(1, "Swamp")
            .withCardInLibrary(2, "Forest")
            .withActivePlayer(ajaniController)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            .build()

        test("an opponent's loyalty ability costs them 1 life") {
            val game = game(ajaniController = 2)
            val ajani = game.findPermanent("Ajani Goldmane")!!
            game.execute(ActivateAbility(game.player2Id, ajani, ajaniPlusOne)).error shouldBe null
            game.resolveStack()
            game.getLifeTotal(2) shouldBe 21 // +2 from Ajani, -1 from Gideon
            game.getLifeTotal(1) shouldBe 20
        }

        test("your own loyalty ability doesn't trigger Gideon") {
            val game = game(ajaniController = 1)
            val ajani = game.findPermanent("Ajani Goldmane")!!
            game.execute(ActivateAbility(game.player1Id, ajani, ajaniPlusOne)).error shouldBe null
            game.resolveStack()
            game.getLifeTotal(1) shouldBe 22
            game.getLifeTotal(2) shouldBe 20
        }

        test("a creature entering under an opponent's control deals 1 damage to them") {
            val game = game(ajaniController = 2)
            game.castSpell(2, "Grizzly Bears").error shouldBe null
            game.resolveStack()
            game.getLifeTotal(2) shouldBe 19
            game.getLifeTotal(1) shouldBe 20
        }
    }
}
