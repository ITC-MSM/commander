package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Aunt May (SPM) — {W} Legendary Creature — Human Citizen 0/2.
 *
 *  "Whenever another creature you control enters, you gain 1 life. If it's a Spider, put a
 *   +1/+1 counter on it."
 *
 * Verifies a non-Spider entering gains 1 life only; a Spider entering gains 1 life AND gets a
 * +1/+1 counter (the conditional branch).
 */
class AuntMayScenarioTest : FunSpec({

    fun plusOneCounters(driver: GameTestDriver, id: com.wingedsheep.sdk.model.EntityId): Int =
        driver.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    test("a non-Spider entering gains 1 life and gets no counter") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val me = driver.activePlayer!!
        driver.putCreatureOnBattlefield(me, "Aunt May")

        val lifeBefore = driver.getLifeTotal(me)
        val bears = driver.putCardInHand(me, "Grizzly Bears")
        driver.giveMana(me, Color.GREEN, 2)
        driver.castSpell(me, bears)
        driver.bothPass() // resolve Grizzly Bears -> Aunt May trigger on stack
        driver.bothPass() // resolve Aunt May trigger

        driver.getLifeTotal(me) shouldBe lifeBefore + 1
        val bearsId = driver.getCreatures(me).first { driver.getCardName(it) == "Grizzly Bears" }
        plusOneCounters(driver, bearsId) shouldBe 0
    }

    test("a Spider entering gains 1 life and gets a +1/+1 counter") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val me = driver.activePlayer!!
        driver.putCreatureOnBattlefield(me, "Aunt May")

        val lifeBefore = driver.getLifeTotal(me)
        val spider = driver.putCardInHand(me, "Pincer Spider")
        driver.giveColorlessMana(me, 2)
        driver.giveMana(me, Color.GREEN, 1)
        driver.castSpell(me, spider)
        driver.bothPass() // resolve Pincer Spider -> Aunt May trigger
        driver.bothPass() // resolve Aunt May trigger

        driver.getLifeTotal(me) shouldBe lifeBefore + 1
        val spiderId = driver.getCreatures(me).first { driver.getCardName(it) == "Pincer Spider" }
        plusOneCounters(driver, spiderId) shouldBe 1
    }
})
