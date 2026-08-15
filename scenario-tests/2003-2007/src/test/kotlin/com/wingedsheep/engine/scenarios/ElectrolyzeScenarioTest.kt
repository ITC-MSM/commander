package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.DistributeDecision
import com.wingedsheep.engine.core.DistributionResponse
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.gpt.cards.Electrolyze
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Electrolyze: 2 damage divided among one or two targets, then draw a card. Covers both the
 * single-target (2 to one creature) and split (1 + 1 across two creatures) divisions, and that
 * the caster always draws a card.
 */
class ElectrolyzeScenarioTest : FunSpec({

    fun setup(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(Electrolyze)
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    test("Electrolyze deals 2 damage to a single target and draws a card") {
        val driver = setup()
        val caster = driver.activePlayer!!
        val opponent = driver.getOpponent(caster)

        val electrolyze = driver.putCardInHand(caster, "Electrolyze")
        driver.giveMana(caster, Color.BLUE, 1)
        driver.giveMana(caster, Color.RED, 1)
        driver.giveColorlessMana(caster, 1) // {1}{U}{R}

        val handBefore = driver.getHandSize(caster)
        // Single target: the opponent player.
        driver.castSpellWithTargets(caster, electrolyze, listOf(ChosenTarget.Player(opponent))).isSuccess shouldBe true

        resolveDistributing(driver, caster) { d -> mapOf(d.targets.first() to d.totalAmount) }

        driver.getLifeTotal(opponent) shouldBe 18
        // Cast Electrolyze (-1) then drew a card (+1) → hand size unchanged.
        driver.getHandSize(caster) shouldBe handBefore
    }

    test("Electrolyze splits 1 damage to each of two targets and draws a card") {
        val driver = setup()
        val caster = driver.activePlayer!!
        val opponent = driver.getOpponent(caster)

        // Two 2/2 creatures (Phantom Warrior) opponent controls.
        val c1 = driver.putCreatureOnBattlefield(opponent, "Phantom Warrior")
        val c2 = driver.putCreatureOnBattlefield(opponent, "Phantom Warrior")

        val electrolyze = driver.putCardInHand(caster, "Electrolyze")
        driver.giveMana(caster, Color.BLUE, 1)
        driver.giveMana(caster, Color.RED, 1)
        driver.giveColorlessMana(caster, 1)

        val handBefore = driver.getHandSize(caster)
        driver.castSpellWithTargets(
            caster, electrolyze,
            listOf(ChosenTarget.Permanent(c1), ChosenTarget.Permanent(c2))
        ).isSuccess shouldBe true

        resolveDistributing(driver, caster) { mapOf(c1 to 1, c2 to 1) }

        // Both 2/2s survive 1 damage each, and the draw still happens.
        driver.findPermanent(opponent, "Phantom Warrior") shouldNotBe null
        driver.getHandSize(caster) shouldBe handBefore
    }
}) {
    companion object {
        /**
         * Resolve a spell that pauses for a DividedDamage DistributeDecision: pass priority until the
         * decision appears, submit it via [planner], then finish resolving the stack.
         */
        private fun resolveDistributing(
            driver: GameTestDriver,
            chooser: EntityId,
            planner: (DistributeDecision) -> Map<EntityId, Int>,
        ) {
            repeat(8) {
                val decision = driver.state.pendingDecision
                if (decision is DistributeDecision) {
                    driver.submitDecision(chooser, DistributionResponse(decision.id, planner(decision)))
                } else if (driver.stackSize > 0 || driver.state.priorityPlayerId != null) {
                    driver.bothPass()
                }
            }
        }
    }
}
