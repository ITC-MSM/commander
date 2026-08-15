package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.otj.cards.ConduitPylons
import com.wingedsheep.mtg.sets.definitions.otj.cards.DesertsDue
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Desert's Due: {1}{B} Instant
 * Target creature gets -2/-2 until end of turn. It gets an additional -1/-1 until end of turn
 * for each Desert you control.
 */
class DesertsDueTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(DesertsDue, ConduitPylons))
        return driver
    }

    test("with no Deserts, gives -2/-2") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)

        // Centaur Courser is a 3/3.
        val courser = driver.putCreatureOnBattlefield(opp, "Centaur Courser")
        val due = driver.putCardInHand(me, "Desert's Due")
        driver.giveMana(me, Color.BLACK, 1)
        driver.giveColorlessMana(me, 1)

        val result = driver.submit(
            CastSpell(
                playerId = me,
                cardId = due,
                targets = listOf(ChosenTarget.Permanent(courser)),
                paymentStrategy = PaymentStrategy.FromPool
            )
        )
        (result.error == null) shouldBe true
        driver.bothPass()

        // 3/3 - 2/2 = 1/1.
        val projected = projector.project(driver.state)
        projected.getPower(courser) shouldBe 1
        projected.getToughness(courser) shouldBe 1
    }

    test("with two Deserts, gives -4/-4 and can kill a 3/3") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)

        val courser = driver.putCreatureOnBattlefield(opp, "Centaur Courser")
        // Two Deserts I control: -2/-2 base plus -1/-1 each = -4/-4.
        driver.putLandOnBattlefield(me, "Conduit Pylons")
        driver.putLandOnBattlefield(me, "Conduit Pylons")
        // A Desert the opponent controls must NOT count.
        driver.putLandOnBattlefield(opp, "Conduit Pylons")

        val due = driver.putCardInHand(me, "Desert's Due")
        driver.giveMana(me, Color.BLACK, 1)
        driver.giveColorlessMana(me, 1)

        val result = driver.submit(
            CastSpell(
                playerId = me,
                cardId = due,
                targets = listOf(ChosenTarget.Permanent(courser)),
                paymentStrategy = PaymentStrategy.FromPool
            )
        )
        (result.error == null) shouldBe true
        driver.bothPass()

        // 3/3 with -4/-4 dies as a state-based action.
        driver.assertInGraveyard(opp, "Centaur Courser")
    }
})
