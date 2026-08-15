package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Winter, Misanthropic Guide (DSK #240) — proves the SetMaximumHandSize static ability.
 *
 * "Delirium — As long as there are four or more card types among cards in your graveyard, each
 * opponent's maximum hand size is equal to seven minus the number of those card types."
 *
 * The cleanup step reads the (Delirium-gated) SetMaximumHandSize(EachOpponent, 7 - distinctTypes)
 * static off Winter and discards the opponent down to that value; below delirium the gate is off and
 * the default maximum of seven applies.
 */
class WinterMisanthropicGuideTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true, startingPlayer = 0)
        return driver
    }

    /** Active player (whose cleanup runs) is the opponent of Winter's controller. */
    fun setup(driver: GameTestDriver, distinctTypesInGraveyard: Int): Pair<EntityId, EntityId> {
        val active = driver.activePlayer!!
        val winterController = driver.getOpponent(active)
        driver.putPermanentOnBattlefield(winterController, "Winter, Misanthropic Guide")

        // Fill Winter's controller's graveyard with the requested number of distinct card types.
        val fodder = listOf(
            "Grizzly Bears", // Creature
            "Lightning Bolt", // Instant
            "Doom Blade",     // Sorcery
            "Swamp",          // Land
        )
        repeat(distinctTypesInGraveyard) { driver.putCardInGraveyard(winterController, fodder[it]) }
        return active to winterController
    }

    test("with delirium (4 types) each opponent's maximum hand size is 7 minus the type count") {
        val driver = newDriver()
        val (active, _) = setup(driver, distinctTypesInGraveyard = 4)

        // Reach the active player's end step, then pad their hand well above any cap.
        driver.passPriorityUntil(Step.END)
        repeat(6) { driver.putCardInHand(active, "Swamp") }

        // Enter cleanup and resolve the discard-to-hand-size decision.
        driver.passPriorityUntil(Step.CLEANUP)
        if (driver.state.pendingDecision != null) driver.autoResolveDecision()

        // 7 - 4 distinct card types = maximum hand size of 3.
        driver.getHandSize(active) shouldBe 3
    }

    test("below delirium (3 types) the opponent keeps the default maximum hand size of 7") {
        val driver = newDriver()
        val (active, _) = setup(driver, distinctTypesInGraveyard = 3)

        driver.passPriorityUntil(Step.END)
        repeat(6) { driver.putCardInHand(active, "Swamp") }

        driver.passPriorityUntil(Step.CLEANUP)
        if (driver.state.pendingDecision != null) driver.autoResolveDecision()

        // Delirium is not met, so the SetMaximumHandSize static doesn't apply: default 7.
        driver.getHandSize(active) shouldBe 7
    }

    test("Winter's controller is unaffected — their own maximum hand size stays 7") {
        val driver = newDriver()
        // Make Winter's controller the active (discarding) player this time.
        val discarder = driver.activePlayer!!
        driver.putPermanentOnBattlefield(discarder, "Winter, Misanthropic Guide")
        repeat(4) { i ->
            driver.putCardInGraveyard(discarder, listOf("Grizzly Bears", "Lightning Bolt", "Doom Blade", "Swamp")[i])
        }

        driver.passPriorityUntil(Step.END)
        repeat(6) { driver.putCardInHand(discarder, "Swamp") }

        driver.passPriorityUntil(Step.CLEANUP)
        if (driver.state.pendingDecision != null) driver.autoResolveDecision()

        // "each opponent's" excludes Winter's own controller.
        driver.getHandSize(discarder) shouldBe 7
    }
})
