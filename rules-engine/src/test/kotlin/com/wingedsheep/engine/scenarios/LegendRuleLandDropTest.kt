package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.PlayLand
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * The legend rule (CR 704.5j) on the **land-drop** path.
 *
 * Regression. Playing a land is a special action (CR 116.2a): it uses no stack, so it is the one
 * way onto the battlefield that never passes through spell resolution. `PlayLandHandler` ran no
 * state-based-action check, and none of the other SBA call sites covered it — so a player could
 * play a second copy of a legendary land they already controlled and simply keep both, for as
 * long as the game lasted.
 *
 * Playing the second copy is *correct*; the legend rule is a state-based action, not a play
 * restriction. What was missing is the cull that follows.
 *
 * The cast path was never affected — `PassPriorityHandler` checks SBAs after resolution — which is
 * why [LegendRuleTest] passed the whole time. The fixture here is Eiganjo Castle (CHK), one of the
 * 22 legendary lands already in the corpus.
 */
class LegendRuleLandDropTest : ScenarioTestBase() {

    init {
        test("playing a second copy of a legendary land culls one to the graveyard") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardInHand(1, "Eiganjo Castle")
                .withCardOnBattlefield(1, "Eiganjo Castle")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val handCard = game.findCardsInHand(1, "Eiganjo Castle").first()

            withClue("The legend rule is a state-based action, not a play restriction (CR 704.5j)") {
                game.execute(PlayLand(game.player1Id, handCard)).error shouldBe null
            }

            withClue("Both copies are briefly on the battlefield, so the choice is a real one") {
                game.hasPendingDecision() shouldBe true
            }
            val decision = game.getPendingDecision()
            decision.shouldNotBeNull()
            decision.shouldBeInstanceOf<SelectCardsDecision>()
            decision.options.size shouldBe 2

            // Keep the copy that was already in play.
            game.selectCards(listOf(decision.options.first()))

            withClue("Exactly one Eiganjo Castle survives") {
                game.findPermanents("Eiganjo Castle").size shouldBe 1
            }
            withClue("…and the other is in its owner's graveyard") {
                game.isInGraveyard(1, "Eiganjo Castle") shouldBe true
            }
        }

        test("a nonlegendary duplicate land raises no legend-rule prompt") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardInHand(1, "Forest")
                .withLandsOnBattlefield(1, "Forest", 1)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val handCard = game.findCardsInHand(1, "Forest").first()
            game.execute(PlayLand(game.player1Id, handCard)).error shouldBe null

            withClue("The SBA check must not disturb the ordinary land drop") {
                game.hasPendingDecision() shouldBe false
                game.findPermanents("Forest").size shouldBe 2
            }
        }

        test("a legendary land is untouched when its controller has no duplicate") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardInHand(1, "Eiganjo Castle")
                .withCardOnBattlefield(2, "Eiganjo Castle")   // the *opponent* controls the other
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val handCard = game.findCardsInHand(1, "Eiganjo Castle").first()
            game.execute(PlayLand(game.player1Id, handCard)).error shouldBe null

            withClue("The legend rule is per-player — two players may each control one") {
                game.hasPendingDecision() shouldBe false
                game.findPermanents("Eiganjo Castle").size shouldBe 2
            }
        }
    }
}
