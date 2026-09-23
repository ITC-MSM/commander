package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Kindred Judgment — "Choose a creature type. Destroy all creatures that aren't of the chosen
 * type."
 *
 * The destroy set is the *complement* of the chosen type, on both sides of the table, and a
 * changeling has every creature type so it is always spared.
 */
class KindredJudgmentScenarioTest : ScenarioTestBase() {
    init {
        test("destroys every creature not of the chosen type; the chosen type and changelings survive") {
            val game = scenario().withPlayers()
                .withCardInHand(1, "Kindred Judgment")
                .withLandsOnBattlefield(1, "Plains", 7)
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withCardOnBattlefield(1, "Raging Goblin")
                .withCardOnBattlefield(2, "Raging Goblin")
                .withCardOnBattlefield(2, "Changeling Berserker")
                .withCardOnBattlefield(2, "Grizzly Bears")
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            game.castSpell(1, "Kindred Judgment").error shouldBe null
            game.resolveStack()

            val decision = game.getPendingDecision()
            decision.shouldBeInstanceOf<ChooseOptionDecision>()
            game.submitDecision(OptionChosenResponse(decision.id, decision.options.indexOf("Goblin")))

            withClue("both Grizzly Bears are not Goblins") {
                game.findAllPermanents("Grizzly Bears").size shouldBe 0
                game.isInGraveyard(1, "Grizzly Bears") shouldBe true
                game.isInGraveyard(2, "Grizzly Bears") shouldBe true
            }
            withClue("both Raging Goblins are of the chosen type") {
                game.findAllPermanents("Raging Goblin").size shouldBe 2
            }
            withClue("a changeling is every creature type, Goblin included") {
                game.isOnBattlefield("Changeling Berserker") shouldBe true
            }
        }
    }
}
