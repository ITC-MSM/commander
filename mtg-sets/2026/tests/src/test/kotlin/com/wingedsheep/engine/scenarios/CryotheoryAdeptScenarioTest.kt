package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class CryotheoryAdeptScenarioTest : ScenarioTestBase() {
    init {
        test("graveyard ability exiles its source and taps and stuns its target") {
            val game = scenario().withPlayers().withCardInGraveyard(1, "Cryotheory Adept")
                .withCardOnBattlefield(2, "Grizzly Bears").withLandsOnBattlefield(1, "Island", 4)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            val source = game.findCardsInGraveyard(1, "Cryotheory Adept").single()
            val target = game.findPermanent("Grizzly Bears")!!
            val ability = cardRegistry.getCard("Cryotheory Adept")!!.script.activatedAbilities.single().id
            game.execute(ActivateAbility(game.player1Id, source, ability, listOf(ChosenTarget.Permanent(target)))).error shouldBe null
            game.isInExile(1, "Cryotheory Adept") shouldBe true
            game.resolveStack()
            game.state.getEntity(target)!!.has<TappedComponent>() shouldBe true
            game.state.getEntity(target)!!.get<CountersComponent>()!!.getCount(CounterType.STUN) shouldBe 1
        }
    }
}
