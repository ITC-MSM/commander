package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

class DiregrafCaptainScenarioTest : ScenarioTestBase() {
    init {
        test("other Zombies you control get +1/+1") {
            val game = scenario()
                .withPlayers("P1", "P2")
                .withCardOnBattlefield(1, "Diregraf Captain")
                .withCardOnBattlefield(1, "Diregraf Ghoul")
                .build()

            val captain = game.findPermanent("Diregraf Captain")!!
            val ghoul = game.findPermanent("Diregraf Ghoul")!!
            game.state.projectedState.getPower(captain) shouldBe 2
            game.state.projectedState.getToughness(captain) shouldBe 2
            game.state.projectedState.getPower(ghoul) shouldBe 3
            game.state.projectedState.getToughness(ghoul) shouldBe 3
        }

        test("when another Zombie you control dies, target opponent loses 1") {
            val game = scenario()
                .withPlayers("P1", "P2")
                .withCardOnBattlefield(1, "Diregraf Captain")
                .withCardOnBattlefield(1, "Diregraf Ghoul")
                .withCardInHand(1, "Lightning Bolt")
                .withLandsOnBattlefield(1, "Mountain", 1)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val ghoul = game.findPermanent("Diregraf Ghoul")!!
            // Buffed Ghoul is 3/3 — Bolt (3) is needed; Shock (2) leaves it alive.
            game.castSpell(1, "Lightning Bolt", ghoul).error shouldBe null
            game.resolveStack()

            // Single legal opponent may already be chosen; otherwise choose.
            if (game.state.pendingDecision is ChooseTargetsDecision) {
                game.selectTargets(listOf(game.player2Id))
            }
            if (game.state.stack.isNotEmpty()) {
                game.resolveStack()
            }

            withClue(
                "ghoul dead=${!game.isOnBattlefield("Diregraf Ghoul")} " +
                    "life2=${game.getLifeTotal(2)} stack=${game.state.stack.size} " +
                    "pending=${game.state.pendingDecision}",
            ) {
                game.isOnBattlefield("Diregraf Ghoul") shouldBe false
                game.isOnBattlefield("Diregraf Captain") shouldBe true
                game.getLifeTotal(2) shouldBe 19
            }
        }
    }
}
