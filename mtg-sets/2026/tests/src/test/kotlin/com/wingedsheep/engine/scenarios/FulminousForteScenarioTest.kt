package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class FulminousForteScenarioTest : ScenarioTestBase() {
    init {
        test("sweep mode damages opposing creatures and planeswalkers only") {
            val game = scenario().withPlayers().withCardInHand(1, "Fulminous Forte")
                .withCardOnBattlefield(1, "Llanowar Elves")
                .withCardOnBattlefield(2, "Goblin Piker").withCardOnBattlefield(2, "Jace Beleren")
                .withLandsOnBattlefield(1, "Mountain", 3)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            val jace = game.findPermanent("Jace Beleren")!!
            game.castSpellWithMode(1, "Fulminous Forte", 0).error shouldBe null
            game.resolveStack()
            game.isInGraveyard(2, "Goblin Piker") shouldBe true
            game.isOnBattlefield("Llanowar Elves") shouldBe true
            game.state.getEntity(jace)!!.get<CountersComponent>()!!.getCount(CounterType.LOYALTY) shouldBe 2
            game.getLifeTotal(2) shouldBe 20
        }
        test("targeted mode deals five damage") {
            val game = scenario().withPlayers().withCardInHand(1, "Fulminous Forte")
                .withCardOnBattlefield(2, "Air Elemental").withLandsOnBattlefield(1, "Mountain", 3)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            game.castSpellWithMode(1, "Fulminous Forte", 1, game.findPermanent("Air Elemental")!!).error shouldBe null
            game.resolveStack()
            game.isInGraveyard(2, "Air Elemental") shouldBe true
        }
    }
}
