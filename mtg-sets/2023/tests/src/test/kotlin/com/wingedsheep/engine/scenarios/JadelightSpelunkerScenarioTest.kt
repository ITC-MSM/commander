package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.engine.state.ZoneKey
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Jadelight Spelunker (LCI #196, {X}{G} Creature — Merfolk Scout 1/1).
 *
 * "When this creature enters, it explores X times."
 *
 * Tests:
 *  1. X=2 with two lands on top of library — two lands go to hand, no +1/+1 counters.
 *  2. X=2 with two nonlands on top of library — two +1/+1 counters placed, two
 *     may-put-in-graveyard decisions (one per explore).
 *  3. X=0 — no explores occur (no library changes, no counters).
 *
 * Jadelight Spelunker is auto-discovered in [com.wingedsheep.mtg.sets.definitions.lci.LostCavernsOfIxalanSet]
 * via [TestCards.all], so no explicit card registration is required.
 */
class JadelightSpelunkerScenarioTest : ScenarioTestBase() {

    private fun plusOneCounters(game: TestGame, name: String): Int {
        val id = game.findPermanent(name) ?: return 0
        return game.state.getEntity(id)?.get<CountersComponent>()
            ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
    }

    init {
        context("Jadelight Spelunker") {

            // -----------------------------------------------------------------------
            // Test 1: X=2 with lands on top — both go to hand, no counters
            // -----------------------------------------------------------------------
            test("ETB explores X times — X=2, two land reveals go to hand with no counters") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Jadelight Spelunker")
                    // {X}{G} for X=2 → 3 mana: two Forests + one extra Green
                    .withLandsOnBattlefield(1, "Forest", 3)
                    // Library top to bottom: Mountain (index 0), Forest (index 1).
                    // First explore → Mountain to hand. Second explore → Forest to hand.
                    .withCardInLibrary(1, "Mountain")  // added first → index 0 = top
                    .withCardInLibrary(1, "Forest")    // added second → index 1
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val handBefore = game.state.getHand(game.player1Id).size  // has Spelunker → 1

                game.castXSpell(1, "Jadelight Spelunker", xValue = 2).error shouldBe null
                // Spelunker enters → ETB trigger queued → resolves: explores twice (both lands, no pauses)
                game.resolveStack()

                withClue("Jadelight Spelunker should be on the battlefield") {
                    game.isOnBattlefield("Jadelight Spelunker") shouldBe true
                }
                // Net hand delta: −1 (cast Spelunker) +2 (two lands from explores) = +1
                withClue("hand grows by 1: −1 cast + 2 land explores") {
                    game.state.getHand(game.player1Id).size shouldBe handBefore - 1 + 2
                }
                withClue("land explores must not place +1/+1 counters (CR 701.44a)") {
                    plusOneCounters(game, "Jadelight Spelunker") shouldBe 0
                }
                withClue("library is empty after both top cards were explored") {
                    game.state.getZone(ZoneKey(game.player1Id, Zone.LIBRARY)).size shouldBe 0
                }
            }

            // -----------------------------------------------------------------------
            // Test 2: X=2 with nonlands on top — two +1/+1 counters placed
            // -----------------------------------------------------------------------
            test("ETB explores X times — X=2, two nonland reveals each put a +1/+1 counter") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Jadelight Spelunker")
                    .withLandsOnBattlefield(1, "Forest", 3)
                    // Lightning Bolt on top; both explores reveal it (player keeps it on top each time).
                    .withCardInLibrary(1, "Lightning Bolt")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castXSpell(1, "Jadelight Spelunker", xValue = 2).error shouldBe null
                // Spelunker enters → ETB trigger resolves: explore #1 reveals Lightning Bolt (nonland)
                // → counter placed → pauses for back-or-graveyard YesNo decision.
                game.resolveStack()
                withClue("first nonland explore pauses for the back-or-graveyard decision") {
                    (game.state.pendingDecision != null) shouldBe true
                }
                game.answerYesNo(true)  // keep Lightning Bolt on top for second explore

                // Explore #2 reveals Lightning Bolt again → another counter → pauses for second decision.
                withClue("second nonland explore pauses for the back-or-graveyard decision") {
                    (game.state.pendingDecision != null) shouldBe true
                }
                game.answerYesNo(true)  // keep it on top
                game.resolveStack()     // finish resolution

                withClue("two nonland explores must place two +1/+1 counters on Jadelight Spelunker") {
                    plusOneCounters(game, "Jadelight Spelunker") shouldBe 2
                }
                withClue("Jadelight Spelunker should be on the battlefield") {
                    game.isOnBattlefield("Jadelight Spelunker") shouldBe true
                }
            }

            // -----------------------------------------------------------------------
            // Test 3: X=0 — no explores, library and hand unchanged
            // -----------------------------------------------------------------------
            test("ETB explores X times — X=0 produces no explores") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Jadelight Spelunker")
                    // {X}{G} for X=0 → only {G} needed
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castXSpell(1, "Jadelight Spelunker", xValue = 0).error shouldBe null
                game.resolveStack()

                withClue("Jadelight Spelunker enters even when X=0") {
                    game.isOnBattlefield("Jadelight Spelunker") shouldBe true
                }
                withClue("no counters placed when X=0") {
                    plusOneCounters(game, "Jadelight Spelunker") shouldBe 0
                }
                withClue("library is unchanged when X=0 — no explore occurred") {
                    game.state.getZone(ZoneKey(game.player1Id, Zone.LIBRARY)).size shouldBe 1
                }
            }
        }
    }
}
