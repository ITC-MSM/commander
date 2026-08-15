package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Thunderdrum Soloist {1}{R} 1/3 Dwarf Bard (Reach).
 *
 * "Opus — Whenever you cast an instant or sorcery spell, this creature deals 1 damage to each
 * opponent. If five or more mana was spent to cast that spell, this creature deals 3 damage to
 * each opponent instead."
 *
 * Opus is an ability word; the 3-damage tier *replaces* the 1-damage base (insteadIfFiveOrMore).
 * Exercises the sub-5 base (1 damage) and the 5+ boundary (3 damage instead, not 4).
 */
class ThunderdrumSoloistScenarioTest : ScenarioTestBase() {

    init {
        context("Thunderdrum Soloist") {

            test("a cheap instant deals 1 damage to each opponent (5+ tier not reached)") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Thunderdrum Soloist")
                    .withCardInHand(1, "Lightning Bolt") // {R}
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val before = game.getLifeTotal(2)

                game.castSpell(1, "Lightning Bolt", targetId = bears).error shouldBe null
                game.resolveStack()

                withClue("1 mana spent → Soloist deals 1 to the opponent") {
                    game.getLifeTotal(2) shouldBe before - 1
                }
            }

            test("a 5-mana spell deals 3 damage to each opponent INSTEAD (boundary: not 4)") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Thunderdrum Soloist")
                    .withCardInHand(1, "Blaze") // {X}{R}
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Mountain", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val before = game.getLifeTotal(2)

                // Blaze X=4 → {4}{R} → 5 mana spent (boundary). Target the bears so the opponent's
                // life change is attributable solely to the Soloist (3), not Blaze.
                game.castXSpell(1, "Blaze", xValue = 4, targetId = bears).error shouldBe null
                game.resolveStack()

                withClue("5 mana spent → Soloist deals 3 (instead of 1) to the opponent") {
                    game.getLifeTotal(2) shouldBe before - 3
                }
            }
        }
    }
}
