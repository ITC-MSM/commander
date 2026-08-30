package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Creepy Puppeteer (VOW #151) — {3}{R} Creature — Human Rogue 4/3, haste.
 *
 *   Whenever this creature attacks, if you attacked with exactly one other creature this combat,
 *   you may have that creature's base power and toughness become 4/3 until end of turn.
 *
 * The "exactly one" boundary is the whole card: zero other attackers and two other attackers both
 * leave the intervening "if" false, so nothing is asked and nothing changes. With one other
 * attacker, accepting the "may" rewrites that creature's base P/T; declining leaves it alone.
 */
class CreepyPuppeteerScenarioTest : ScenarioTestBase() {

    init {
        context("Creepy Puppeteer") {

            test("exactly one other attacker — accepting the may makes it 4/3") {
                val game = puppeteerGame().build()
                val bears = game.findPermanent("Grizzly Bears")!!

                game.declareAttackers(mapOf("Creepy Puppeteer" to 2, "Grizzly Bears" to 2)).error shouldBe null
                game.resolveStack()

                withClue("the trigger's 'you may' should be pending") {
                    game.hasPendingDecision() shouldBe true
                }
                game.answerYesNo(true).error shouldBe null
                game.resolveStack()

                game.state.projectedState.getPower(bears) shouldBe 4
                game.state.projectedState.getToughness(bears) shouldBe 3
            }

            test("declining the may leaves the other attacker unchanged") {
                val game = puppeteerGame().build()
                val bears = game.findPermanent("Grizzly Bears")!!

                game.declareAttackers(mapOf("Creepy Puppeteer" to 2, "Grizzly Bears" to 2)).error shouldBe null
                game.resolveStack()
                game.hasPendingDecision() shouldBe true
                game.answerYesNo(false).error shouldBe null
                game.resolveStack()

                game.state.projectedState.getPower(bears) shouldBe 2
                game.state.projectedState.getToughness(bears) shouldBe 2
            }

            test("attacking alone — the intervening if fails, nothing is asked") {
                val game = puppeteerGame().build()

                game.declareAttackers(mapOf("Creepy Puppeteer" to 2)).error shouldBe null
                game.resolveStack()

                game.hasPendingDecision() shouldBe false
            }

            test("two other attackers — the intervening if fails, nothing changes") {
                val game = puppeteerGame()
                    .withCardOnBattlefield(1, "Hill Giant", summoningSickness = false)
                    .build()
                val bears = game.findPermanent("Grizzly Bears")!!

                game.declareAttackers(
                    mapOf("Creepy Puppeteer" to 2, "Grizzly Bears" to 2, "Hill Giant" to 2)
                ).error shouldBe null
                game.resolveStack()

                game.hasPendingDecision() shouldBe false
                game.state.projectedState.getPower(bears) shouldBe 2
                game.state.projectedState.getToughness(bears) shouldBe 2
            }
        }
    }

    private fun puppeteerGame(): ScenarioBuilder = scenario()
        .withPlayers("Player1", "Player2")
        .withCardOnBattlefield(1, "Creepy Puppeteer", summoningSickness = false)
        .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
        .withActivePlayer(1)
        .withTurnNumber(3)
        .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
}
