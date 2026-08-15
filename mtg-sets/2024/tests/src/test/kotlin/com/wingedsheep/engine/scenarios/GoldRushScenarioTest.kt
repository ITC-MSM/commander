package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Gold Rush (OTJ #166).
 *
 * "{1}{G} Instant. Create a Treasure token. Until end of turn, up to one target creature gets
 *  +2/+2 for each Treasure you control."
 *
 * Verifies: the Treasure created by the spell counts toward the buff (so with zero prior
 * Treasures the buff is +2/+2), additional Treasures scale it, the buff is locked at resolution,
 * and the spell still resolves (creating a Treasure) when no target is chosen.
 */
class GoldRushScenarioTest : ScenarioTestBase() {

    init {
        context("Gold Rush") {

            test("buffs target by +2/+2 per Treasure, counting the Treasure it creates") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Gold Rush")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bear = game.findPermanent("Grizzly Bears")!!
                withClue("Bear starts as a 2/2") {
                    game.state.projectedState.getPower(bear) shouldBe 2
                    game.state.projectedState.getToughness(bear) shouldBe 2
                }

                game.castSpell(1, "Gold Rush", bear).error shouldBe null
                game.resolveStack()

                // One Treasure now exists -> +2/+2.
                withClue("a Treasure token was created") {
                    game.findPermanents("Treasure").size shouldBe 1
                }
                withClue("Bear becomes a 4/4 (+2/+2 for the single Treasure)") {
                    game.state.projectedState.getPower(bear) shouldBe 4
                    game.state.projectedState.getToughness(bear) shouldBe 4
                }
            }

            test("buff scales with existing Treasures and locks at resolution") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Gold Rush")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardOnBattlefield(1, "Treasure", isToken = true)
                    .withCardOnBattlefield(1, "Treasure", isToken = true)
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bear = game.findPermanent("Grizzly Bears")!!

                game.castSpell(1, "Gold Rush", bear).error shouldBe null
                game.resolveStack()

                // 2 existing + 1 created = 3 Treasures -> +6/+6.
                withClue("three Treasures now exist") {
                    game.findPermanents("Treasure").size shouldBe 3
                }
                withClue("Bear becomes an 8/8 (+6/+6 for three Treasures)") {
                    game.state.projectedState.getPower(bear) shouldBe 8
                    game.state.projectedState.getToughness(bear) shouldBe 8
                }
            }

            test("resolves and still creates a Treasure when no target is chosen") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Gold Rush")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Gold Rush", null).error shouldBe null
                game.resolveStack()

                withClue("a Treasure token was created even with no creature target") {
                    game.findPermanents("Treasure").size shouldBe 1
                }
            }
        }
    }
}
