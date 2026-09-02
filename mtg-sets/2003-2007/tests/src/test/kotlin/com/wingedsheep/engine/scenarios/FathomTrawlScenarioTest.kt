package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Fathom Trawl (LRW #65, {3}{U}{U} Sorcery).
 *
 *   Reveal cards from the top of your library until you reveal three nonland cards. Put the nonland
 *   cards revealed this way into your hand, then put the rest of the revealed cards on the bottom of
 *   your library in any order.
 *
 * Three is the whole card, so the count is what these tests aim at. `revealUntilMatchToHand`
 * defaulted to a single match before this card; a `count` that silently stayed at 1 would leave a
 * card that reads right, draws one nonland, and bottoms the other two — the failure the first test
 * catches. The second covers the 2007-10-01 ruling: fewer than three nonland cards in the library
 * takes every one there is rather than fizzling.
 */
class FathomTrawlScenarioTest : ScenarioTestBase() {

    /**
     * Library is seeded top-down in call order, so the interleaving here is deliberate: the third
     * nonland card sits under two lands, and a fourth nonland sits under *it*. A correct reveal
     * stops on "Hill Giant" and never touches "Gray Ogre".
     */
    private fun trawl(vararg library: String) = scenario()
        .withPlayers("Alice", "Bob")
        .withCardInHand(1, "Fathom Trawl")
        .withLandsOnBattlefield(1, "Island", 5)
        .apply { library.forEach { withCardInLibrary(1, it) } }
        .withCardInLibrary(2, "Swamp")
        .withActivePlayer(1)
        .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
        .build()

    init {
        context("Fathom Trawl") {

            test("takes three nonland cards and bottoms the lands revealed alongside them") {
                val game = trawl(
                    "Grizzly Bears", // nonland 1
                    "Forest",
                    "Bog Imp", // nonland 2
                    "Mountain",
                    "Plains",
                    "Hill Giant", // nonland 3 — the reveal stops here
                    "Gray Ogre", // never revealed
                )
                val libraryBefore = game.librarySize(1)

                game.castSpell(1, "Fathom Trawl").error shouldBe null
                game.resolveStack()
                // "In any order" is a controller decision; keeping the revealed order is a legal answer.
                if (game.hasPendingDecision()) game.keepLibraryOrder()

                withClue("all three nonland cards revealed this way go to hand") {
                    game.isInHand(1, "Grizzly Bears") shouldBe true
                    game.isInHand(1, "Bog Imp") shouldBe true
                    game.isInHand(1, "Hill Giant") shouldBe true
                }
                withClue("the reveal stops at the third nonland card — the fourth is untouched") {
                    game.isInHand(1, "Gray Ogre") shouldBe false
                }
                withClue("the lands revealed alongside them go to the bottom, not to hand") {
                    game.isInHand(1, "Forest") shouldBe false
                    game.isInHand(1, "Mountain") shouldBe false
                    game.isInHand(1, "Plains") shouldBe false
                }
                withClue("only the three nonland cards left the library") {
                    game.librarySize(1) shouldBe libraryBefore - 3
                }
                game.isInGraveyard(1, "Fathom Trawl") shouldBe true
            }

            test("with fewer than three nonland cards, it takes every one there is") {
                val game = trawl("Forest", "Grizzly Bears", "Mountain", "Bog Imp", "Plains")

                game.castSpell(1, "Fathom Trawl").error shouldBe null
                game.resolveStack()
                if (game.hasPendingDecision()) game.keepLibraryOrder()

                withClue("the 2007-10-01 ruling: reveal the whole library, take every nonland card") {
                    game.isInHand(1, "Grizzly Bears") shouldBe true
                    game.isInHand(1, "Bog Imp") shouldBe true
                }
                withClue("the rest go back to the bottom rather than being lost") {
                    game.librarySize(1) shouldBe 3
                    game.isInGraveyard(1, "Forest") shouldBe false
                }
            }
        }
    }
}
