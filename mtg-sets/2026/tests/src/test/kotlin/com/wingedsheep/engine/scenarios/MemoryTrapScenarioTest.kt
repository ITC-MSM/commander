package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class MemoryTrapScenarioTest : ScenarioTestBase() {
    init {
        test("source leaving in response prevents the exile") {
            val game = scenario().withPlayers().withCardInHand(1, "Memory Trap")
                .withCardInHand(1, "Disenchant").withCardOnBattlefield(2, "Grizzly Bears")
                .withLandsOnBattlefield(1, "Plains", 5)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            val bears = game.findPermanent("Grizzly Bears")!!
            game.castSpell(1, "Memory Trap").error shouldBe null
            game.resolveStack()
            game.selectTargets(listOf(bears)).error shouldBe null
            game.castSpell(1, "Disenchant", game.findPermanent("Memory Trap")!!).error shouldBe null
            game.resolveStack()
            game.isOnBattlefield("Grizzly Bears") shouldBe true
            game.isInExile(2, "Grizzly Bears") shouldBe false
        }
        test("exiled permanent returns immediately when the enchantment leaves") {
            val game = scenario().withPlayers().withCardInHand(1, "Memory Trap")
                .withCardInHand(1, "Disenchant").withCardOnBattlefield(2, "Grizzly Bears")
                .withLandsOnBattlefield(1, "Plains", 5)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            val bears = game.findPermanent("Grizzly Bears")!!
            game.castSpell(1, "Memory Trap").error shouldBe null
            game.resolveStack()
            game.selectTargets(listOf(bears)).error shouldBe null
            game.resolveStack()
            game.isInExile(2, "Grizzly Bears") shouldBe true
            game.castSpell(1, "Disenchant", game.findPermanent("Memory Trap")!!).error shouldBe null
            game.resolveStack()
            game.isOnBattlefield("Grizzly Bears") shouldBe true
            game.state.stack.isEmpty() shouldBe true
        }
    }
}
