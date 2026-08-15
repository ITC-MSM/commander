package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Spinewoods Paladin (OTJ #183) — {4}{G} Creature — Human Knight, 5/4.
 *
 * "Trample
 *  When this creature enters, you gain 3 life.
 *  Plot {3}{G}"
 *
 * Plot is a generic, well-covered keyword; this test pins the novel ETB life gain and the
 * Trample keyword.
 */
class SpinewoodsPaladinScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    init {
        context("Spinewoods Paladin ETB") {

            test("gains 3 life on enter and has trample") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Spinewoods Paladin")
                    .withLandsOnBattlefield(1, "Forest", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val startLife = game.getLifeTotal(1)

                game.castSpell(1, "Spinewoods Paladin").error shouldBe null
                game.resolveStack()

                withClue("controller gained 3 life from the ETB trigger") {
                    game.getLifeTotal(1) shouldBe startLife + 3
                }
                withClue("Spinewoods Paladin resolved onto the battlefield") {
                    game.isOnBattlefield("Spinewoods Paladin") shouldBe true
                }
                val paladin = game.findPermanent("Spinewoods Paladin")!!
                withClue("Spinewoods Paladin has trample") {
                    projector.hasProjectedKeyword(game.state, paladin, Keyword.TRAMPLE) shouldBe true
                }
            }
        }
    }
}
