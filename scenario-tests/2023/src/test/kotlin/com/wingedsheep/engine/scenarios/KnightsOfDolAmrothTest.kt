package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dom.cards.Divination
import com.wingedsheep.mtg.sets.definitions.ltr.cards.KnightsOfDolAmroth
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Knights of Dol Amroth (LTR #59)
 * {3}{U} Creature — Human Knight 3/3
 * Whenever you draw your second card each turn, put a +1/+1 counter on this creature.
 */
class KnightsOfDolAmrothTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(KnightsOfDolAmroth, Divination))
        return driver
    }

    test("gets a +1/+1 counter when controller draws their second card via Divination") {
        // Turn-1 active player skips the draw step, so Divination draws cards 1 and 2;
        // the second card crosses the threshold and the trigger fires once.
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 20, "Plains" to 10, "Divination" to 10))

        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val knights = driver.putCreatureOnBattlefield(p1, "Knights of Dol Amroth")

        val divination = driver.putCardInHand(p1, "Divination")
        driver.giveMana(p1, Color.BLUE, 1)
        driver.giveColorlessMana(p1, 2)
        driver.castSpell(p1, divination)
        driver.bothPass()
        driver.bothPass()

        val counters = driver.state.getEntity(knights)?.get<CountersComponent>()
        counters?.counters?.get(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
    }

    test("does not get a counter when only one card is drawn that turn") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 20, "Plains" to 10))

        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val knights = driver.putCreatureOnBattlefield(p1, "Knights of Dol Amroth")

        // No draw spells; the active player on turn 1 already skipped the draw step.
        driver.passPriorityUntil(Step.END)
        driver.bothPass()

        val counters = driver.state.getEntity(knights)?.get<CountersComponent>()
        counters?.counters?.get(CounterType.PLUS_ONE_PLUS_ONE) ?: 0 shouldBe 0
    }

    test("does not trigger from an opponent's draws") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 20, "Plains" to 10, "Divination" to 10))

        val p1 = driver.activePlayer!!
        val p2 = driver.getOpponent(p1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val knights = driver.putCreatureOnBattlefield(p1, "Knights of Dol Amroth")

        // Hand turn over to p2 and let them draw extra cards.
        driver.passPriorityUntil(Step.END)
        driver.bothPass()
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val divination = driver.putCardInHand(p2, "Divination")
        driver.giveMana(p2, Color.BLUE, 1)
        driver.giveColorlessMana(p2, 2)
        driver.castSpell(p2, divination)
        driver.bothPass()
        driver.bothPass()

        val counters = driver.state.getEntity(knights)?.get<CountersComponent>()
        counters?.counters?.get(CounterType.PLUS_ONE_PLUS_ONE) ?: 0 shouldBe 0
    }

    test("triggers again on the controller's next turn") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 30, "Plains" to 10, "Divination" to 10))

        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val knights = driver.putCreatureOnBattlefield(p1, "Knights of Dol Amroth")

        val div1 = driver.putCardInHand(p1, "Divination")
        driver.giveMana(p1, Color.BLUE, 1)
        driver.giveColorlessMana(p1, 2)
        driver.castSpell(p1, div1)
        driver.bothPass()
        driver.bothPass()

        driver.state.getEntity(knights)?.get<CountersComponent>()
            ?.counters?.get(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1

        // Pass to p2's turn, back to p1.
        driver.passPriorityUntil(Step.END)
        driver.bothPass()
        driver.passPriorityUntil(Step.END)
        driver.bothPass()
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // p1's draw step already drew card #1. Divination draws #2 and #3 → trigger fires once.
        val div2 = driver.putCardInHand(p1, "Divination")
        driver.giveMana(p1, Color.BLUE, 1)
        driver.giveColorlessMana(p1, 2)
        driver.castSpell(p1, div2)
        driver.bothPass()
        driver.bothPass()

        driver.state.getEntity(knights)?.get<CountersComponent>()
            ?.counters?.get(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 2
    }
})
