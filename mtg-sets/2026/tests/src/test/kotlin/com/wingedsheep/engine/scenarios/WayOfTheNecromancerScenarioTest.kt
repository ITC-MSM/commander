package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

/**
 * Way of the Necromancer (Reality Fracture #239) — {1}{B} Legendary Enchantment:
 *   When Way of the Necromancer enters, empower Jace 2.
 *   Whenever a creature you control dies, put a loyalty counter on each planeswalker you control.
 *
 * Pins that the death trigger reaches every planeswalker you control — the Jace token and a
 * nontoken planeswalker alike — and ignores an opponent's creature dying.
 */
class WayOfTheNecromancerScenarioTest : ScenarioTestBase() {
    init {
        test("a creature you control dying puts a loyalty counter on each of your planeswalkers") {
            val game = scenario().withPlayers()
                .withCardInHand(1, "Way of the Necromancer")
                .withCardInHand(1, "Lightning Bolt")
                .withCardInHand(1, "Lightning Bolt")
                .withLandsOnBattlefield(1, "Swamp", 2)
                .withLandsOnBattlefield(1, "Mountain", 3)
                .withCardOnBattlefield(1, "Ajani Goldmane")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withCardOnBattlefield(2, "Grizzly Bears")
                .withCardInLibrary(1, "Swamp")
                .withCardInLibrary(2, "Swamp")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            game.castSpell(1, "Way of the Necromancer").error shouldBe null
            game.resolveStack()

            val jace = game.findPermanents("Jace").single()
            val ajani = game.findPermanent("Ajani Goldmane")!!
            fun loyalty(id: com.wingedsheep.sdk.model.EntityId) =
                game.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.LOYALTY) ?: 0
            loyalty(jace) shouldBe 2
            loyalty(ajani) shouldBe 4

            val (mine, theirs) = game.findPermanents("Grizzly Bears")
                .partition { game.state.projectedState.getController(it) == game.player1Id }

            game.castSpell(1, "Lightning Bolt", theirs.single()).error shouldBe null
            game.resolveStack()
            loyalty(jace) shouldBe 2
            loyalty(ajani) shouldBe 4

            game.castSpell(1, "Lightning Bolt", mine.single()).error shouldBe null
            game.resolveStack()
            loyalty(jace) shouldBe 3
            loyalty(ajani) shouldBe 5
        }
    }
}
