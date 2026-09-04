package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.AbilityActivatedThisTurnComponent
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.stack.ActivatedAbilityOnStackComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf

class ArlinnKordScenarioTest : ScenarioTestBase() {

    private fun seedLoyalty(game: TestGame, id: EntityId, amount: Int) {
        game.state = game.state.updateEntity(id) { container ->
            container
                .with(CountersComponent().withAdded(CounterType.LOYALTY, amount))
                .without<AbilityActivatedThisTurnComponent>()
        }
    }

    init {
        test("emblem grants its tap ability to creatures you control") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Arlinn Kord")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val arlinn = game.findPermanent("Arlinn Kord")!!
            val frontTransform = cardRegistry.getCard("Arlinn Kord")!!.script.activatedAbilities[1]
            game.execute(ActivateAbility(game.player1Id, arlinn, frontTransform.id))
            game.resolveStack()

            seedLoyalty(game, arlinn, 6)
            val ultimate = cardRegistry.getCard("Arlinn Kord")!!.backFace!!.script.activatedAbilities[2]
            game.execute(ActivateAbility(game.player1Id, arlinn, ultimate.id))
            game.resolveStack()

            val bear = game.findPermanent("Grizzly Bears")!!
            val granted = game.getLegalActions(1).firstOrNull { legal ->
                (legal.action as? ActivateAbility)?.sourceId == bear &&
                    legal.description.contains("damage equal to its power")
            }
            withClue("Arlinn's emblem should make the Bear's damage ability activatable") {
                granted shouldNotBe null
            }

            val action = granted.shouldNotBeNull().action.shouldBeInstanceOf<ActivateAbility>()
            val activation = game.execute(
                action.copy(targets = listOf(ChosenTarget.Player(game.player2Id)))
            )
            withClue("an ability emitted as legal must pass activation validation") {
                activation.error shouldBe null
            }

            val onStack = game.state.getEntity(game.state.stack.last())
                ?.get<ActivatedAbilityOnStackComponent>()
                .shouldNotBeNull()
            withClue("the emblem supplies routing, not the Bear's definition identity") {
                onStack.abilityIdentity shouldBe null
                onStack.activatedAbilityId shouldBe action.abilityId
            }
            game.state.getEntity(bear)?.has<TappedComponent>() shouldBe true

            game.resolveStack()
            withClue("the Bear deals damage equal to its power") {
                game.getLifeTotal(2) shouldBe 18
            }
        }
    }
}
