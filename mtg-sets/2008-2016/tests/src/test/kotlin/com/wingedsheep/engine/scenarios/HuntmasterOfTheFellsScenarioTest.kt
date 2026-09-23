package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.scripting.effects.TransformEffect
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Huntmaster of the Fells // Ravager of the Fells (DKA #140).
 *
 * Ravager: "Whenever this creature transforms into Ravager of the Fells, it deals 2 damage to target
 * opponent or planeswalker and 2 damage to up to one target creature that player or that
 * planeswalker's controller controls."
 *
 * The creature slot's legal set depends on the first target, so the two are chosen in order.
 */
class HuntmasterOfTheFellsScenarioTest : ScenarioTestBase() {

    init {
        cardRegistry.register(card("Test Moonmist") {
            manaCost = "{0}"
            typeLine = "Instant"
            spell {
                val creature = target("target creature", Targets.Creature)
                effect = TransformEffect(creature)
            }
        })

        fun transformGame() = scenario()
            .withPlayers("Player1", "Player2")
            .withCardOnBattlefield(1, "Huntmaster of the Fells", summoningSickness = false)
            .withCardOnBattlefield(1, "Hill Giant")
            .withCardOnBattlefield(2, "Grizzly Bears")
            .withCardInHand(1, "Test Moonmist")
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

        fun ScenarioTestBase.TestGame.transform() {
            val huntmaster = findPermanent("Huntmaster of the Fells")!!
            castSpell(1, "Test Moonmist", targetId = huntmaster).error shouldBe null
            if (getPendingDecision() is SelectManaSourcesDecision) submitManaSourcesAutoPay()
            resolveStack()
            state.getEntity(huntmaster)!!.get<CardComponent>()!!.name shouldBe "Ravager of the Fells"
        }

        context("Ravager of the Fells transform trigger") {

            test("after choosing the opponent, their creatures are offered and the chosen one takes 2") {
                val game = transformGame().build()
                game.transform()

                val first = game.getPendingDecision().shouldBeInstanceOf<ChooseTargetsDecision>()
                first.legalTargets[0]!! shouldContainExactlyInAnyOrder listOf(game.player2Id)
                game.selectTargets(listOf(game.player2Id)).error shouldBe null

                val bears = game.findPermanent("Grizzly Bears")!!
                val second = game.getPendingDecision().shouldBeInstanceOf<ChooseTargetsDecision>()
                withClue("only creatures the targeted opponent controls are legal") {
                    second.legalTargets[0]!! shouldContainExactlyInAnyOrder listOf(bears)
                    second.targetRequirements.single().minTargets shouldBe 0
                }
                game.selectTargets(listOf(bears)).error shouldBe null
                game.resolveStack()

                game.getLifeTotal(2) shouldBe 18
                game.isInGraveyard(2, "Grizzly Bears") shouldBe true
            }

            test("the creature is optional — declining it still damages the opponent") {
                val game = transformGame().build()
                game.transform()

                game.selectTargets(listOf(game.player2Id)).error shouldBe null
                game.skipTargets().error shouldBe null
                game.resolveStack()

                game.getLifeTotal(2) shouldBe 18
                game.isInGraveyard(2, "Grizzly Bears") shouldBe false
            }

            test("targeting a planeswalker offers creatures its controller controls") {
                val game = transformGame()
                    .withCardOnBattlefield(2, "Chandra Nalaar")
                    .build()
                game.transform()

                val chandra = game.findPermanent("Chandra Nalaar")!!
                val first = game.getPendingDecision().shouldBeInstanceOf<ChooseTargetsDecision>()
                first.legalTargets[0]!! shouldContainExactlyInAnyOrder listOf(game.player2Id, chandra)
                game.selectTargets(listOf(chandra)).error shouldBe null

                val bears = game.findPermanent("Grizzly Bears")!!
                val second = game.getPendingDecision().shouldBeInstanceOf<ChooseTargetsDecision>()
                second.legalTargets[0]!! shouldContainExactlyInAnyOrder listOf(bears)
                game.selectTargets(listOf(bears)).error shouldBe null
                game.resolveStack()

                game.getLifeTotal(2) shouldBe 20
                game.isInGraveyard(2, "Grizzly Bears") shouldBe true
            }
        }
    }
}
