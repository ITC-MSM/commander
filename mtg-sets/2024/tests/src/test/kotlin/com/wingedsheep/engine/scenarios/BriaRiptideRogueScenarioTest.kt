package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.OrderObjectsDecision
import com.wingedsheep.engine.core.OrderedResponse
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Bria, Riptide Rogue (BLB #379) — {2}{U}{R} 3/3 Legendary Otter Rogue.
 *
 * - Prowess (own +1/+1 on noncreature cast).
 * - Other creatures you control have prowess.
 * - Whenever you cast a noncreature spell, target creature you control can't be blocked this turn.
 *
 * Verifies the prowess grant actually fires the +1/+1 triggered ability on other creatures (not
 * just the cosmetic keyword), accumulates per noncreature cast, ignores creature spells, and that
 * the cast trigger grants CANT_BE_BLOCKED to its chosen target.
 */
class BriaRiptideRogueScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    /**
     * Cast a noncreature spell and fully resolve the resulting triggers, ordering any
     * simultaneous triggers in their default order and pointing Bria's unblockable trigger at
     * [unblockableTarget].
     */
    private fun TestGame.castNoncreatureResolving(spell: String, unblockableTarget: EntityId) {
        castSpell(1, spell).error shouldBe null
        resolveStack()
        var guard = 0
        while (getPendingDecision() != null && guard++ < 12) {
            when (val decision = getPendingDecision()!!) {
                is OrderObjectsDecision -> submitDecision(OrderedResponse(decision.id, decision.objects))
                else -> selectTargets(listOf(unblockableTarget))
            }
            resolveStack()
        }
    }

    init {
        cardRegistry.register(
            CardDefinition.instant(
                name = "Test Bolt",
                manaCost = ManaCost.parse("{R}"),
                oracleText = ""
            )
        )
        cardRegistry.register(
            CardDefinition.creature(
                name = "Test Bear",
                manaCost = ManaCost.parse("{1}{G}"),
                subtypes = setOf(Subtype("Bear")),
                power = 2,
                toughness = 2
            )
        )

        context("Bria, Riptide Rogue") {

            test("Bria's own prowess pumps her +1/+1 on a noncreature cast") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Bria, Riptide Rogue", summoningSickness = false)
                    .withCardInHand(1, "Test Bolt")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bria = game.findPermanent("Bria, Riptide Rogue")!!
                game.castNoncreatureResolving("Test Bolt", bria)

                val projected = projector.project(game.state)
                withClue("Bria is 4/4 after one noncreature spell") {
                    projected.getPower(bria) shouldBe 4
                    projected.getToughness(bria) shouldBe 4
                }
            }

            test("other creatures you control gain prowess and pump per noncreature cast") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Bria, Riptide Rogue", summoningSickness = false)
                    .withCardOnBattlefield(1, "Test Bear", summoningSickness = false)
                    .withCardsInHand(1, "Test Bolt", 2)
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bria = game.findPermanent("Bria, Riptide Rogue")!!
                val bear = game.findPermanent("Test Bear")!!

                withClue("the bear has the granted prowess keyword before any cast") {
                    projector.project(game.state).hasKeyword(bear, Keyword.PROWESS) shouldBe true
                }

                game.castNoncreatureResolving("Test Bolt", bria)
                game.castNoncreatureResolving("Test Bolt", bria)

                withClue("the granted prowess triggered ability fired for each of the two casts") {
                    val projected = projector.project(game.state)
                    projected.getPower(bear) shouldBe 4
                    projected.getToughness(bear) shouldBe 4
                }
            }

            test("a creature spell does not trigger prowess") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Bria, Riptide Rogue", summoningSickness = false)
                    .withCardInHand(1, "Test Bear")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bria = game.findPermanent("Bria, Riptide Rogue")!!
                game.castSpell(1, "Test Bear").error shouldBe null
                game.resolveStack()

                withClue("casting a creature spell leaves Bria at base 3/3") {
                    val projected = projector.project(game.state)
                    projected.getPower(bria) shouldBe 3
                    projected.getToughness(bria) shouldBe 3
                }
            }

            test("the cast trigger grants CANT_BE_BLOCKED to a target creature you control") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Bria, Riptide Rogue", summoningSickness = false)
                    .withCardInHand(1, "Test Bolt")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bria = game.findPermanent("Bria, Riptide Rogue")!!
                withClue("no evasion before the spell is cast") {
                    projector.project(game.state).hasKeyword(bria, AbilityFlag.CANT_BE_BLOCKED) shouldBe false
                }

                game.castNoncreatureResolving("Test Bolt", bria)

                withClue("the unblockable trigger resolved onto Bria") {
                    projector.project(game.state).hasKeyword(bria, AbilityFlag.CANT_BE_BLOCKED) shouldBe true
                }
            }
        }
    }
}
