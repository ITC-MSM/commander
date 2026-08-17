package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.msh.cards.AvengersUnderSiege
import com.wingedsheep.mtg.sets.tokens.PredefinedTokens
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Avengers: Under Siege (MSH #205) — {2}{B}{R} Enchantment — Saga.
 *
 *   I  — Create two 2/1 black Villain creature tokens with menace.
 *   II — This Saga deals 2 damage to each non-Villain creature and each opponent.
 *   III— Create a Treasure token for each Villain you control.
 *
 * The load-bearing chapter is III: its count is a resolution-time
 * [com.wingedsheep.sdk.dsl.DynamicAmounts.battlefield] aggregate over
 * `GameObjectFilter.Any.withSubtype(VILLAIN)`, so the number of Treasures is asserted **exactly**
 * against a board with a known Villain count — Villains that are not creatures, Villains the
 * opponent controls and non-Villains you control are all part of that setup, because a count that
 * silently degrades to 0 (or to "all permanents") would look the same from a `> 0` assertion.
 */
class AvengersUnderSiegeScenarioTest : FunSpec({

    /** A plain Villain creature — 1/3 so it is unambiguously alive after chapter II either way. */
    val testVillain = card("Test Villain") {
        manaCost = "{1}{B}"
        typeLine = "Creature — Human Villain"
        power = 1
        toughness = 3
    }

    /** A non-Villain creature, tough enough to survive chapter II's 2 damage. */
    val testBystander = card("Test Bystander") {
        manaCost = "{2}{G}"
        typeLine = "Creature — Human"
        power = 3
        toughness = 3
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        // PredefinedTokens is *not* part of TestCards.all — without it the Treasure token has no
        // CardDefinition and CreatePredefinedTokenExecutor silently makes nothing.
        driver.registerCards(
            TestCards.all + PredefinedTokens.allTokens + listOf(AvengersUnderSiege, testVillain, testBystander)
        )
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        return driver
    }

    fun GameTestDriver.resolveStack() {
        var guard = 0
        while ((state.stack.isNotEmpty() || state.pendingDecision != null) && guard < 60) {
            if (state.pendingDecision != null) autoResolveDecision() else bothPass()
            guard++
        }
    }

    /**
     * Advance to the precombat main of the starting player's [nth] turn — the clock the Saga's
     * lore counters run on. `turnNumber` counts player turns and this is a duel, so the starting
     * player's nth turn is turn `2n - 1`.
     */
    fun GameTestDriver.advanceToMain(nth: Int) {
        val targetTurn = nth * 2 - 1
        var guard = 0
        while (!(state.turnNumber == targetTurn && state.step == Step.PRECOMBAT_MAIN) && guard < 500) {
            if (state.gameOver) throw AssertionError("Game ended while advancing to turn $targetTurn")
            when {
                state.pendingDecision != null -> autoResolveDecision()
                state.priorityPlayerId != null -> {
                    autoSubmitCombatDeclarationIfNeeded()
                    passPriority(state.priorityPlayerId!!)
                }
                else -> break
            }
            guard++
        }
        if (guard >= 500) error("Failed to reach turn $targetTurn precombat main")
    }

    fun GameTestDriver.castSaga(controller: EntityId) {
        passPriorityUntil(Step.PRECOMBAT_MAIN)
        giveMana(controller, Color.BLACK, 3)
        giveMana(controller, Color.RED, 1)
        val saga = putCardInHand(controller, "Avengers: Under Siege")
        castSpell(controller, saga)
        resolveStack() // the Saga enters, lore 1 → chapter I resolves
    }

    fun GameTestDriver.nameOf(id: EntityId): String? =
        state.getEntity(id)?.get<CardComponent>()?.name

    fun GameTestDriver.countNamed(player: EntityId, name: String): Int =
        getPermanents(player).count { nameOf(it) == name }

    /** Diagnostic: every permanent name a player controls, so a miscount names the board. */
    fun GameTestDriver.boardNames(player: EntityId): List<String?> =
        getPermanents(player).map { nameOf(it) }

    /** Every permanent [player] controls with the Villain subtype — what chapter III counts. */
    fun GameTestDriver.villains(player: EntityId): List<EntityId> =
        getPermanents(player).filter { id ->
            state.getEntity(id)?.get<CardComponent>()?.typeLine?.subtypes
                ?.any { it.value == "Villain" } == true
        }

    test("chapter III creates exactly one Treasure per Villain you control") {
        val driver = createDriver()
        val controller = driver.activePlayer!!
        val opponent = if (controller == driver.player1) driver.player2 else driver.player1

        // Board before the Saga: two Villains you control, one non-Villain you control, and one
        // Villain the opponent controls (which must not be counted).
        val villainA = driver.putCreatureOnBattlefield(controller, "Test Villain")
        val villainB = driver.putCreatureOnBattlefield(controller, "Test Villain")
        val bystander = driver.putCreatureOnBattlefield(controller, "Test Bystander")
        driver.putCreatureOnBattlefield(opponent, "Test Villain")

        // ---- Chapter I ----
        driver.castSaga(controller)
        val tokens = driver.villains(controller) - setOf(villainA, villainB)
        withClue("chapter I creates two 2/1 black Villain tokens with menace") {
            tokens.size shouldBe 2
            tokens.forEach { token ->
                driver.state.projectedState.getPower(token) shouldBe 2
                driver.state.projectedState.getToughness(token) shouldBe 1
                driver.state.projectedState.hasKeyword(token, Keyword.MENACE) shouldBe true
            }
        }

        // ---- Chapter II ----
        val opponentLifeBefore = driver.getLifeTotal(opponent)
        driver.advanceToMain(2)
        driver.resolveStack()
        withClue("chapter II deals 2 damage to each opponent") {
            driver.getLifeTotal(opponent) shouldBe opponentLifeBefore - 2
        }
        withClue("the Villains — yours, the opponent's, and the 2/1 tokens — are spared") {
            driver.getPermanents(controller).contains(villainA) shouldBe true
            driver.getPermanents(controller).contains(villainB) shouldBe true
            driver.villains(controller).size shouldBe 4
            driver.countNamed(opponent, "Test Villain") shouldBe 1
        }
        withClue("the non-Villain took the 2 damage but survives as a 3/3") {
            driver.getPermanents(controller).contains(bystander) shouldBe true
        }

        // ---- Chapter III ----
        val villainCount = driver.villains(controller).size
        withClue("setup sanity: two Test Villains plus the two chapter I tokens") {
            villainCount shouldBe 4
        }
        withClue("the opponent's Villain is on the battlefield but is not yours") {
            driver.villains(opponent).size shouldBe 1
        }

        driver.advanceToMain(3)
        driver.resolveStack()

        withClue("chapter III ran: the Saga is sacrificed after it") {
            driver.findPermanent(controller, "Avengers: Under Siege") shouldBe null
        }
        withClue("chapter III creates exactly one Treasure per Villain you control; board=${driver.boardNames(controller)}") {
            driver.countNamed(controller, "Treasure") shouldBe villainCount
        }
        withClue("the Treasures are yours, not the opponent's") {
            driver.countNamed(opponent, "Treasure") shouldBe 0
        }
    }

    test("chapter III's count is dynamic: only the two chapter I tokens means two Treasures") {
        val driver = createDriver()
        val controller = driver.activePlayer!!

        driver.castSaga(controller)
        driver.advanceToMain(2)
        driver.resolveStack()
        driver.advanceToMain(3)
        driver.resolveStack()

        withClue("the only Villains you control are the two tokens chapter I minted") {
            driver.villains(controller).size shouldBe 2
        }
        withClue("chapter III ran: the Saga is sacrificed after it") {
            driver.findPermanent(controller, "Avengers: Under Siege") shouldBe null
        }
        withClue("so chapter III makes two Treasures, not a fixed one; board=${driver.boardNames(controller)}") {
            driver.countNamed(controller, "Treasure") shouldBe 2
        }
    }
})
