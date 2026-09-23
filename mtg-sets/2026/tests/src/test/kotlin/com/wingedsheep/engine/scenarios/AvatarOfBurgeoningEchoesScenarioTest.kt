package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.PlayLand
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

/**
 * Avatar of Burgeoning Echoes (Reality Fracture #122) — {G}{U} 2/3:
 *   Landfall — Whenever a land you control enters, empower Jace 2.
 *   Planeswalkers you control have "[−10]: Put a +1/+1 counter on target creature for each land
 *   you control."
 *
 * Pins the landfall empower, that the granted −10 isn't offered below ten loyalty, and that once
 * affordable it puts one +1/+1 counter per land you control on the target.
 */
class AvatarOfBurgeoningEchoesScenarioTest : ScenarioTestBase() {
    init {
        test("landfall empowers Jace, and the granted −10 counts lands you control") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Avatar of Burgeoning Echoes")
                .withLandsOnBattlefield(1, "Forest", 3)
                .withCardInHand(1, "Island")
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(2, "Island")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            game.execute(PlayLand(game.player1Id, game.findCardsInHand(1, "Island").single())).error shouldBe null
            game.resolveStack()

            val jace = game.findPermanents("Jace").single()
            fun loyalty() = game.state.getEntity(jace)?.get<CountersComponent>()?.getCount(CounterType.LOYALTY) ?: 0
            loyalty() shouldBe 2

            fun grantedAction() = game.getLegalActions(1).firstOrNull {
                (it.action as? ActivateAbility)?.sourceId == jace && it.description.contains("+1/+1 counter")
            }
            // Two loyalty can't pay −10.
            (grantedAction()?.isAffordable ?: false) shouldBe false

            game.state = game.state.updateEntity(jace) { c ->
                c.with(c.get<CountersComponent>()!!.withAdded(CounterType.LOYALTY, 8))
            }
            val avatar = game.findPermanent("Avatar of Burgeoning Echoes")!!
            val action = grantedAction()!!.action as ActivateAbility
            game.execute(action.copy(targets = listOf(ChosenTarget.Permanent(avatar)))).error shouldBe null
            game.resolveStack()

            loyalty() shouldBe 0
            // Four lands: three Forests and the Island.
            game.state.getEntity(avatar)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 4
        }
    }
}
