package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.handlers.effects.ZoneTransitionService
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.ZoyowaLavaTongue
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Zoyowa Lava-Tongue (LCI #245): {B}{R} 2/2 Legendary Goblin Warlock, Deathtouch.
 *
 * "At the beginning of your end step, if you descended this turn, each opponent may discard a
 *  card or sacrifice a permanent of their choice. Zoyowa deals 3 damage to each
 *  opponent who didn't."
 *
 * The end-step trigger is gated on the descend intervening-if (Child of the Volcano template).
 * The per-opponent punisher is a ForEachPlayer(EachOpponent) → ChooseAction with three feasible
 * options when the opponent has both a card and a permanent:
 *   [0] Discard a card, [1] Sacrifice a permanent, [2] Take 3 damage from Zoyowa.
 *
 * Tests:
 * 1. No trigger (no decision, no damage) when the controller did not descend this turn.
 * 2. Opponent chooses to discard → loses a card, takes no damage.
 * 3. Opponent chooses to sacrifice a permanent → permanent hits the graveyard, no damage.
 * 4. Opponent declines (takes the 3 damage) → loses 3 life, keeps hand and permanents.
 */
class ZoyowaLavaTongueScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(ZoyowaLavaTongue))
        driver.initMirrorMatch(
            deck = Deck.of("Swamp" to 20, "Grizzly Bears" to 20),
            skipMulligans = true,
            startingLife = 20
        )
        return driver
    }

    /**
     * Move a permanent card to the graveyard via [ZoneTransitionService] to fire the zone-change
     * event and increment the descend counter (CR 700.11). Matches Broodrage Mycoid's test helper.
     */
    fun GameTestDriver.descend(entityId: EntityId) {
        val result = ZoneTransitionService.moveToZone(
            state = state,
            entityId = entityId,
            destinationZone = Zone.GRAVEYARD
        )
        replaceState(result.state)
    }

    /**
     * Advance to the controller's end step and pass priority until the opponent is prompted with
     * the discard-or-sacrifice-or-take-damage choice. Returns the pending [ChooseOptionDecision].
     */
    fun GameTestDriver.reachEndStepChoice(): ChooseOptionDecision {
        passPriorityUntil(Step.END)
        var guard = 0
        while (pendingDecision !is ChooseOptionDecision && guard < 50) {
            if (state.stack.isEmpty() && pendingDecision == null && guard > 0) break
            bothPass()
            guard++
        }
        return pendingDecision as? ChooseOptionDecision
            ?: error("Expected a ChooseOptionDecision, but pending decision is $pendingDecision")
    }

    test("no trigger when the controller did not descend this turn") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putCreatureOnBattlefield(me, "Zoyowa Lava-Tongue")

        val lifeBefore = driver.getLifeTotal(opponent)
        driver.passPriorityUntil(Step.END)
        driver.bothPass()

        driver.pendingDecision shouldBe null
        driver.getLifeTotal(opponent) shouldBe lifeBefore
    }

    test("opponent chooses to discard a card and takes no damage") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)

        // Opponent controls a permanent so all three options are feasible (stable indices).
        driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")
        // Give the opponent a known card to discard so the graveyard assertion is deterministic.
        val oppFodder = driver.putCardInHand(opponent, "Grizzly Bears")

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putCreatureOnBattlefield(me, "Zoyowa Lava-Tongue")
        val fodder = driver.putCardInHand(me, "Grizzly Bears")
        driver.descend(fodder)

        val handBefore = driver.getHandSize(opponent)
        val lifeBefore = driver.getLifeTotal(opponent)

        val decision = driver.reachEndStepChoice()
        decision.playerId shouldBe opponent
        // Option 0 = "Discard a card".
        driver.submitDecision(opponent, OptionChosenResponse(decision.id, 0))
        // The discard pauses to let the opponent choose which card; discard the known fodder.
        driver.submitCardSelection(opponent, listOf(oppFodder))

        driver.getHandSize(opponent) shouldBe handBefore - 1
        driver.getLifeTotal(opponent) shouldBe lifeBefore
        driver.getGraveyardCardNames(opponent).contains("Grizzly Bears") shouldBe true
    }

    test("opponent chooses to sacrifice a permanent and takes no damage") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)

        driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putCreatureOnBattlefield(me, "Zoyowa Lava-Tongue")
        val fodder = driver.putCardInHand(me, "Grizzly Bears")
        driver.descend(fodder)

        val lifeBefore = driver.getLifeTotal(opponent)

        val decision = driver.reachEndStepChoice()
        // Option 1 = "Sacrifice a permanent".
        driver.submitDecision(opponent, OptionChosenResponse(decision.id, 1))

        driver.getCreatures(opponent).none { driver.getCardName(it) == "Grizzly Bears" } shouldBe true
        driver.getGraveyardCardNames(opponent).contains("Grizzly Bears") shouldBe true
        driver.getLifeTotal(opponent) shouldBe lifeBefore
    }

    test("opponent who declines takes 3 damage and keeps hand and permanents") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)

        driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putCreatureOnBattlefield(me, "Zoyowa Lava-Tongue")
        val fodder = driver.putCardInHand(me, "Grizzly Bears")
        driver.descend(fodder)

        val handBefore = driver.getHandSize(opponent)
        val lifeBefore = driver.getLifeTotal(opponent)

        val decision = driver.reachEndStepChoice()
        // Option 2 = "Take 3 damage from Zoyowa Lava-Tongue".
        driver.submitDecision(opponent, OptionChosenResponse(decision.id, 2))

        driver.getLifeTotal(opponent) shouldBe lifeBefore - 3
        driver.getHandSize(opponent) shouldBe handBefore
        driver.getCreatures(opponent).any { driver.getCardName(it) == "Grizzly Bears" } shouldBe true
    }
})
