package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe
import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.mtg.sets.definitions.fra.cards.GreenhousePropagator

class GreenhousePropagatorScenarioTest : ScenarioTestBase() {
    init {
        test("ignores its own entry and opposing creatures but rewards another creature you control") {
            val game = scenario().withPlayers()
                .withCardInHand(1, "Greenhouse Propagator")
                .withCardInHand(1, "Grizzly Bears")
                .withCardInHand(2, "Spectral Sailor")
                .withLandsOnBattlefield(1, "Forest", 5)
                .withLandsOnBattlefield(2, "Island", 1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            game.castSpell(1, "Greenhouse Propagator").error shouldBe null
            game.resolveStack()
            game.getLifeTotal(1) shouldBe 20
            game.passPriority()
            game.castSpell(2, "Spectral Sailor").error shouldBe null
            game.resolveStack()
            game.getLifeTotal(1) shouldBe 20
            game.passPriority()
            game.castSpell(1, "Grizzly Bears").error shouldBe null
            game.resolveStack()
            game.getLifeTotal(1) shouldBe 21
        }
        test("its tap ability supplies green mana immediately") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Greenhouse Propagator")
                .withCardInHand(1, "Llanowar Elves")
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            val propagator = game.findPermanent("Greenhouse Propagator")!!
            game.execute(ActivateAbility(game.player1Id, propagator,
                GreenhousePropagator.script.activatedAbilities.single().id)).error shouldBe null
            game.state.getEntity(propagator)!!.has<TappedComponent>() shouldBe true
            game.castSpell(1, "Llanowar Elves").error shouldBe null
            game.resolveStack()
            game.getLifeTotal(1) shouldBe 21
        }
    }
}
