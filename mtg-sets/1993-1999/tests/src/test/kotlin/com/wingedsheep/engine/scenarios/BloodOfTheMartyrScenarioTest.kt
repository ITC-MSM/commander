package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Blood of the Martyr (DRK #2).
 *
 * {W}{W}{W} Instant
 * "Until end of turn, if damage would be dealt to any creature, you may have that damage dealt to
 *  you instead."
 *
 * The shield protects a *class* rather than a list, so the tests check that it covers a creature
 * the caster doesn't control and that it leaves damage aimed at players alone.
 */
class BloodOfTheMartyrScenarioTest : ScenarioTestBase() {

    init {
        context("Blood of the Martyr") {

            test("damage aimed at a creature hits the caster instead") {
                val game = scenario()
                    .withPlayers("Martyr", "Burner")
                    .withCardInHand(1, "Blood of the Martyr")
                    .withLandsOnBattlefield(1, "Plains", 3)
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(2, "Lightning Bolt")
                    .withLandsOnBattlefield(2, "Mountain", 1)
                    .withLifeTotal(1, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Blood of the Martyr").error shouldBe null
                game.resolveStack()

                // Priority is back with the active player after the instant resolves; hand it to
                // the opponent so they can respond.
                game.passPriority()
                val bears = game.findPermanent("Grizzly Bears")!!
                game.castSpell(2, "Lightning Bolt", targetId = bears).error shouldBe null
                game.resolveStack()

                withClue("the 3 damage went to the caster, not the creature") {
                    game.getLifeTotal(1) shouldBe 17
                }
                withClue("so the 2/2 survived untouched") {
                    game.findPermanent("Grizzly Bears").shouldNotBeNull()
                }
            }

            test("damage aimed at a player is untouched — it shields creatures only") {
                val game = scenario()
                    .withPlayers("Martyr", "Burner")
                    .withCardInHand(1, "Blood of the Martyr")
                    .withLandsOnBattlefield(1, "Plains", 3)
                    .withCardInHand(2, "Lightning Bolt")
                    .withLandsOnBattlefield(2, "Mountain", 1)
                    .withLifeTotal(1, 20)
                    .withLifeTotal(2, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Blood of the Martyr").error shouldBe null
                game.resolveStack()

                game.passPriority()
                game.castSpellTargetingPlayer(2, "Lightning Bolt", 1).error shouldBe null
                game.resolveStack()

                withClue("a bolt to the face is still a bolt to the face") {
                    game.getLifeTotal(1) shouldBe 17
                }
            }
        }
    }
}
