package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.DeclareBlockers
import com.wingedsheep.engine.handlers.effects.ZoneTransitionService
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.BroodrageMycoid
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Broodrage Mycoid (LCI #95): {3}{B} 4/3 Fungus
 *
 * "At the beginning of your end step, if you descended this turn, create a 1/1 black
 * Fungus creature token with 'This token can't block.'"
 *
 * Tests:
 * 1. No token is created at the end step when the controller has not descended.
 * 2. A 1/1 black Fungus token with "can't block" is created when descended this turn.
 * 3. The Fungus token cannot legally be declared as a blocker.
 */
class BroodrageMycoidScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(BroodrageMycoid))
        driver.initMirrorMatch(
            deck = Deck.of("Swamp" to 20, "Grizzly Bears" to 20),
            skipMulligans = true
        )
        return driver
    }

    /**
     * Move a card to the graveyard via [ZoneTransitionService] to fire the zone-change
     * event and increment the descend counter (CR 700.11). Matches the pattern in
     * [RuinLurkerBatScenarioTest].
     */
    fun GameTestDriver.descend(entityId: EntityId) {
        val result = ZoneTransitionService.moveToZone(
            state = state,
            entityId = entityId,
            destinationZone = Zone.GRAVEYARD
        )
        replaceState(result.state)
    }

    fun GameTestDriver.fungusTokens(playerId: EntityId): List<EntityId> =
        getCreatures(playerId).filter { getCardName(it) == "Fungus Token" }

    test("no Fungus token is created at end step when the controller has not descended") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putCreatureOnBattlefield(player, "Broodrage Mycoid")

        val tokensBefore = driver.fungusTokens(player).size
        driver.passPriorityUntil(Step.END)
        driver.bothPass()

        driver.fungusTokens(player).size shouldBe tokensBefore
    }

    test("a 1/1 black Fungus token with can't block is created when the controller descended this turn") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putCreatureOnBattlefield(player, "Broodrage Mycoid")

        // Discard a creature card to the graveyard — this is the canonical "descend"
        // event: a permanent (creature) card moved from any zone to the graveyard.
        val bears = driver.putCardInHand(player, "Grizzly Bears")
        driver.descend(bears)

        val tokensBefore = driver.fungusTokens(player).size
        driver.passPriorityUntil(Step.END)
        driver.bothPass() // EOT trigger resolves, token is created

        val tokensAfter = driver.fungusTokens(player)
        tokensAfter.size shouldBe tokensBefore + 1

        val token = tokensAfter.first()

        // Verify the token's power and toughness.
        driver.state.projectedState.getPower(token) shouldBe 1
        driver.state.projectedState.getToughness(token) shouldBe 1

        // Verify the "can't block" static ability is applied via the projected state.
        driver.state.projectedState.cantBlock(token) shouldBe true
    }

    test("the Fungus token cannot be declared as a blocker") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)

        // Put a vanilla attacker for the opponent on the battlefield (no summoning sickness).
        val oppAttacker = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")
        driver.removeSummoningSickness(oppAttacker)

        // On the active player's turn, descend then pass to end step to create the token.
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putCreatureOnBattlefield(player, "Broodrage Mycoid")
        val bears = driver.putCardInHand(player, "Grizzly Bears")
        driver.descend(bears)

        driver.passPriorityUntil(Step.END)
        driver.bothPass() // creates the Fungus token for player

        val token = driver.fungusTokens(player).single()

        // Advance to the opponent's declare-attackers step; opponent attacks with Bears.
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(opponent, listOf(oppAttacker), player)
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)

        // Attempting to block with the Fungus token is illegal.
        driver.submitExpectFailure(
            DeclareBlockers(player, mapOf(token to listOf(oppAttacker)))
        )
    }
})
