package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.msh.cards.AntMansArmy
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Ant-Man's Army (MSH #161) — {2}{G} Creature — Insect 3/2.
 *
 *   When this creature enters, create a Food token or a Treasure token.
 *
 * The "X or Y" is a resolution-time [com.wingedsheep.sdk.scripting.effects.ModalEffect.chooseOne]
 * hung off an enters trigger, so the thing worth proving is that answering the mode decision
 * actually mints the token — a modal *trigger* that offers the choice, clears the decision and then
 * creates nothing would look identical from the outside.
 */
class AntMansArmyScenarioTest : ScenarioTestBase() {

    private val foodMode = "Create a Food token"
    private val treasureMode = "Create a Treasure token"

    private fun TestGame.chooseMode(decision: ChooseOptionDecision, description: String) {
        val index = decision.options.indexOf(description)
        check(index >= 0) { "Mode '$description' not offered; options=${decision.options}" }
        submitDecision(OptionChosenResponse(decision.id, index)).error shouldBe null
    }

    /** Casts the Insect and answers its enters trigger with [mode]. Returns the Insect. */
    private fun TestGame.playArmyChoosing(mode: String): EntityId {
        castSpell(1, "Ant-Man's Army").error shouldBe null
        resolveStack()

        val decision = getPendingDecision()
        withClue("the enters trigger must offer the two token modes") {
            decision.shouldNotBeNull()
            decision as ChooseOptionDecision
            decision.options shouldContain foodMode
            decision.options shouldContain treasureMode
        }
        chooseMode(decision as ChooseOptionDecision, mode)
        resolveStack()

        withClue("the mode decision must be fully answered") {
            hasPendingDecision() shouldBe false
            state.stack.size shouldBe 0
        }
        return findPermanent("Ant-Man's Army")!!
    }

    private fun TestGame.typeLineOf(id: EntityId) =
        state.getEntity(id)?.get<CardComponent>()?.typeLine

    private fun game() = scenario()
        .withPlayers("Player1", "Player2")
        .withCardInHand(1, "Ant-Man's Army")
        .withLandsOnBattlefield(1, "Forest", 3)
        .withActivePlayer(1)
        .withPriorityPlayer(1)
        .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
        .build()

    init {
        cardRegistry.register(AntMansArmy)

        context("Ant-Man's Army") {

            test("choosing Food puts a Food artifact token on the battlefield") {
                val game = game()
                val army = game.playArmyChoosing(foodMode)

                withClue("the 3/2 Insect body itself entered") {
                    game.state.projectedState.getPower(army) shouldBe 3
                    game.state.projectedState.getToughness(army) shouldBe 2
                    game.typeLineOf(army)!!.isCreature shouldBe true
                    game.typeLineOf(army)!!.subtypes.map { it.value } shouldContain "Insect"
                }

                val food = game.findPermanent("Food")
                withClue("the chosen mode must actually mint the Food token") {
                    food shouldNotBe null
                }
                withClue("the token is an artifact — Food") {
                    game.typeLineOf(food!!)!!.isArtifact shouldBe true
                    game.typeLineOf(food)!!.subtypes.map { it.value } shouldContain "Food"
                }
                withClue("only the chosen mode happened") {
                    game.findPermanent("Treasure") shouldBe null
                    game.findPermanents("Food").size shouldBe 1
                }
            }

            test("choosing Treasure puts a Treasure artifact token on the battlefield") {
                val game = game()
                val army = game.playArmyChoosing(treasureMode)

                withClue("the 3/2 Insect body itself entered") {
                    game.state.projectedState.getPower(army) shouldBe 3
                    game.state.projectedState.getToughness(army) shouldBe 2
                }

                val treasure = game.findPermanent("Treasure")
                withClue("the chosen mode must actually mint the Treasure token") {
                    treasure shouldNotBe null
                }
                withClue("the token is an artifact — Treasure") {
                    game.typeLineOf(treasure!!)!!.isArtifact shouldBe true
                    game.typeLineOf(treasure)!!.subtypes.map { it.value } shouldContain "Treasure"
                }
                withClue("only the chosen mode happened") {
                    game.findPermanent("Food") shouldBe null
                    game.findPermanents("Treasure").size shouldBe 1
                }
            }
        }
    }
}
