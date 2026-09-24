package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Way of the Cryomancer (Reality Fracture #223) — {2}{U} Legendary Enchantment:
 *   When Way of the Cryomancer enters, empower Jace 5.
 *   Planeswalkers you control have "[−3]: When you next cast an instant or sorcery spell this
 *   turn, copy that spell. You may choose new targets for the copy."
 *
 * Pins the empower, that the granted −3 is activatable on the Jace token and costs three loyalty,
 * and that the next instant cast afterwards is copied (a Lightning Bolt deals 6 in total).
 */
class WayOfTheCryomancerScenarioTest : ScenarioTestBase() {
    init {
        test("the granted −3 copies the next instant you cast this turn") {
            val game = scenario().withPlayers()
                .withCardInHand(1, "Way of the Cryomancer")
                .withCardInHand(1, "Lightning Bolt")
                .withLandsOnBattlefield(1, "Island", 3)
                .withLandsOnBattlefield(1, "Mountain", 1)
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(2, "Island")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            game.castSpell(1, "Way of the Cryomancer").error shouldBe null
            game.resolveStack()

            val jace = game.findPermanents("Jace").single()
            fun loyalty() = game.state.getEntity(jace)?.get<CountersComponent>()?.getCount(CounterType.LOYALTY) ?: 0
            loyalty() shouldBe 5

            val granted = game.getLegalActions(1)
                .first { (it.action as? ActivateAbility)?.sourceId == jace && it.description.contains("copy that spell") }
            game.execute(granted.action).error shouldBe null
            loyalty() shouldBe 2
            game.resolveStack()

            withClue("the −3 set up a one-shot copy of the next instant or sorcery") {
                game.state.pendingSpellCopies.size shouldBe 1
            }

            game.castSpellTargetingPlayer(1, "Lightning Bolt", 2).error shouldBe null
            withClue("casting the instant consumed the pending copy") {
                game.state.pendingSpellCopies.size shouldBe 0
            }
            // The copy trigger offers new targets; aim the copy at the same opponent.
            repeat(10) {
                game.resolveStack()
                val decision = game.getPendingDecision()
                if (decision is ChooseTargetsDecision) game.selectTargets(listOf(game.player2Id)).error shouldBe null
            }

            withClue("the Bolt and its copy each dealt 3") {
                game.getLifeTotal(2) shouldBe 14
            }
        }
    }
}
