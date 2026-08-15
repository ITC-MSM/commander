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
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Over the Edge (LCI #205, {1}{G} Sorcery).
 *
 * "Choose one —
 *  • Destroy target artifact or enchantment.
 *  • Target creature you control explores, then it explores again."
 *
 * Tests:
 *  1. Mode 0 — targeting an enchantment destroys it (moves it to the graveyard).
 *  2. Mode 1 — a creature you control explores twice, two land reveals go to hand with no counters.
 *  3. Mode 1 — a creature you control explores twice, two nonland reveals each place a +1/+1 counter.
 */
class OverTheEdgeScenarioTest : ScenarioTestBase() {

    private fun plusOneCounters(game: TestGame, name: String): Int {
        val id = game.findPermanent(name) ?: return 0
        return game.state.getEntity(id)?.get<CountersComponent>()
            ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
    }

    init {
        context("Over the Edge") {

            // -----------------------------------------------------------------------
            // Test 1: Mode 0 — destroy target artifact or enchantment.
            // -----------------------------------------------------------------------
            test("mode 0: destroys target enchantment") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Over the Edge")
                    .withCardOnBattlefield(1, "Test Enchantment")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val enchantment = game.findPermanent("Test Enchantment")
                withClue("Test Enchantment should start on the battlefield") {
                    enchantment shouldNotBe null
                }

                game.castSpellWithMode(1, "Over the Edge", 0, enchantment)
                game.resolveStack()

                withClue("Test Enchantment should be destroyed (off the battlefield)") {
                    game.isOnBattlefield("Test Enchantment") shouldBe false
                }
                withClue("Test Enchantment should be in its owner's graveyard") {
                    game.isInGraveyard(1, "Test Enchantment") shouldBe true
                }
            }

            // -----------------------------------------------------------------------
            // Test 2: Mode 1 — creature explores twice with land reveals (no counters).
            // -----------------------------------------------------------------------
            test("mode 1: creature explores twice — two land reveals go to hand with no counter") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Over the Edge")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    // Library: [Mountain (top/index-0), Forest (below/index-1)].
                    // Each explore reveals a land → straight to hand, no +1/+1 counter, no decision.
                    .withCardInLibrary(1, "Mountain")   // added first → index 0 = top
                    .withCardInLibrary(1, "Forest")     // added second → index 1 = below
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")
                val handBefore = game.state.getHand(game.player1Id).size  // has Over the Edge → 1

                game.castSpellWithMode(1, "Over the Edge", 1, bears)
                game.resolveStack()  // Explore×2, both lands — no pauses.

                // Net hand delta: −1 (cast Over the Edge) +2 (two land reveals) = +1
                withClue("hand should have grown by 1 (−1 cast Over the Edge, +2 lands from two explores)") {
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
            // Test 3: Mode 1 — creature explores twice with nonland reveals (two counters).
            // -----------------------------------------------------------------------
            test("mode 1: creature explores twice — two nonland reveals each put a +1/+1 counter on it") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Over the Edge")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    // Library top is a nonland; keeping it on top (answerYesNo(true)) lets the
                    // second explore reveal it again → two nonland reveals → two +1/+1 counters.
                    .withCardInLibrary(1, "Lightning Bolt")   // top — revealed by both explores
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")

                game.castSpellWithMode(1, "Over the Edge", 1, bears)

                // Explore #1 reveals Lightning Bolt (nonland) → first counter, then pauses for the
                // back-or-graveyard decision.
                game.resolveStack()
                withClue("first explore (nonland) should pause for the back-or-graveyard decision") {
                    (game.state.pendingDecision != null) shouldBe true
                }
                game.answerYesNo(true)  // keep Lightning Bolt on top of library

                // Explore #2 reveals Lightning Bolt again → second counter, pauses for its decision.
                withClue("second explore (nonland) should pause for the back-or-graveyard decision") {
                    (game.state.pendingDecision != null) shouldBe true
                }
                game.answerYesNo(true)  // keep Lightning Bolt on top
                game.resolveStack()

                withClue("two nonland explores must place two +1/+1 counters on Grizzly Bears") {
                    plusOneCounters(game, "Grizzly Bears") shouldBe 2
                }
            }
        }
    }
}
