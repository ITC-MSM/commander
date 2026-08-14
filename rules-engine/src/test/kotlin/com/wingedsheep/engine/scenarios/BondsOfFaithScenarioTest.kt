package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

class BondsOfFaithScenarioTest : ScenarioTestBase() {
    init {
        test("Human host gets +2/+2") {
            val game = scenario()
                .withPlayers("P1", "P2")
                .withCardOnBattlefield(1, "Glory Seeker")
                .withCardAttachedTo(1, "Bonds of Faith", "Glory Seeker")
                .build()

            val human = game.findPermanent("Glory Seeker")!!
            // Glory Seeker is 2/2; Human branch is +2/+2 → 4/4.
            game.state.projectedState.getPower(human) shouldBe 4
            game.state.projectedState.getToughness(human) shouldBe 4
            game.state.projectedState.cantAttack(human) shouldBe false
            game.state.projectedState.cantBlock(human) shouldBe false
        }

        test("non-Human host can't attack or block") {
            val game = scenario()
                .withPlayers("P1", "P2")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withCardAttachedTo(1, "Bonds of Faith", "Grizzly Bears")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val bears = game.findPermanent("Grizzly Bears")!!
            withClue("non-Human should not get the +2/+2") {
                game.state.projectedState.getPower(bears) shouldBe 2
                game.state.projectedState.getToughness(bears) shouldBe 2
            }
            game.state.projectedState.cantAttack(bears) shouldBe true
            game.state.projectedState.cantBlock(bears) shouldBe true
        }
    }
}
