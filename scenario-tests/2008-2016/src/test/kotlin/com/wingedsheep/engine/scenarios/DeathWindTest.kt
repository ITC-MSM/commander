package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.avr.cards.DeathWind
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Death Wind.
 *
 * Death Wind: {X}{B}
 * Instant
 * Target creature gets -X/-X until end of turn.
 */
class DeathWindTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(DeathWind))
        return driver
    }

    test("X=3 kills a 3/3 creature (-3/-3)") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)

        // 3/3 Centaur Courser controlled by the opponent.
        val courser = driver.putCreatureOnBattlefield(opponent, "Centaur Courser")

        val deathWind = driver.putCardInHand(activePlayer, "Death Wind")
        driver.giveColorlessMana(activePlayer, 3) // for X=3
        driver.giveMana(activePlayer, com.wingedsheep.sdk.core.Color.BLACK, 1) // for {B}

        val result = driver.submit(
            CastSpell(
                playerId = activePlayer,
                cardId = deathWind,
                targets = listOf(ChosenTarget.Permanent(courser)),
                xValue = 3,
                paymentStrategy = PaymentStrategy.FromPool
            )
        )
        (result.error == null) shouldBe true

        driver.bothPass()

        // -3/-3 makes it 0/0 — it dies to a state-based action.
        driver.assertInGraveyard(opponent, "Centaur Courser")
    }

    test("X=1 leaves a 3/3 alive as a 2/2") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)

        val courser = driver.putCreatureOnBattlefield(opponent, "Centaur Courser")

        val deathWind = driver.putCardInHand(activePlayer, "Death Wind")
        driver.giveColorlessMana(activePlayer, 1) // for X=1
        driver.giveMana(activePlayer, com.wingedsheep.sdk.core.Color.BLACK, 1) // for {B}

        val result = driver.submit(
            CastSpell(
                playerId = activePlayer,
                cardId = deathWind,
                targets = listOf(ChosenTarget.Permanent(courser)),
                xValue = 1,
                paymentStrategy = PaymentStrategy.FromPool
            )
        )
        (result.error == null) shouldBe true

        driver.bothPass()

        // -1/-1 makes the 3/3 a 2/2 — still on the battlefield.
        val projected = projector.project(driver.state)
        projected.getPower(courser) shouldBe 2
        projected.getToughness(courser) shouldBe 2
    }
})
