package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fdn.cards.CryptFeaster
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Crypt Feaster {3}{B} — Creature — Zombie 3/4
 *   Menace
 *   Threshold — Whenever this creature attacks, if there are seven or more cards in your
 *   graveyard, this creature gets +2/+0 until end of turn.
 *
 * Proves the intervening-"if" attack trigger: pumps only when the controller's graveyard has
 * seven or more cards.
 */
class CryptFeasterScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(CryptFeaster))
        return driver
    }

    fun projectedPower(driver: GameTestDriver, id: EntityId): Int =
        StateProjector().getProjectedPower(driver.state, id)

    test("attacks with seven cards in graveyard → +2/+0") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 20, "Mountain" to 20), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)

        val feaster = driver.putCreatureOnBattlefield(you, "Crypt Feaster")
        driver.removeSummoningSickness(feaster)
        repeat(7) { driver.putCardInGraveyard(you, "Swamp") }

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(you, listOf(feaster), defendingPlayer = opponent).error shouldBe null

        // Resolve the attack trigger.
        var safety = 0
        while (driver.stackSize > 0 && safety < 20) { driver.bothPass(); safety++ }

        projectedPower(driver, feaster) shouldBe 5 // 3 base + 2
    }

    test("attacks with only six cards in graveyard → no pump") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 20, "Mountain" to 20), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)

        val feaster = driver.putCreatureOnBattlefield(you, "Crypt Feaster")
        driver.removeSummoningSickness(feaster)
        repeat(6) { driver.putCardInGraveyard(you, "Swamp") }

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(you, listOf(feaster), defendingPlayer = opponent).error shouldBe null

        var safety = 0
        while (driver.stackSize > 0 && safety < 20) { driver.bothPass(); safety++ }

        projectedPower(driver, feaster) shouldBe 3 // unchanged
    }
})
