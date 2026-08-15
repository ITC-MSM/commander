package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Spider Food — {2}{G} Sorcery — "Destroy up to one target artifact, enchantment, or creature with
 * flying. Create a Food token."
 *
 * Covers the heterogeneous OR target (a creature with flying) plus the always-on Food half, and
 * the "up to one" optional target (declining still creates the Food).
 */
class SpiderFoodScenarioTest : ScenarioTestBase() {

    private fun game(vararg opponentPermanents: String) = scenario()
        .withPlayers()
        .withCardInHand(1, "Spider Food")
        .withLandsOnBattlefield(1, "Forest", 3)
        .apply { opponentPermanents.forEach { withCardOnBattlefield(2, it) } }
        .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
        .withActivePlayer(1)
        .withPriorityPlayer(1)
        .build()

    init {
        test("destroys a creature with flying and creates a Food") {
            val g = game("Birds of Paradise") // Birds of Paradise has flying
            val flyer = g.findPermanent("Birds of Paradise")!!

            g.castSpell(1, "Spider Food", flyer).error shouldBe null
            g.resolveStack()

            g.isOnBattlefield("Birds of Paradise") shouldBe false
            g.isInGraveyard(2, "Birds of Paradise") shouldBe true
            g.findPermanents("Food").size shouldBe 1
        }

        test("with no target chosen it still creates a Food") {
            val g = game("Centaur Courser")

            g.castSpell(1, "Spider Food").error shouldBe null
            if (g.getPendingDecision() is ChooseTargetsDecision) {
                g.skipTargets()
            }
            g.resolveStack()

            withClue("no permanent destroyed") {
                g.isOnBattlefield("Centaur Courser") shouldBe true
            }
            withClue("Food still created") {
                g.findPermanents("Food").size shouldBe 1
            }
        }
    }
}
