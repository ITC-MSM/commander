package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.identity.FaceDownComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Break Under Pressure (FRA #50) — the target opponent sacrifices a creature or planeswalker tied
 * for the greatest mana value among those they control; you gain 2 life either way.
 */
class BreakUnderPressureScenarioTest : ScenarioTestBase() {

    init {
        cardRegistry.register(testCreature("Four-Mana Test Creature", "{4}"))
        cardRegistry.register(testCreature("Other Four-Mana Test Creature", "{2}{B}{B}"))
        cardRegistry.register(testCreature("One-Mana Test Creature", "{1}"))

        fun builder(vararg opponentCreatures: String): ScenarioBuilder {
            var b = scenario()
                .withPlayers("Player", "Opponent")
                .withCardInHand(1, "Break Under Pressure")
                .withLandsOnBattlefield(1, "Swamp", 3)
                .withCardInLibrary(1, "Swamp")
                .withCardInLibrary(2, "Swamp")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            opponentCreatures.forEach { b = b.withCardOnBattlefield(2, it) }
            return b
        }

        context("Break Under Pressure") {
            test("the opponent's greatest-mana-value creature is sacrificed and you gain 2 life") {
                val game = builder("One-Mana Test Creature", "Four-Mana Test Creature", "Grizzly Bears").build()

                game.castSpellTargetingPlayer(1, "Break Under Pressure", 2).error shouldBe null
                game.resolveStack()

                game.isOnBattlefield("Four-Mana Test Creature") shouldBe false
                game.isOnBattlefield("Grizzly Bears") shouldBe true
                game.isOnBattlefield("One-Mana Test Creature") shouldBe true
                game.getLifeTotal(1) shouldBe 22
            }

            test("with a tie, the opponent chooses which of the tied creatures to sacrifice") {
                val game = builder("Four-Mana Test Creature", "Other Four-Mana Test Creature", "Grizzly Bears").build()

                game.castSpellTargetingPlayer(1, "Break Under Pressure", 2).error shouldBe null
                game.resolveStack()

                val decision = game.getPendingDecision().shouldNotBeNull()
                withClue("the opponent makes the choice") { decision.playerId shouldBe game.player2Id }
                val four = game.findPermanent("Four-Mana Test Creature").shouldNotBeNull()
                val otherFour = game.findPermanent("Other Four-Mana Test Creature").shouldNotBeNull()
                withClue("only the two tied creatures are offered") {
                    decision.shouldBeInstanceOf<SelectCardsDecision>().options
                        .shouldContainExactlyInAnyOrder(four, otherFour)
                }
                game.selectCards(listOf(otherFour)).error shouldBe null
                game.resolveStack()

                game.isOnBattlefield("Other Four-Mana Test Creature") shouldBe false
                game.isOnBattlefield("Four-Mana Test Creature") shouldBe true
                game.getLifeTotal(1) shouldBe 22
            }

            test("a face-down creature counts as mana value 0") {
                val game = builder("Four-Mana Test Creature", "Grizzly Bears").build()
                val hidden = game.findPermanent("Four-Mana Test Creature").shouldNotBeNull()
                val container = game.state.getEntity(hidden).shouldNotBeNull()
                game.state = game.state.withEntity(hidden, container.with(FaceDownComponent))

                game.castSpellTargetingPlayer(1, "Break Under Pressure", 2).error shouldBe null
                game.resolveStack()

                withClue("the face-down four-drop is a 0 — the Bears are the greatest") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                    game.state.getBattlefield().contains(hidden) shouldBe true
                }
            }

            test("with nothing to sacrifice, you still gain 2 life") {
                val game = builder().build()

                game.castSpellTargetingPlayer(1, "Break Under Pressure", 2).error shouldBe null
                game.resolveStack()

                game.getLifeTotal(1) shouldBe 22
            }
        }
    }

    private fun testCreature(name: String, cost: String): CardDefinition = CardDefinition.creature(
        name = name,
        manaCost = ManaCost.parse(cost),
        subtypes = setOf(Subtype("Construct")),
        power = 1,
        toughness = 1
    )
}
