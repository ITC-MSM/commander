package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Reproduction test for the Larval Scoutlander (EOE #194) bug.
 *
 * The card's ETB clause reads:
 *  "When this Spacecraft enters, you may sacrifice a land or Lander. If you do, search your
 *   library for up to two basic land cards, put them onto the battlefield tapped, then shuffle."
 *
 * Bug surfaced in play: the may-sacrifice prompt fires and a land is sacrificed, but the
 * library-search payoff never delivers — no basic lands hit the battlefield and the library
 * is not shuffled. This is the "MayEffect(Sacrifice.then(search))" vs. correct
 * "IfYouDoEffect(Sacrifice, search)" mis-modeling: the whole composite gets gated as one
 * yes/no instead of the sacrifice acting as the action whose outcome gates the search.
 *
 * This test exercises the full flow Alice would experience and asserts on each visible
 * step. It is expected to FAIL today on the post-sacrifice payoff.
 */
class LarvalScoutlanderScenarioTest : ScenarioTestBase() {

    init {
        context("Larval Scoutlander ETB") {

            test("sacrificing a land searches up two basic lands tapped and shuffles") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardInHand(1, "Larval Scoutlander")
                    // Four Forests on battlefield: three pay {2}{G}, one is the sacrificial fodder
                    // (so Alice still has lands left over after the ETB resolves).
                    .withLandsOnBattlefield(1, "Forest", 4)
                    // Library: 2 searchable Forests + 8 non-land cards so the post-search
                    // residue is large enough that a real shuffle is statistically visible
                    // (8! permutations; the original-order outcome is 1/40320 ≈ 0.0025%).
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Glory Seeker")
                    .withCardInLibrary(1, "Border Guard")
                    .withCardInLibrary(1, "Lightning Bolt")
                    .withCardInLibrary(1, "Cancel")
                    .withCardInLibrary(1, "Giant Spider")
                    .withCardInLibrary(1, "Hill Giant")
                    .withCardInLibrary(1, "Mountain")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val alice = game.player1Id

                // (1) Capture pre-cast library order so we can detect shuffling later.
                val libraryBeforeCast = game.state.getLibrary(alice).toList()
                val battlefieldLandsBefore = game.findPermanents("Forest").size

                // (2) Cast Larval Scoutlander, resolve mana, advance the stack so the ETB
                //     trigger pops and asks the controller to choose.
                game.castSpell(1, "Larval Scoutlander").error shouldBe null
                game.resolveStack()

                // (3) Answer the may-pay yes/no with YES.
                withClue("the ETB trigger should open a yes/no may-sacrifice prompt") {
                    (game.getPendingDecision() is YesNoDecision) shouldBe true
                }
                game.answerYesNo(true)
                game.resolveStack()

                // (4) Answer the sacrifice prompt by selecting exactly one Forest from
                //     Alice's battlefield.
                withClue("the may-sacrifice resolution should ask for a land or Lander to sacrifice") {
                    val decision = game.getPendingDecision()
                    (decision is SelectCardsDecision) shouldBe true
                }
                val sacrificeDecision = game.getPendingDecision() as SelectCardsDecision
                val forestToSac = sacrificeDecision.options.first()
                game.selectCards(listOf(forestToSac)).error shouldBe null
                game.resolveStack()

                // (5) Answer the library-search prompt by selecting two basic lands from
                //     Alice's library.
                withClue("after sacrificing, the library-search prompt should appear") {
                    val decision = game.getPendingDecision()
                    (decision is SelectCardsDecision) shouldBe true
                }
                val searchDecision = game.getPendingDecision() as SelectCardsDecision
                val searchedLands = searchDecision.options.take(2)
                game.selectCards(searchedLands).error shouldBe null
                game.resolveStack()

                // --- Assertions, in order, one withClue each. ---

                withClue("Larval Scoutlander entered the battlefield under Alice's control") {
                    game.isOnBattlefield("Larval Scoutlander") shouldBe true
                }

                withClue("Alice sacrificed exactly one land — graveyard has +1 Forest") {
                    game.findCardsInGraveyard(1, "Forest").size shouldBe 1
                }

                // THE BUG ENTRY: the searched basics actually move from library to battlefield.
                withClue(
                    "Alice's battlefield should have two MORE lands than before the search " +
                        "(the searched basics actually entered the battlefield). " +
                        "Before: $battlefieldLandsBefore, after the sacrifice the Forests on " +
                        "battlefield should be $battlefieldLandsBefore - 1 (sacrificed) + 2 (searched) " +
                        "= ${battlefieldLandsBefore + 1}."
                ) {
                    game.findPermanents("Forest").size shouldBe battlefieldLandsBefore + 1
                }

                withClue("both newly-arrived searched basic lands have TappedComponent (entersTapped=true)") {
                    val tappedSearched = searchedLands.count { id ->
                        game.state.getEntity(id)?.get<TappedComponent>() != null
                    }
                    tappedSearched shouldBe 2
                }

                withClue(
                    "the library was shuffled: the post-resolution library should differ from the pre-cast " +
                        "order (or at minimum the searched cards are gone from the library)."
                ) {
                    val libraryAfter = game.state.getLibrary(alice).toList()
                    val searchedGone = searchedLands.none { it in libraryAfter }
                    val orderChanged = libraryAfter != libraryBeforeCast.filterNot { it in searchedLands }
                    (searchedGone && orderChanged) shouldBe true
                }
            }
        }
    }
}
