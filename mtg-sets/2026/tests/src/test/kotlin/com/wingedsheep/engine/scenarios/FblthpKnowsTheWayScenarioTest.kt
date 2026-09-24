package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Fblthp, Knows the Way (FRA #258) — {X}{G}{G} Legendary Creature — Homunculus Scout * /2:
 *   Domain — Fblthp's power is equal to the number of basic land types among lands you control.
 *   When Fblthp enters, search your library for up to X basic land cards with different names,
 *   reveal them, put them into your hand, then shuffle.
 *
 * Pins that X is the X paid, that "different names" refuses two copies of one basic, that
 * nonbasic lands are not findable, and that the Domain power tracks basic land types.
 */
class FblthpKnowsTheWayScenarioTest : ScenarioTestBase() {

    private fun board(): TestGame = scenario().withPlayers()
        .withCardInHand(1, "Fblthp, Knows the Way")
        .withLandsOnBattlefield(1, "Forest", 4)
        .withCardInLibrary(1, "Plains")
        .withCardInLibrary(1, "Plains")
        .withCardInLibrary(1, "Island")
        .withCardInLibrary(1, "Swamp")
        .withCardInLibrary(1, "Tundra")
        .withCardInLibrary(2, "Plains")
        .withActivePlayer(1)
        .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
        .build()

    init {
        test("X = 2 finds two basics with different names; power is the domain count") {
            val game = board()
            game.castXSpell(1, "Fblthp, Knows the Way", 2).error shouldBe null
            game.resolveStack()

            val fblthp = game.findPermanent("Fblthp, Knows the Way")!!
            withClue("only Forests: one basic land type") {
                game.state.projectedState.getPower(fblthp) shouldBe 1
                game.state.projectedState.getToughness(fblthp) shouldBe 2
            }

            withClue("the enters trigger pauses for the search") {
                game.hasPendingDecision() shouldBe true
            }
            val plains = game.findCardsInLibrary(1, "Plains")
            val island = game.findCardsInLibrary(1, "Island").single()
            val tundra = game.findCardsInLibrary(1, "Tundra").single()

            withClue("a nonbasic land is not a basic land card") {
                game.selectCards(listOf(tundra)).error shouldNotBe null
            }
            withClue("\"up to X\" — three is one too many for X = 2") {
                game.selectCards(listOf(plains.first(), island, game.findCardsInLibrary(1, "Swamp").single()))
                    .error shouldNotBe null
            }
            game.selectCards(listOf(plains.first(), island)).error shouldBe null
            game.resolveStack()

            game.isInHand(1, "Plains") shouldBe true
            game.isInHand(1, "Island") shouldBe true
            game.findCardsInLibrary(1, "Plains").size shouldBe 1
            game.findCardsInLibrary(1, "Swamp").size shouldBe 1
        }

        test("two cards named Plains are not \"different names\": only one is found") {
            val game = board()
            game.castXSpell(1, "Fblthp, Knows the Way", 2).error shouldBe null
            game.resolveStack()
            game.hasPendingDecision() shouldBe true

            game.selectCards(game.findCardsInLibrary(1, "Plains")).error shouldBe null
            game.resolveStack()

            game.findCardsInHand(1, "Plains").size shouldBe 1
            game.findCardsInLibrary(1, "Plains").size shouldBe 1
        }

        test("X = 0 searches for nothing") {
            val game = board()
            game.castXSpell(1, "Fblthp, Knows the Way", 0).error shouldBe null
            game.resolveStack()

            game.isOnBattlefield("Fblthp, Knows the Way") shouldBe true
            if (game.hasPendingDecision()) {
                game.skipSelection().error shouldBe null
                game.resolveStack()
            }
            game.handSize(1) shouldBe 0
            game.librarySize(1) shouldBe 5
        }

        test("power grows with each basic land type among lands you control") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Fblthp, Knows the Way")
                .withCardOnBattlefield(1, "Forest")
                .withCardOnBattlefield(1, "Tundra")
                .withCardOnBattlefield(2, "Swamp")
                .withCardInLibrary(1, "Plains")
                .withCardInLibrary(2, "Plains")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()
            val fblthp = game.findPermanent("Fblthp, Knows the Way")!!
            withClue("Forest + Tundra (Plains Island) = 3; the opponent's Swamp doesn't count") {
                game.state.projectedState.getPower(fblthp) shouldBe 3
            }
        }
    }
}
