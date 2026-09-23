package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

/**
 * Repurposed Enforcer (Reality Fracture #19) — {1}{W} 3/2:
 *   Whenever this creature attacks, empower Jace X, where X is the number of creatures you control.
 *
 * Pins that X counts every creature you control as the trigger resolves (the Enforcer included),
 * both when the Jace token has to be created and when it already exists.
 */
class RepurposedEnforcerScenarioTest : ScenarioTestBase() {
    init {
        fun TestGame.jaceLoyalty(): Int {
            val jace = findPermanents("Jace").single()
            return state.getEntity(jace)?.get<CountersComponent>()?.getCount(CounterType.LOYALTY) ?: 0
        }

        test("attacking with three creatures in play creates a Jace token with three loyalty") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Repurposed Enforcer")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withCardInLibrary(1, "Plains")
                .withCardInLibrary(2, "Plains")
                .withActivePlayer(1)
                .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                .build()

            game.declareAttackers(mapOf("Repurposed Enforcer" to 2)).error shouldBe null
            game.resolveStack()

            game.jaceLoyalty() shouldBe 3
        }

        test("X counts the Enforcer itself along with each other creature you control") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Repurposed Enforcer")
                .withCardOnBattlefield(1, "Arcane Amphisbaena")
                .withCardInLibrary(1, "Plains")
                .withCardInLibrary(2, "Plains")
                .withActivePlayer(1)
                .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                .build()

            game.declareAttackers(mapOf("Repurposed Enforcer" to 2, "Arcane Amphisbaena" to 2)).error shouldBe null
            game.resolveStack()

            game.findPermanents("Jace").size shouldBe 1
            game.jaceLoyalty() shouldBe 2
        }
    }
}
