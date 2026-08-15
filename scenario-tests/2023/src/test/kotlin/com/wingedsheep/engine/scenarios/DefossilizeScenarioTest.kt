package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Defossilize (LCI #103, {4}{B} Sorcery).
 *
 * "Return target creature card from your graveyard to the battlefield.
 *  That creature explores, then it explores again."
 *
 * Tests:
 *  1. The targeted creature card moves from the graveyard to the battlefield.
 *  2. The reanimated creature explores exactly twice.
 *     - Both explores reveal lands → two cards go to hand, no +1/+1 counters.
 *     - Both explores reveal nonlands → two +1/+1 counters are placed, with two
 *       may-put-in-graveyard decisions (one per explore).
 *
 * Uses [ScenarioTestBase] / the [scenario] builder. Defossilize is auto-discovered in
 * [com.wingedsheep.mtg.sets.definitions.lci.LostCavernsOfIxalanSet] via [TestCards.all],
 * so no explicit card registration is required.
 */
class DefossilizeScenarioTest : ScenarioTestBase() {

    private fun plusOneCounters(game: TestGame, name: String): Int {
        val id = game.findPermanent(name) ?: return 0
        return game.state.getEntity(id)?.get<CountersComponent>()
            ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
    }

    init {
        context("Defossilize") {

            // -----------------------------------------------------------------------
            // Test 1: Basic reanimation — creature goes from graveyard to battlefield
            // -----------------------------------------------------------------------
            test("returns target creature card from graveyard to battlefield") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Defossilize")
                    .withCardInGraveyard(1, "Grizzly Bears")
                    // Two lands in library (both reveals go to hand, no decision pauses).
                    .withCardInLibrary(1, "Forest")    // top
                    .withCardInLibrary(1, "Mountain")  // below
                    .withLandsOnBattlefield(1, "Swamp", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpellTargetingGraveyardCard(1, "Defossilize", 1, "Grizzly Bears")
                game.resolveStack()

                withClue("Grizzly Bears should be on the battlefield after reanimation") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                }
                withClue("Grizzly Bears should no longer be in the graveyard") {
                    game.isInGraveyard(1, "Grizzly Bears") shouldBe false
                }
            }

            // -----------------------------------------------------------------------
            // Test 2: Double explore with lands — both lands go to hand, no counters
            // -----------------------------------------------------------------------
            test("reanimated creature explores twice — two land reveals go to hand with no counter") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Defossilize")
                    .withCardInGraveyard(1, "Grizzly Bears")
                    // Library: [Mountain (top/index-0), Forest (bottom/index-1)].
                    // First explore → Mountain to hand.  Second explore → Forest to hand.
                    // Neither is a nonland, so no may-put-in-graveyard decision fires
                    // and no +1/+1 counters are added.
                    .withCardInLibrary(1, "Mountain")   // added first → index 0 = top
                    .withCardInLibrary(1, "Forest")     // added second → index 1 = below
                    .withLandsOnBattlefield(1, "Swamp", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val handBefore = game.state.getHand(game.player1Id).size  // has Defossilize → 1

                game.castSpellTargetingGraveyardCard(1, "Defossilize", 1, "Grizzly Bears")
                game.resolveStack()  // spell resolves: PutOntoBattlefield, Explore×2 (both lands — no pauses)

                withClue("Grizzly Bears should be on the battlefield") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                }
                // Net hand delta: −1 (Defossilize cast) +2 (two land reveals) = +1
                withClue("hand should have grown by 1 (−1 cast Defossilize, +2 lands drawn by two explores)") {
                    game.state.getHand(game.player1Id).size shouldBe handBefore - 1 + 2
                }
                withClue("land reveals must not place +1/+1 counters (CR 701.44a)") {
                    plusOneCounters(game, "Grizzly Bears") shouldBe 0
                }
                withClue("library should be empty after both top cards were drawn") {
                    game.state.getZone(ZoneKey(game.player1Id, Zone.LIBRARY)).size shouldBe 0
                }
            }

            // -----------------------------------------------------------------------
            // Test 3: Double explore with nonlands — two +1/+1 counters placed
            // -----------------------------------------------------------------------
            test("reanimated creature explores twice — two nonland reveals each put a +1/+1 counter on it") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Defossilize")
                    .withCardInGraveyard(1, "Grizzly Bears")
                    // Library: [Lightning Bolt (top)].  Both explores reveal Lightning Bolt because
                    // the player keeps it on top after each nonland reveal (answerYesNo(true)).
                    // Two nonland reveals → two decisions → two +1/+1 counters placed.
                    .withCardInLibrary(1, "Lightning Bolt")   // top — revealed by both explores
                    .withLandsOnBattlefield(1, "Swamp", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpellTargetingGraveyardCard(1, "Defossilize", 1, "Grizzly Bears")

                // Spell resolves: PutOntoBattlefield then Explore #1.
                // Explore #1 reveals Lightning Bolt (top card, nonland) → counter placed, then pauses
                // for the ExploreEffect YesNoDecision ("put card back on top of library, or graveyard?").
                game.resolveStack()
                withClue("first explore (nonland) should pause for the back-or-graveyard decision") {
                    (game.state.pendingDecision != null) shouldBe true
                }
                // Yes = keep Lightning Bolt on top of library so the second explore reveals it again.
                game.answerYesNo(true)

                // Answering resumes the composite continuation: Explore #2 reveals Lightning Bolt again
                // (still on top), places the second counter, and pauses for its own YesNoDecision.
                withClue("second explore (nonland) should pause for the back-or-graveyard decision") {
                    (game.state.pendingDecision != null) shouldBe true
                }
                game.answerYesNo(true)  // keep Lightning Bolt on top
                game.resolveStack()     // finish the resolution

                withClue("two nonland explores must place two +1/+1 counters on Grizzly Bears") {
                    plusOneCounters(game, "Grizzly Bears") shouldBe 2
                }
                withClue("Grizzly Bears should be on the battlefield") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                }
            }
        }
    }
}
