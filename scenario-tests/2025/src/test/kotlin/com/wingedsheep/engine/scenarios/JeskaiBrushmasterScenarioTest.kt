package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Jeskai Brushmaster (TDM #195).
 *
 * {1}{U}{R}{W} Creature — Orc Monk, 2/4. Double strike + Prowess.
 *
 * Both abilities are well-covered keywords; this confirms the card wires them up: it enters as a
 * 2/4 with double strike, and casting a noncreature spell pumps it +1/+1 via prowess.
 */
class JeskaiBrushmasterScenarioTest : ScenarioTestBase() {

    init {
        context("Jeskai Brushmaster keywords") {

            test("enters as a 2/4 with double strike") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Jeskai Brushmaster")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val brushmaster = game.findPermanent("Jeskai Brushmaster")!!
                val projected = StateProjector().project(game.state)

                projected.getPower(brushmaster) shouldBe 2
                projected.getToughness(brushmaster) shouldBe 4
                withClue("Jeskai Brushmaster has double strike") {
                    projected.hasKeyword(brushmaster, Keyword.DOUBLE_STRIKE) shouldBe true
                }
            }

            test("prowess pumps it when a noncreature spell is cast") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Jeskai Brushmaster")
                    .withCardInHand(1, "Lightning Bolt")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val brushmaster = game.findPermanent("Jeskai Brushmaster")!!

                game.castSpellTargetingPlayer(1, "Lightning Bolt", 2).error shouldBe null
                game.resolveStack() // prowess trigger + bolt resolve

                val projected = StateProjector().project(game.state)
                withClue("Prowess: Jeskai Brushmaster is 3/5 after a noncreature spell") {
                    projected.getPower(brushmaster) shouldBe 3
                    projected.getToughness(brushmaster) shouldBe 5
                }
            }
        }
    }
}
