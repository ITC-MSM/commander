package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

/**
 * Titanic Growth — {1}{G} Instant — "Target creature gets +4/+4 until end of turn."
 *
 * Canonical script lives in M12; WOE is a reprint row. Verifies the pump lands on the target and
 * is a one-turn buff (present in the same-turn projected state).
 */
class TitanicGrowthScenarioTest : ScenarioTestBase() {

    init {
        test("pumps the target creature by +4/+4") {
            val g = scenario()
                .withPlayers()
                .withCardInHand(1, "Titanic Growth")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withLandsOnBattlefield(1, "Forest", 2)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .withActivePlayer(1)
                .withPriorityPlayer(1)
                .build()

            val bears = g.findPermanent("Grizzly Bears")!!
            g.state.projectedState.getPower(bears) shouldBe 2
            g.state.projectedState.getToughness(bears) shouldBe 2

            g.castSpell(1, "Titanic Growth", bears).error shouldBe null
            g.resolveStack()

            g.state.projectedState.getPower(bears) shouldBe 6
            g.state.projectedState.getToughness(bears) shouldBe 6
        }
    }
}
