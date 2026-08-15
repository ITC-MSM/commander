package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Matterbending Mage {2}{U} 2/2 Human Wizard.
 *
 * "When this creature enters, return up to one other target creature to its owner's hand.
 *  Whenever you cast a spell with {X} in its mana cost, this creature can't be blocked this turn."
 *
 * Exercises:
 *  - the ETB bounce of an "other target creature" (and that it cannot pick itself);
 *  - declining the optional bounce (up to one);
 *  - the {X}-spell trigger granting itself CANT_BE_BLOCKED for the turn (X=0 still counts);
 *  - a spell without {X} not granting evasion.
 */
class MatterbendingMageScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    init {
        context("Matterbending Mage — ETB bounce") {

            test("returns an other target creature to its owner's hand") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Matterbending Mage")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                game.castSpell(1, "Matterbending Mage").error shouldBe null
                game.resolveStack()

                withClue("ETB pauses to choose the bounce target") {
                    (game.state.pendingDecision != null) shouldBe true
                }
                game.selectTargets(listOf(bears)).error shouldBe null
                game.resolveStack()

                withClue("Grizzly Bears returned to its owner's (Player2's) hand") {
                    game.findPermanent("Grizzly Bears") shouldBe null
                    game.isInHand(2, "Grizzly Bears") shouldBe true
                }
            }

            test("the bounce is optional (up to one) — declining leaves the board untouched") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Matterbending Mage")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Matterbending Mage").error shouldBe null
                game.resolveStack()

                game.skipTargets().error shouldBe null
                game.resolveStack()

                withClue("declining returns nothing — Grizzly Bears stays on the battlefield") {
                    (game.findPermanent("Grizzly Bears") != null) shouldBe true
                    game.isInHand(2, "Grizzly Bears") shouldBe false
                }
            }
        }

        context("Matterbending Mage — {X} spell unblockable trigger") {

            test("casting a spell with {X} in its cost makes the Mage unblockable this turn") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Matterbending Mage")
                    .withCardInHand(1, "Blaze") // {X}{R}
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Island", 1)
                    .withLandsOnBattlefield(1, "Mountain", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val mage = game.findPermanent("Matterbending Mage")!!
                val bears = game.findPermanent("Grizzly Bears")!!

                withClue("no evasion before any {X} spell is cast") {
                    projector.project(game.state).hasKeyword(mage, AbilityFlag.CANT_BE_BLOCKED) shouldBe false
                }

                game.castXSpell(1, "Blaze", xValue = 2, targetId = bears).error shouldBe null
                game.resolveStack()

                withClue("the {X} spell granted the Mage CANT_BE_BLOCKED for the turn") {
                    projector.project(game.state).hasKeyword(mage, AbilityFlag.CANT_BE_BLOCKED) shouldBe true
                }
            }

            test("a spell WITHOUT {X} in its cost grants no evasion") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Matterbending Mage")
                    .withCardInHand(1, "Lightning Bolt") // {R}, no {X}
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val mage = game.findPermanent("Matterbending Mage")!!
                val bears = game.findPermanent("Grizzly Bears")!!

                game.castSpell(1, "Lightning Bolt", targetId = bears).error shouldBe null
                game.resolveStack()

                withClue("no {X} in cost → no unblockable grant") {
                    projector.project(game.state).hasKeyword(mage, AbilityFlag.CANT_BE_BLOCKED) shouldBe false
                }
            }
        }
    }
}
