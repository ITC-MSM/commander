package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

/**
 * Vigor (LRW #240) — damage to another creature you control is prevented, and that creature gets
 * a +1/+1 counter per point prevented, as part of the prevention.
 */
class VigorScenarioTest : ScenarioTestBase() {
    init {
        fun base() = scenario().withPlayers("P1", "P2")
            .withCardOnBattlefield(1, "Vigor")
            .withActivePlayer(2).inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

        fun TestGame.plusCounters(name: String): Int =
            state.getEntity(findPermanent(name)!!)?.get<CountersComponent>()
                ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

        test("lethal burn on your creature becomes counters and it survives") {
            val game = base().withCardOnBattlefield(1, "Grizzly Bears")
                .withCardInHand(2, "Lightning Bolt")
                .withLandsOnBattlefield(2, "Mountain", 1).build()
            game.castSpell(2, "Lightning Bolt", game.findPermanent("Grizzly Bears")!!).error shouldBe null
            game.resolveStack()
            game.isOnBattlefield("Grizzly Bears") shouldBe true
            game.plusCounters("Grizzly Bears") shouldBe 3
            game.state.projectedState.getPower(game.findPermanent("Grizzly Bears")!!) shouldBe 5
        }

        test("each creature you control gets counters for its own prevented damage; opponents' don't") {
            val game = base().withCardOnBattlefield(1, "Grizzly Bears")
                .withCardOnBattlefield(2, "Hill Giant")
                .withCardInHand(2, "Pyroclasm")
                .withLandsOnBattlefield(2, "Mountain", 2).build()
            game.castSpell(2, "Pyroclasm").error shouldBe null
            game.resolveStack()
            game.plusCounters("Grizzly Bears") shouldBe 2
            game.plusCounters("Vigor") shouldBe 0
            (game.state.getEntity(game.findPermanent("Hill Giant")!!)?.get<DamageComponent>()?.amount ?: 0) shouldBe 2
        }

        test("Vigor itself is dealt damage normally") {
            val game = base().withCardInHand(2, "Lightning Bolt")
                .withLandsOnBattlefield(2, "Mountain", 1).build()
            game.castSpell(2, "Lightning Bolt", game.findPermanent("Vigor")!!).error shouldBe null
            game.resolveStack()
            game.plusCounters("Vigor") shouldBe 0
            (game.state.getEntity(game.findPermanent("Vigor")!!)?.get<DamageComponent>()?.amount ?: 0) shouldBe 3
        }

        test("combat damage to a blocking creature becomes counters") {
            val game = base().withCardOnBattlefield(1, "Grizzly Bears")
                .withCardOnBattlefield(2, "Hill Giant").build()
            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            game.declareAttackers(mapOf("Hill Giant" to 1)).error shouldBe null
            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
            game.declareBlockers(mapOf("Grizzly Bears" to listOf("Hill Giant"))).error shouldBe null
            game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
            game.isOnBattlefield("Grizzly Bears") shouldBe true
            game.plusCounters("Grizzly Bears") shouldBe 3
        }

        test("damage that can't be prevented is dealt and places no counters") {
            val game = base().withCardOnBattlefield(1, "Grizzly Bears")
                .withCardOnBattlefield(2, "Sunspine Lynx")
                .withCardInHand(2, "Lightning Bolt")
                .withLandsOnBattlefield(2, "Mountain", 1).build()
            game.castSpell(2, "Lightning Bolt", game.findPermanent("Grizzly Bears")!!).error shouldBe null
            game.resolveStack()
            game.isInGraveyard(1, "Grizzly Bears") shouldBe true
        }
    }
}
