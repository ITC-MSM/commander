package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mrd.cards.NecrogenMists
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Necrogen Mists (MRD) — {2}{B} Enchantment.
 *
 * "At the beginning of each player's upkeep, that player discards a card."
 *
 * Pins the each-player upkeep scope (`Triggers.EachUpkeep`) drafted by mtgish-tooling. Unlike
 * Bottomless Pit's random discard, this is a CHOSEN discard, so the discarding player (the one
 * whose upkeep it is) gets to pick — verified by the SelectCardsDecision routed to that player.
 */
class NecrogenMistsScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(NecrogenMists))
        return driver
    }

    fun advanceToUpkeepOf(driver: GameTestDriver, player: EntityId) {
        driver.passPriorityUntil(Step.UPKEEP, maxPasses = 200)
        if (driver.activePlayer != player) {
            driver.passPriorityUntil(Step.DRAW, maxPasses = 200)
            driver.passPriorityUntil(Step.UPKEEP, maxPasses = 200)
        }
        driver.currentStep shouldBe Step.UPKEEP
        driver.activePlayer shouldBe player
    }

    // Resolve the upkeep trigger and let [discarder] choose one card to discard. Returns the
    // entity that was discarded.
    fun resolveDiscardChoice(driver: GameTestDriver, discarder: EntityId): EntityId {
        driver.bothPass() // resolve the trigger; the chosen-discard effect pauses for a selection
        val decision = driver.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        decision.playerId shouldBe discarder
        val toDiscard = driver.getHand(discarder).first()
        driver.submitCardSelection(discarder, listOf(toDiscard))
        return toDiscard
    }

    test("its controller chooses a card to discard on their own upkeep") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40))

        val controller = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putPermanentOnBattlefield(controller, "Necrogen Mists")

        advanceToUpkeepOf(driver, controller)
        driver.stackSize shouldBe 1

        val handBefore = driver.getHandSize(controller)
        val discarded = resolveDiscardChoice(driver, controller)

        driver.getHandSize(controller) shouldBe handBefore - 1
        driver.getGraveyard(controller) shouldBe listOf(discarded)
    }

    test("each opponent chooses a card to discard on their upkeep") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40))

        val controller = driver.activePlayer!!
        val opponent = driver.getOpponent(controller)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putPermanentOnBattlefield(controller, "Necrogen Mists")

        advanceToUpkeepOf(driver, opponent)
        driver.stackSize shouldBe 1

        val handBefore = driver.getHandSize(opponent)
        val discarded = resolveDiscardChoice(driver, opponent)

        driver.getHandSize(opponent) shouldBe handBefore - 1
        driver.getGraveyard(opponent) shouldBe listOf(discarded)
    }
})
