package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Clash of Elements (Reality Fracture #126) — {1}{U}{R} Instant:
 *   Choose target nonland permanent. Its owner may put it on the top of their library. If they do,
 *   Clash of Elements deals 2 damage to them. If they didn't put the card on top of their library,
 *   they put it on the bottom.
 *
 * The owner — not the caster — answers the "may".
 */
class ClashOfElementsScenarioTest : ScenarioTestBase() {

    private fun game(): ScenarioTestBase.TestGame = scenario().withPlayers()
        .withCardInHand(1, "Clash of Elements")
        .withLandsOnBattlefield(1, "Island", 2)
        .withLandsOnBattlefield(1, "Mountain", 1)
        .withCardOnBattlefield(2, "Grizzly Bears")
        .withCardInLibrary(1, "Island")
        .withCardInLibrary(2, "Forest")
        .withCardInLibrary(2, "Forest")
        .withActivePlayer(1)
        .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
        .build()

    init {
        test("owner puts it on top and takes 2 damage") {
            val game = game()
            val bears = game.findPermanent("Grizzly Bears")!!
            game.castSpell(1, "Clash of Elements", bears).error shouldBe null
            game.resolveStack()

            val decision = game.getPendingDecision()
            decision.shouldBeInstanceOf<YesNoDecision>()
            decision.playerId shouldBe game.player2Id
            game.answerYesNo(true).error shouldBe null
            game.resolveStack()

            game.isOnBattlefield("Grizzly Bears") shouldBe false
            game.state.getLibrary(game.player2Id).first() shouldBe bears
            game.getLifeTotal(2) shouldBe 18
            game.getLifeTotal(1) shouldBe 20
        }

        test("owner declines: it goes to the bottom and no damage is dealt") {
            val game = game()
            val bears = game.findPermanent("Grizzly Bears")!!
            game.castSpell(1, "Clash of Elements", bears).error shouldBe null
            game.resolveStack()

            game.getPendingDecision()!!.playerId shouldBe game.player2Id
            game.answerYesNo(false).error shouldBe null
            game.resolveStack()

            game.isOnBattlefield("Grizzly Bears") shouldBe false
            game.state.getLibrary(game.player2Id).last() shouldBe bears
            game.getLifeTotal(2) shouldBe 20
        }

        test("targeting your own permanent: you are the one asked and damaged") {
            val game = scenario().withPlayers()
                .withCardInHand(1, "Clash of Elements")
                .withLandsOnBattlefield(1, "Island", 2)
                .withLandsOnBattlefield(1, "Mountain", 1)
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(2, "Forest")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()
            val bears = game.findPermanent("Grizzly Bears")!!
            game.castSpell(1, "Clash of Elements", bears).error shouldBe null
            game.resolveStack()

            game.getPendingDecision()!!.playerId shouldBe game.player1Id
            game.answerYesNo(true).error shouldBe null
            game.resolveStack()

            game.state.getLibrary(game.player1Id).first() shouldBe bears
            game.getLifeTotal(1) shouldBe 18
            game.getLifeTotal(2) shouldBe 20
        }
    }
}
