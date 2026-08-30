package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Hunted Horror (RAV #90) — {B}{B} Creature — Horror 7/7.
 *
 *   Trample
 *   When this creature enters, target opponent creates two 3/3 green Centaur creature tokens with
 *   protection from black.
 *
 * The Centaurs' protection from black is the one part of this cycle with no corpus precedent: a
 * *token* has no printed keyword abilities, so protection can't take the
 * `keywordAbility(KeywordAbility.Protection(...))` route a creature card uses. It rides on the
 * token's own `staticAbilities` as a `GrantProtection` scoped to `GroupFilter.source()`, which
 * projects `PROTECTION_FROM_BLACK` — the single keyword every protection leg in the engine reads.
 * These tests prove the keyword actually lands on the token and that the targeting leg honours it,
 * because a silently-inert protection would leave the Horror's drawback unpaid.
 */
class HuntedHorrorScenarioTest : ScenarioTestBase() {

    init {
        context("Hunted Horror") {

            test("two 3/3 Centaurs with protection from black enter under the opponent's control") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardInHand(1, "Hunted Horror")
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Hunted Horror").isSuccess shouldBe true
                game.resolveStack()

                val horror = game.findPermanent("Hunted Horror")!!
                withClue("Hunted Horror is a 7/7 trampler") {
                    game.state.projectedState.getPower(horror) shouldBe 7
                    game.state.projectedState.getToughness(horror) shouldBe 7
                    game.state.projectedState.hasKeyword(horror, Keyword.TRAMPLE) shouldBe true
                }

                val centaurs = game.findPermanents("Centaur Token")
                withClue("Both Centaurs are created") { centaurs shouldHaveSize 2 }

                withClue("Each Centaur is a 3/3 with protection from black, opponent-controlled") {
                    centaurs.forEach { centaur ->
                        game.state.getBattlefield(game.player2Id).contains(centaur) shouldBe true
                        game.state.projectedState.getPower(centaur) shouldBe 3
                        game.state.projectedState.getToughness(centaur) shouldBe 3
                        game.state.projectedState
                            .hasKeyword(centaur, "PROTECTION_FROM_BLACK") shouldBe true
                    }
                }
            }

            test("a black removal spell can't target a Centaur") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardInHand(1, "Hunted Horror")
                    .withCardInHand(1, "Terror")
                    .withLandsOnBattlefield(1, "Swamp", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Hunted Horror").isSuccess shouldBe true
                game.resolveStack()

                val centaur = game.findPermanents("Centaur Token").first()

                val kill = game.castSpell(1, "Terror", centaur)
                withClue("Protection from black should make the Centaur an illegal target") {
                    kill.isSuccess shouldBe false
                }
                withClue("The Centaur is still on the battlefield") {
                    game.findPermanents("Centaur Token") shouldHaveSize 2
                }
            }
        }
    }
}
