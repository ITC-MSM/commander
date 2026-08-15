package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Mintstrosity — {1}{B} Creature — Horror 3/1 — "When this creature dies, create a Food token."
 *
 * Kills the 3/1 with a Lightning Bolt and confirms the self-dies trigger makes exactly one Food.
 */
class MintstrosityScenarioTest : ScenarioTestBase() {

    init {
        test("creates a Food token when it dies") {
            val g = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Mintstrosity")
                .withCardInHand(1, "Lightning Bolt")
                .withLandsOnBattlefield(1, "Mountain", 1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .withActivePlayer(1)
                .withPriorityPlayer(1)
                .build()

            val mint = g.findPermanent("Mintstrosity")!!
            g.findPermanents("Food").size shouldBe 0

            g.castSpell(1, "Lightning Bolt", mint).error shouldBe null
            g.resolveStack() // bolt resolves; state-based death moves Mintstrosity to graveyard
            g.resolveStack() // dies trigger resolves

            withClue("Mintstrosity died") {
                g.isOnBattlefield("Mintstrosity") shouldBe false
                g.isInGraveyard(1, "Mintstrosity") shouldBe true
            }
            withClue("dying created one Food") {
                g.findPermanents("Food").size shouldBe 1
            }
        }
    }
}
