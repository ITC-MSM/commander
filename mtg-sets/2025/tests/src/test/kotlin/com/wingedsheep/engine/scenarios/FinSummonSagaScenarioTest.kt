package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.SummonAnima
import com.wingedsheep.mtg.sets.definitions.fin.cards.SummonKnightsOfRound
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * End-to-end behaviour for two real FIN Summon Saga cards, confirming their chapter abilities fire
 * on entry while the permanent is a live creature. The generic saga-creature machinery is proven by
 * [CreatureSagaTest]; this pins two shipped cards.
 */
class FinSummonSagaScenarioTest : FunSpec({

    val projector = StateProjector()

    fun resolveStack(driver: GameTestDriver) {
        var guard = 0
        while (guard++ < 30 && driver.state.stack.isNotEmpty()) driver.bothPass()
    }

    fun castFromHand(driver: GameTestDriver, controller: EntityId, name: String) {
        val spell = driver.putCardInHand(controller, name)
        driver.giveMana(controller, Color.WHITE, 2)
        driver.giveMana(controller, Color.BLACK, 2)
        driver.giveColorlessMana(controller, 8)
        driver.castSpell(controller, spell)
        driver.bothPass()
        resolveStack(driver)
    }

    test("Summon: Anima enters as a 4/4 Menace creature-saga and chapter I draws + loses 1 life") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(SummonAnima))
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val active = driver.activePlayer!!
        val startLife = driver.getLifeTotal(active)
        val startHand = driver.getHandSize(active)

        castFromHand(driver, active, "Summon: Anima")

        val anima = driver.findPermanent(active, "Summon: Anima")!!
        val projected = projector.project(driver.state)
        projected.isCreature(anima) shouldBe true
        projected.hasType(anima, "Saga") shouldBe true
        projected.getPower(anima) shouldBe 4
        projected.getToughness(anima) shouldBe 4
        projected.hasKeyword(anima, Keyword.MENACE) shouldBe true

        // Chapter I "Pain": draw a card and lose 1 life. Anima was added to hand then cast (net 0
        // vs the captured baseline), so the chapter's draw leaves hand at startHand + 1.
        driver.getLifeTotal(active) shouldBe startLife - 1
        driver.getHandSize(active) shouldBe startHand + 1
    }

    test("Summon: Knights of Round enters Indestructible and chapter I makes three 2/2 Knights") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(SummonKnightsOfRound))
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40))
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val active = driver.activePlayer!!

        castFromHand(driver, active, "Summon: Knights of Round")

        val saga = driver.findPermanent(active, "Summon: Knights of Round")!!
        projector.project(driver.state).hasKeyword(saga, Keyword.INDESTRUCTIBLE) shouldBe true

        // Chapter I created three 2/2 Knight tokens; with the saga itself that's 4 creatures.
        driver.getCreatures(active).size shouldBe 4
    }
})
