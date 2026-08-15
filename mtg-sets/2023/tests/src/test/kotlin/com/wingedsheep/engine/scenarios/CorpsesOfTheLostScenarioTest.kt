package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.handlers.effects.ZoneTransitionService
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.CorpsesOfTheLost
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Corpses of the Lost (LCI #98): {2}{B} Enchantment
 *
 * 1. Skeletons you control get +1/+0 and have haste (static anthem).
 * 2. When this enchantment enters, create a 2/2 black Skeleton Pirate creature token (ETB).
 * 3. At the beginning of your end step, if you descended this turn, you may pay 1 life.
 *    If you do, return this enchantment to its owner's hand.
 * 4. The end-step trigger does not fire if the controller has not descended.
 */
class CorpsesOfTheLostScenarioTest : FunSpec({

    // A minimal 1/1 Skeleton for testing the static anthem without any extra abilities.
    val testSkeleton = card("Test Skeleton") {
        manaCost = "{B}"
        typeLine = "Creature — Skeleton"
        power = 1
        toughness = 1
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(CorpsesOfTheLost, testSkeleton))
        driver.initMirrorMatch(
            deck = Deck.of("Swamp" to 20, "Grizzly Bears" to 20),
            skipMulligans = true
        )
        return driver
    }

    /**
     * Move a permanent card to the graveyard to fire the zone-change event and
     * increment the descend counter (CR 700.11).
     */
    fun GameTestDriver.descend(entityId: EntityId) {
        val result = ZoneTransitionService.moveToZone(
            state = state,
            entityId = entityId,
            destinationZone = Zone.GRAVEYARD
        )
        replaceState(result.state)
    }

    fun GameTestDriver.skeletonPirateTokens(playerId: EntityId): List<EntityId> =
        getCreatures(playerId).filter { getCardName(it) == "Skeleton Pirate Token" }

    // -------------------------------------------------------------------------
    // Test 1: ETB creates a 2/2 black Skeleton Pirate token
    // -------------------------------------------------------------------------
    test("enters: creates a 2/2 black Skeleton Pirate creature token") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val tokensBefore = driver.skeletonPirateTokens(player).size

        // Cast Corpses of the Lost — {2}{B}.
        val enchantment = driver.putCardInHand(player, "Corpses of the Lost")
        driver.giveMana(player, Color.BLACK, 3)
        driver.castSpell(player, enchantment).isSuccess shouldBe true

        // Resolve the enchantment spell and its ETB token-creation trigger.
        var guard = 0
        while (driver.state.stack.isNotEmpty() && !driver.isPaused && guard++ < 20) {
            driver.bothPass()
        }

        val tokensAfter = driver.skeletonPirateTokens(player)
        tokensAfter.size shouldBe tokensBefore + 1

        val token = tokensAfter.first()
        // Base stats are 2/2. The enchantment's own anthem (+1/+0 to Skeletons you control)
        // is already active when the token enters, so projected power is 2+1 = 3.
        driver.state.projectedState.getPower(token) shouldBe 3
        driver.state.projectedState.getToughness(token) shouldBe 2
    }

    // -------------------------------------------------------------------------
    // Test 2: Skeletons you control get +1/+0 and have haste
    // -------------------------------------------------------------------------
    test("static: Skeletons you control get +1/+0 and have haste") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Put the enchantment on the battlefield directly (no ETB fires).
        driver.putPermanentOnBattlefield(player, "Corpses of the Lost")

        // Put a 1/1 Skeleton onto the battlefield.
        val skeleton = driver.putCreatureOnBattlefield(player, "Test Skeleton")

        // The static anthem should give it +1/+0, making it a 2/1, and grant haste.
        driver.state.projectedState.getPower(skeleton) shouldBe 2
        driver.state.projectedState.getToughness(skeleton) shouldBe 1
        driver.state.projectedState.hasKeyword(skeleton, Keyword.HASTE) shouldBe true
    }

    // -------------------------------------------------------------------------
    // Test 3: End-step trigger fires when descended; paying 1 life bounces to hand
    // -------------------------------------------------------------------------
    test("descended: end-step trigger fires and paying 1 life returns enchantment to hand") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val enchantment = driver.putPermanentOnBattlefield(player, "Corpses of the Lost")
        val lifeBefore = driver.getLifeTotal(player)
        val handSizeBefore = driver.getHandSize(player)

        // Descend: put a permanent card into the graveyard.
        val bearCard = driver.putCardInHand(player, "Grizzly Bears")
        driver.descend(bearCard)

        // Advance to end step and resolve the descended trigger.
        driver.passPriorityUntil(Step.END)
        var safety = 0
        while (safety++ < 20) {
            when {
                driver.pendingDecision is YesNoDecision ->
                    driver.submitYesNo(player, true) // "Yes, pay 1 life"
                driver.stackSize > 0 -> driver.bothPass()
                else -> break
            }
        }

        // Life decreased by 1 (the payment).
        driver.getLifeTotal(player) shouldBe lifeBefore - 1

        // The enchantment left the battlefield and is now in hand.
        driver.findPermanent(player, "Corpses of the Lost") shouldBe null
        driver.getHandSize(player) shouldBe handSizeBefore + 1
    }

    // -------------------------------------------------------------------------
    // Test 4: End-step trigger does NOT fire if the controller has not descended
    // -------------------------------------------------------------------------
    test("not descended: end-step trigger does not fire") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val enchantment = driver.putPermanentOnBattlefield(player, "Corpses of the Lost")

        // Do NOT descend — just advance to the end step.
        driver.passPriorityUntil(Step.END)
        driver.bothPass()

        // The trigger should not have fired — no YesNoDecision pending.
        (driver.pendingDecision is YesNoDecision) shouldBe false

        // The enchantment remains on the battlefield.
        driver.findPermanent(player, "Corpses of the Lost") shouldNotBe null
        driver.findPermanent(player, "Corpses of the Lost") shouldBe enchantment
    }
})
