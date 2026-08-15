package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.otj.cards.GiantBeaver
import com.wingedsheep.mtg.sets.definitions.otj.cards.ThrowFromTheSaddle
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Throw from the Saddle: {1}{G} Sorcery
 * Target creature you control gets +1/+1 until end of turn. Put a +1/+1 counter on it instead
 * if it's a Mount. Then it deals damage equal to its power to target creature you don't control.
 */
class ThrowFromTheSaddleTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(ThrowFromTheSaddle, GiantBeaver))
        return driver
    }

    test("non-Mount gets a temporary +1/+1 and deals its boosted power as damage") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)

        // My non-Mount 3/3.
        val mine = driver.putCreatureOnBattlefield(me, "Centaur Courser")
        // Their 3/3 — 4 damage kills it.
        val theirs = driver.putCreatureOnBattlefield(opp, "Centaur Courser")

        val throwCard = driver.putCardInHand(me, "Throw from the Saddle")
        driver.giveMana(me, Color.GREEN, 1)
        driver.giveColorlessMana(me, 1)

        val result = driver.submit(
            CastSpell(
                playerId = me,
                cardId = throwCard,
                targets = listOf(ChosenTarget.Permanent(mine), ChosenTarget.Permanent(theirs)),
                paymentStrategy = PaymentStrategy.FromPool
            )
        )
        (result.error == null) shouldBe true
        driver.bothPass()

        // Non-Mount got no +1/+1 counter (the boost was the temporary modifier).
        val counters = driver.state.getEntity(mine)?.get<CountersComponent>()
            ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
        counters shouldBe 0

        // 3/3 + 1/1 = 4 power; 4 damage kills the opposing 3/3.
        driver.assertInGraveyard(opp, "Centaur Courser")
    }

    test("Mount gets a +1/+1 counter and deals its boosted power as damage") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)

        // My Mount: Giant Beaver, a 4/4 Beaver Mount.
        val mine = driver.putCreatureOnBattlefield(me, "Giant Beaver")
        // Their 4/4 Mount survives 4 but not 5 damage.
        val theirs = driver.putCreatureOnBattlefield(opp, "Giant Beaver")

        val throwCard = driver.putCardInHand(me, "Throw from the Saddle")
        driver.giveMana(me, Color.GREEN, 1)
        driver.giveColorlessMana(me, 1)

        val result = driver.submit(
            CastSpell(
                playerId = me,
                cardId = throwCard,
                targets = listOf(ChosenTarget.Permanent(mine), ChosenTarget.Permanent(theirs)),
                paymentStrategy = PaymentStrategy.FromPool
            )
        )
        (result.error == null) shouldBe true
        driver.bothPass()

        // Mount got a real +1/+1 counter instead of a temporary boost.
        val counters = driver.state.getEntity(mine)?.get<CountersComponent>()
            ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
        counters shouldBe 1

        // It is now 5/5.
        val projected = projector.project(driver.state)
        projected.getPower(mine) shouldBe 5

        // 5 damage kills the opposing 4/4 Mount.
        driver.assertInGraveyard(opp, "Giant Beaver")
    }
})
