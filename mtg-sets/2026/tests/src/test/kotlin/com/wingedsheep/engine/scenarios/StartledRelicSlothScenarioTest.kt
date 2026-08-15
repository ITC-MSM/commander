package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Startled Relic Sloth (SOS #233).
 *
 * "{2}{R}{W} Creature — Sloth Beast 4/4. Trample, lifelink.
 *  At the beginning of combat on your turn, exile up to one target card from a graveyard."
 *
 * Covers: (a) the begin-combat trigger exiles a chosen graveyard card,
 * (b) it has trample and lifelink.
 */
class StartledRelicSlothScenarioTest : ScenarioTestBase() {

    init {
        context("Startled Relic Sloth") {

            test("begin-combat trigger exiles a chosen card from a graveyard") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Startled Relic Sloth", summoningSickness = false)
                    .withCardInGraveyard(2, "Hill Giant")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val target = game.state.getGraveyard(game.player2Id).single()

                // Advance into combat so the "beginning of combat on your turn" trigger fires.
                game.passUntilPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
                game.resolveStack()

                // "Up to one target" — choose the graveyard card.
                game.selectTargets(listOf(target))
                game.resolveStack()

                withClue("Opponent's graveyard should be empty after the card is exiled") {
                    game.state.getGraveyard(game.player2Id).size shouldBe 0
                }
                withClue("The card should now be in the opponent's exile zone") {
                    game.state.getExile(game.player2Id) shouldContainExactly listOf(target)
                }
            }

            test("has trample and lifelink") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Startled Relic Sloth")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val sloth = game.findPermanent("Startled Relic Sloth")!!
                withClue("Startled Relic Sloth should have trample") {
                    game.state.projectedState.hasKeyword(sloth, Keyword.TRAMPLE) shouldBe true
                }
                withClue("Startled Relic Sloth should have lifelink") {
                    game.state.projectedState.hasKeyword(sloth, Keyword.LIFELINK) shouldBe true
                }
            }
        }
    }
}
