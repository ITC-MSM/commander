package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.DeclareBlockers
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dom.cards.TetsukoUmezawaFugitive
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContainIgnoringCase

/**
 * Tests for Tetsuko Umezawa, Fugitive.
 *
 * Tetsuko Umezawa, Fugitive: {1}{U}
 * Legendary Creature — Human Rogue
 * 1/3
 * Creatures you control with power or toughness 1 or less can't be blocked.
 */
class TetsukoUmezawaFugitiveTest : FunSpec({

    // 0/4 test creature — has power 0 (<=1) so it qualifies for Tetsuko's ability
    val WallOfAir = CardDefinition.creature(
        name = "Wall of Air",
        manaCost = ManaCost.parse("{1}{U}{U}"),
        subtypes = setOf(Subtype("Wall")),
        power = 0,
        toughness = 4,
        oracleText = ""
    )

    // 3/1 test creature — has toughness 1 (<=1) so it qualifies for Tetsuko's ability
    val GlassAspid = CardDefinition.creature(
        name = "Glass Aspid",
        manaCost = ManaCost.parse("{2}{G}"),
        subtypes = setOf(Subtype("Snake")),
        power = 3,
        toughness = 1,
        oracleText = ""
    )

    val allCards = TestCards.all + listOf(TetsukoUmezawaFugitive, WallOfAir, GlassAspid)

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(allCards)
        driver.initMirrorMatch(
            deck = Deck.of(
                "Island" to 20,
                "Forest" to 20
            ),
            skipMulligans = true
        )
        return driver
    }

    fun GameTestDriver.advanceToPlayer1DeclareAttackers() {
        passPriorityUntil(Step.DECLARE_ATTACKERS)
        var safety = 0
        while (activePlayer != player1 && safety < 50) {
            bothPass()
            passPriorityUntil(Step.DECLARE_ATTACKERS)
            safety++
        }
    }

    test("1/1 creature can't be blocked when Tetsuko is on battlefield") {
        val driver = createDriver()

        // Put Tetsuko on player 1's battlefield
        val tetsuko = driver.putCreatureOnBattlefield(driver.player1, "Tetsuko Umezawa, Fugitive")
        driver.removeSummoningSickness(tetsuko)

        // Put a 1/1 attacker on player 1's battlefield
        val attacker = driver.putCreatureOnBattlefield(driver.player1, "Savannah Lions")
        driver.removeSummoningSickness(attacker)

        // Put a blocker on player 2's battlefield
        val blocker = driver.putCreatureOnBattlefield(driver.player2, "Centaur Courser")
        driver.removeSummoningSickness(blocker)

        driver.advanceToPlayer1DeclareAttackers()
        driver.declareAttackers(driver.player1, listOf(attacker), driver.player2)
        driver.bothPass()
        driver.currentStep shouldBe Step.DECLARE_BLOCKERS

        // Try to block — should fail because Savannah Lions has power 1
        val result = driver.submitExpectFailure(
            DeclareBlockers(driver.player2, mapOf(blocker to listOf(attacker)))
        )
        result.isSuccess shouldBe false
        result.error shouldContainIgnoringCase "can't be blocked"
    }

    test("creature with power 0 can't be blocked (power <= 1)") {
        val driver = createDriver()

        val tetsuko = driver.putCreatureOnBattlefield(driver.player1, "Tetsuko Umezawa, Fugitive")
        driver.removeSummoningSickness(tetsuko)

        val attacker = driver.putCreatureOnBattlefield(driver.player1, "Wall of Air")
        driver.removeSummoningSickness(attacker)

        val blocker = driver.putCreatureOnBattlefield(driver.player2, "Centaur Courser")
        driver.removeSummoningSickness(blocker)

        driver.advanceToPlayer1DeclareAttackers()
        driver.declareAttackers(driver.player1, listOf(attacker), driver.player2)
        driver.bothPass()
        driver.currentStep shouldBe Step.DECLARE_BLOCKERS

        val result = driver.submitExpectFailure(
            DeclareBlockers(driver.player2, mapOf(blocker to listOf(attacker)))
        )
        result.isSuccess shouldBe false
        result.error shouldContainIgnoringCase "can't be blocked"
    }

    test("creature with toughness 1 can't be blocked (toughness <= 1)") {
        val driver = createDriver()

        val tetsuko = driver.putCreatureOnBattlefield(driver.player1, "Tetsuko Umezawa, Fugitive")
        driver.removeSummoningSickness(tetsuko)

        // Glass Aspid is 3/1 — toughness 1 qualifies
        val attacker = driver.putCreatureOnBattlefield(driver.player1, "Glass Aspid")
        driver.removeSummoningSickness(attacker)

        val blocker = driver.putCreatureOnBattlefield(driver.player2, "Centaur Courser")
        driver.removeSummoningSickness(blocker)

        driver.advanceToPlayer1DeclareAttackers()
        driver.declareAttackers(driver.player1, listOf(attacker), driver.player2)
        driver.bothPass()
        driver.currentStep shouldBe Step.DECLARE_BLOCKERS

        val result = driver.submitExpectFailure(
            DeclareBlockers(driver.player2, mapOf(blocker to listOf(attacker)))
        )
        result.isSuccess shouldBe false
        result.error shouldContainIgnoringCase "can't be blocked"
    }

    test("creature with power and toughness both > 1 can still be blocked") {
        val driver = createDriver()

        val tetsuko = driver.putCreatureOnBattlefield(driver.player1, "Tetsuko Umezawa, Fugitive")
        driver.removeSummoningSickness(tetsuko)

        // Centaur Courser is 3/3 — neither power nor toughness is <= 1
        val attacker = driver.putCreatureOnBattlefield(driver.player1, "Centaur Courser")
        driver.removeSummoningSickness(attacker)

        val blocker = driver.putCreatureOnBattlefield(driver.player2, "Centaur Courser")
        driver.removeSummoningSickness(blocker)

        driver.advanceToPlayer1DeclareAttackers()
        driver.declareAttackers(driver.player1, listOf(attacker), driver.player2)
        driver.bothPass()
        driver.currentStep shouldBe Step.DECLARE_BLOCKERS

        // Block should succeed — 3/3 doesn't qualify for Tetsuko's ability
        val result = driver.declareBlockers(
            driver.player2,
            mapOf(blocker to listOf(attacker))
        )
        result.isSuccess shouldBe true
    }

    test("without Tetsuko, 1/1 creature can be blocked normally") {
        val driver = createDriver()

        // No Tetsuko on battlefield
        val attacker = driver.putCreatureOnBattlefield(driver.player1, "Savannah Lions")
        driver.removeSummoningSickness(attacker)

        val blocker = driver.putCreatureOnBattlefield(driver.player2, "Centaur Courser")
        driver.removeSummoningSickness(blocker)

        driver.advanceToPlayer1DeclareAttackers()
        driver.declareAttackers(driver.player1, listOf(attacker), driver.player2)
        driver.bothPass()
        driver.currentStep shouldBe Step.DECLARE_BLOCKERS

        // Block should succeed — no Tetsuko effect
        val result = driver.declareBlockers(
            driver.player2,
            mapOf(blocker to listOf(attacker))
        )
        result.isSuccess shouldBe true
    }

    test("Tetsuko does not affect opponent's creatures") {
        val driver = createDriver()

        // Tetsuko on player 1's battlefield
        val tetsuko = driver.putCreatureOnBattlefield(driver.player1, "Tetsuko Umezawa, Fugitive")
        driver.removeSummoningSickness(tetsuko)

        // Opponent's 1/1 creature should still be blockable
        val opponentAttacker = driver.putCreatureOnBattlefield(driver.player2, "Savannah Lions")
        driver.removeSummoningSickness(opponentAttacker)

        val blocker = driver.putCreatureOnBattlefield(driver.player1, "Centaur Courser")
        driver.removeSummoningSickness(blocker)

        // Advance to player 2's declare attackers step
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        var safety = 0
        while (driver.activePlayer != driver.player2 && safety < 50) {
            driver.bothPass()
            driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
            safety++
        }
        driver.activePlayer shouldBe driver.player2

        driver.declareAttackers(driver.player2, listOf(opponentAttacker), driver.player1)
        driver.bothPass()
        driver.currentStep shouldBe Step.DECLARE_BLOCKERS

        // Block should succeed — Tetsuko only protects controller's creatures
        val result = driver.declareBlockers(
            driver.player1,
            mapOf(blocker to listOf(opponentAttacker))
        )
        result.isSuccess shouldBe true
    }

    test("Tetsuko itself can't be blocked (1/3, power 1)") {
        val driver = createDriver()

        val tetsuko = driver.putCreatureOnBattlefield(driver.player1, "Tetsuko Umezawa, Fugitive")
        driver.removeSummoningSickness(tetsuko)

        val blocker = driver.putCreatureOnBattlefield(driver.player2, "Centaur Courser")
        driver.removeSummoningSickness(blocker)

        driver.advanceToPlayer1DeclareAttackers()
        driver.declareAttackers(driver.player1, listOf(tetsuko), driver.player2)
        driver.bothPass()
        driver.currentStep shouldBe Step.DECLARE_BLOCKERS

        // Tetsuko is 1/3 — power 1 qualifies for its own ability
        val result = driver.submitExpectFailure(
            DeclareBlockers(driver.player2, mapOf(blocker to listOf(tetsuko)))
        )
        result.isSuccess shouldBe false
        result.error shouldContainIgnoringCase "can't be blocked"
    }
})
