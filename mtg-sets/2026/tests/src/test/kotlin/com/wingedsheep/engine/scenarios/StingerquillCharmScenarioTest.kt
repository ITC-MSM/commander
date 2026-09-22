package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class StingerquillCharmScenarioTest : ScenarioTestBase() {
    init {
        test("deals 3 damage to a creature") {
            val game = scenario().withPlayers()
                .withCardInHand(1, "Stingerquill Charm")
                .withCardOnBattlefield(2, "Hill Giant")
                .withLandsOnBattlefield(1, "Swamp", 1)
                .withLandsOnBattlefield(1, "Mountain", 1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            game.castSpellWithMode(1, "Stingerquill Charm", 0, game.findPermanent("Hill Giant")!!).error shouldBe null
            game.resolveStack()
            game.isInGraveyard(2, "Hill Giant") shouldBe true
        }

        test("grants first strike and deathtouch until end of turn") {
            val game = scenario().withPlayers()
                .withCardInHand(1, "Stingerquill Charm")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withLandsOnBattlefield(1, "Swamp", 1)
                .withLandsOnBattlefield(1, "Mountain", 1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            val bears = game.findPermanent("Grizzly Bears")!!
            game.castSpellWithMode(1, "Stingerquill Charm", 1, bears).error shouldBe null
            game.resolveStack()
            game.state.projectedState.hasKeyword(bears, Keyword.FIRST_STRIKE) shouldBe true
            game.state.projectedState.hasKeyword(bears, Keyword.DEATHTOUCH) shouldBe true
        }

        test("creates a colorless 2/2 Cadet that has haste") {
            val game = scenario().withPlayers()
                .withCardInHand(1, "Stingerquill Charm")
                .withLandsOnBattlefield(1, "Swamp", 1)
                .withLandsOnBattlefield(1, "Mountain", 1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            game.castSpellWithMode(1, "Stingerquill Charm", 2).error shouldBe null
            game.resolveStack()
            val cadet = game.findPermanent("Cadet")!!
            game.state.projectedState.getColors(cadet) shouldBe emptySet()
            game.state.projectedState.getPower(cadet) shouldBe 2
            game.state.projectedState.getToughness(cadet) shouldBe 2
            game.state.projectedState.hasKeyword(cadet, Keyword.HASTE) shouldBe true
        }
    }
}
