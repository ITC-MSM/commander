package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Calamity of Cinders (BLC #23).
 *
 * {5}{R}{R} Sorcery — Convoke
 * "Calamity of Cinders deals 6 damage to each untapped creature."
 *
 * "Each untapped creature" is a *group sweep with a state predicate*, not a target: it hits both
 * players' creatures, and tapped ones are excluded when the spell resolves. That exclusion is the
 * whole card — a tapped blocker or a creature tapped to convoke this very spell walks away — so
 * the tests assert on both sides of the tap line.
 */
class CalamityOfCindersScenarioTest : ScenarioTestBase() {

    init {
        context("Calamity of Cinders") {

            test("burns every untapped creature on both sides and spares the tapped ones") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Calamity of Cinders")
                    .withLandsOnBattlefield(1, "Mountain", 7)
                    .withCardOnBattlefield(1, "Centaur Courser")
                    .withCardOnBattlefield(1, "Savannah Lions", tapped = true)
                    .withCardOnBattlefield(2, "Force of Nature")
                    .withCardOnBattlefield(2, "Goblin Guide", tapped = true)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Calamity of Cinders").error shouldBe null
                game.resolveStack()
                game.checkStateBasedActions()

                withClue("Your own untapped 3/3 is not spared — the sweep is symmetric") {
                    game.findPermanent("Centaur Courser").shouldBeNull()
                }
                withClue("The opponent's untapped 5/5 takes 6 and dies too") {
                    game.findPermanent("Force of Nature").shouldBeNull()
                }
                withClue("Tapped creatures are outside the group filter and take nothing") {
                    game.isOnBattlefield("Savannah Lions") shouldBe true
                    game.isOnBattlefield("Goblin Guide") shouldBe true
                }
            }
        }
    }
}
