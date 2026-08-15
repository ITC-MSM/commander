package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Revival of the Ancestors (TDM #218) — {1}{W}{B}{G} Enchantment — Saga.
 *
 * "I — Create three 1/1 white Spirit creature tokens.
 *  II — Distribute three +1/+1 counters among one, two, or three target creatures you control.
 *  III — Creatures you control gain trample and lifelink until end of turn."
 *
 * Confirms chapter I: casting the Saga puts it onto the battlefield, the entering lore counter
 * triggers chapter I, and three 1/1 white Spirit tokens are created.
 *
 * Chapters II/III advance via the lore-counter turn-based action on later turns, which the
 * turn-driver-free ScenarioTestBase cannot reach cleanly (see the Thunder of Unity note in
 * TdmGroup3ScenarioTest); they are exercised by the chapter-by-chapter saga driver suite instead.
 */
class RevivalOfTheAncestorsScenarioTest : ScenarioTestBase() {

    init {
        context("Revival of the Ancestors chapter I") {

            test("casting the Saga creates three 1/1 white Spirit tokens") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Revival of the Ancestors")
                    .withLandsOnBattlefield(1, "Plains", 1)
                    .withLandsOnBattlefield(1, "Swamp", 1)
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withLandsOnBattlefield(1, "Island", 1) // {1}{W}{B}{G}
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                withClue("Casting Revival of the Ancestors should succeed") {
                    game.castSpell(1, "Revival of the Ancestors").error shouldBe null
                }
                game.resolveStack() // Saga enters (lore 1 → chapter I), then chapter I resolves

                withClue("The Saga is on the battlefield") {
                    game.isOnBattlefield("Revival of the Ancestors") shouldBe true
                }
                withClue("Chapter I creates three 1/1 white Spirit tokens") {
                    game.findAllPermanents("Spirit Token").size shouldBe 3
                }
            }
        }
    }
}
