package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.ltr.cards.FrodoDeterminedHero
import com.wingedsheep.mtg.sets.definitions.mrd.cards.Bonesplitter
import com.wingedsheep.mtg.sets.definitions.mrd.cards.LoxodonWarhammer
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Frodo, Determined Hero — attaches an MV-2-or-3 Equipment you control on enter/attack, and
 * prevents all damage to itself during your turn.
 */
class FrodoDeterminedHeroScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + FrodoDeterminedHero + Bonesplitter + LoxodonWarhammer)
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    test("attacking attaches a mana-value-2-or-3 Equipment you control to Frodo") {
        val driver = createDriver()
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)
        val frodo = driver.putCreatureOnBattlefield(you, "Frodo, Determined Hero")
        val hammer = driver.putPermanentOnBattlefield(you, "Loxodon Warhammer") // mana value 3
        driver.putPermanentOnBattlefield(you, "Bonesplitter") // mana value 1 — NOT a legal target
        driver.removeSummoningSickness(frodo)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(you, listOf(frodo), opponent)
        // Only the MV-3 Loxodon Warhammer is a legal target (Bonesplitter is MV 1).
        driver.submitTargetSelection(you, listOf(hammer))
        driver.bothPass()

        driver.state.getEntity(hammer)?.get<AttachedToComponent>()?.targetId shouldBe frodo
    }

    test("prevents all damage to Frodo during your turn (survives 3 damage to a 2/2)") {
        val driver = createDriver()
        val you = driver.activePlayer!!
        val frodo = driver.putCreatureOnBattlefield(you, "Frodo, Determined Hero") // 2/2

        // Lightning Bolt (3 damage) at Frodo on your turn — all damage prevented, so Frodo lives.
        val bolt = driver.putCardInHand(you, "Lightning Bolt")
        driver.giveMana(you, Color.RED, 1)
        driver.castSpell(you, bolt, targets = listOf(frodo)).isSuccess shouldBe true
        driver.bothPass()

        driver.state.getBattlefield(you).contains(frodo) shouldBe true
    }
})
