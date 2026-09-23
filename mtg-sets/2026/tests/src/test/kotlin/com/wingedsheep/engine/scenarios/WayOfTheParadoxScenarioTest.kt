package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.PlayLand
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.AbilityCost
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Way of the Paradox (FRA #267) — "Whenever you activate a loyalty ability, you gain 1 life. You may
 * play an additional land this turn."
 *
 * Pins `Triggers.YouActivateLoyaltyAbility`: a loyalty ability fires it, a non-loyalty activated
 * ability does not.
 */
class WayOfTheParadoxScenarioTest : ScenarioTestBase() {
    init {
        test("a loyalty ability gains 1 life and grants an extra land drop") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Way of the Paradox")
                .withCardOnBattlefield(1, "Ajani Goldmane")
                .withCardInHand(1, "Forest")
                .withCardInHand(1, "Forest")
                .withCardInHand(1, "Forest")
                .withCardInLibrary(1, "Forest")
                .withCardInLibrary(2, "Forest")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val ajani = game.findPermanent("Ajani Goldmane")!!
            val plusOne = cardRegistry.getCard("Ajani Goldmane")!!.script.activatedAbilities
                .single { (it.cost as? AbilityCost.Loyalty)?.change == 1 }.id
            game.execute(ActivateAbility(game.player1Id, ajani, plusOne)).error shouldBe null
            game.resolveStack()
            game.getLifeTotal(1) shouldBe 23 // +1 Way, +2 Ajani

            val forests = game.findCardsInHand(1, "Forest")
            game.execute(PlayLand(game.player1Id, forests[0])).error shouldBe null
            game.execute(PlayLand(game.player1Id, forests[1])).error shouldBe null
            withClue("only one additional land drop was granted") {
                game.execute(PlayLand(game.player1Id, forests[2])).error shouldNotBe null
            }
        }

        test("a non-loyalty activated ability doesn't trigger it") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Way of the Paradox")
                .withCardOnBattlefield(1, "Surveillance Phantasm")
                .withLandsOnBattlefield(1, "Island", 4)
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(2, "Island")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val phantasm = game.findPermanent("Surveillance Phantasm")!!
            val surveil = cardRegistry.getCard("Surveillance Phantasm")!!.script.activatedAbilities.single().id
            game.execute(ActivateAbility(game.player1Id, phantasm, surveil)).error shouldBe null
            game.resolveStack()
            if (game.hasPendingDecision()) game.skipSelection()
            game.resolveStack()
            game.getLifeTotal(1) shouldBe 20
        }
    }
}
