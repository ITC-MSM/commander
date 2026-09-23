package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

/**
 * Way of the Healer (Reality Fracture #207) — {3}{W} Legendary Enchantment:
 *   When Way of the Healer enters, empower Jace 5.
 *   Planeswalkers you control have "[−2]: Create a 2/2 colorless Wizard Soldier creature token
 *   named Cadet. Surveil 1."
 *
 * Pins the enters trigger and that the granted loyalty ability is activatable on the Jace token,
 * pays two loyalty, makes a Cadet, surveils, and uses up that planeswalker's loyalty activation.
 */
class WayOfTheHealerScenarioTest : ScenarioTestBase() {
    init {
        test("enters to empower Jace 5, and Jace can use the granted −2 to make a Cadet and surveil") {
            val game = scenario().withPlayers()
                .withCardInHand(1, "Way of the Healer")
                .withLandsOnBattlefield(1, "Plains", 4)
                .withCardInLibrary(1, "Plains")
                .withCardInLibrary(1, "Plains")
                .withCardInLibrary(2, "Plains")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            game.castSpell(1, "Way of the Healer").error shouldBe null
            game.resolveStack()

            val jace = game.findPermanents("Jace").single()
            fun loyalty() = game.state.getEntity(jace)?.get<CountersComponent>()?.getCount(CounterType.LOYALTY) ?: 0
            loyalty() shouldBe 5

            val granted = game.getLegalActions(1)
                .first { (it.action as? ActivateAbility)?.sourceId == jace && it.description.contains("Cadet") }
            game.execute(granted.action).error shouldBe null
            loyalty() shouldBe 3
            game.resolveStack()
            if (game.hasPendingDecision()) game.skipSelection()

            game.findPermanents("Cadet").size shouldBe 1
            game.getLegalActions(1).none { (it.action as? ActivateAbility)?.sourceId == jace } shouldBe true
        }
    }
}
