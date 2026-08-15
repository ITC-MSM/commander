package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Omenpath Journey (BIG #18).
 *
 * Omenpath Journey — {3}{G} Enchantment.
 *   "When this enchantment enters, search your library for up to five land cards that have
 *    different names, exile them, then shuffle.
 *    At the beginning of your end step, choose a card at random exiled with this enchantment
 *    and put it onto the battlefield tapped."
 */
class OmenpathJourneyScenarioTest : ScenarioTestBase() {

    init {
        test("ETB exiles up to five different-named lands linked to the source") {
            val game = scenario()
                .withPlayers()
                .withRngSeed(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .withCardInHand(1, "Omenpath Journey")
                .withLandsOnBattlefield(1, "Forest", 4)
                .withCardInLibrary(1, "Plains")
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(1, "Swamp")
                .withCardInLibrary(1, "Mountain")
                .withCardInLibrary(1, "Forest")
                .build()

            game.castSpell(1, "Omenpath Journey").error shouldBe null
            game.resolveStack() // resolve the enchantment; ETB trigger goes on the stack
            game.resolveStack() // resolve the ETB trigger → pauses at the search selection

            // Choose three distinct lands to exile.
            val plains = game.findCardsInLibrary(1, "Plains")
            val island = game.findCardsInLibrary(1, "Island")
            val swamp = game.findCardsInLibrary(1, "Swamp")
            withClue("the search decision is offered") { game.hasPendingDecision() shouldBe true }
            game.selectCards(plains + island + swamp).error shouldBe null
            game.resolveStack()

            withClue("three lands are now exiled (linked to Omenpath Journey)") {
                game.state.getExile(game.player1Id).size shouldBeGreaterThanOrEqual 3
            }
            withClue("Omenpath Journey is on the battlefield") {
                game.isOnBattlefield("Omenpath Journey") shouldBe true
            }
        }

        test("at the beginning of your end step a random exiled card enters tapped") {
            val game = scenario()
                .withPlayers()
                .withRngSeed(7)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .withCardInHand(1, "Omenpath Journey")
                .withLandsOnBattlefield(1, "Forest", 4)
                .withCardInLibrary(1, "Plains")
                .withCardInLibrary(1, "Island")
                .build()

            game.castSpell(1, "Omenpath Journey").error shouldBe null
            game.resolveStack()
            game.resolveStack()

            val plains = game.findCardsInLibrary(1, "Plains")
            val island = game.findCardsInLibrary(1, "Island")
            game.selectCards(plains + island).error shouldBe null
            game.resolveStack()

            val exiledBefore = game.state.getExile(game.player1Id).size
            withClue("two lands exiled before the end step") { exiledBefore shouldBe 2 }

            // Advance to this player's end step; the trigger fires and puts one card in.
            game.passUntilPhase(Phase.ENDING, Step.END)
            game.resolveStack()

            withClue("one of the exiled lands moved to the battlefield (one fewer in exile)") {
                game.state.getExile(game.player1Id).size shouldBe exiledBefore - 1
            }
            withClue("a land entered the battlefield this way (Plains or Island present)") {
                (game.isOnBattlefield("Plains") || game.isOnBattlefield("Island")) shouldBe true
            }
        }
    }
}
