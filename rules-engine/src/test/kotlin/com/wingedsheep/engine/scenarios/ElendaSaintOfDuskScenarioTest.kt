package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fdn.cards.ElendaSaintOfDusk
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Elenda, Saint of Dusk (FDN #119) — {2}{W}{B} 4/4 Legendary Creature — Vampire Knight.
 *
 * "Lifelink, hexproof from instants
 *  As long as your life total is greater than your starting life total, Elenda gets +1/+1 and has
 *  menace. Elenda gets an additional +5/+5 as long as your life total is at least 10 greater than
 *  your starting life total."
 *
 * Covers the two life-gated stat tiers (and their menace rider) plus the new hexproof-from-card-type
 * targeting rule: an opponent's instant can't target her, an opponent's sorcery can, and her own
 * controller is never blocked.
 */
class ElendaSaintOfDuskScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(ElendaSaintOfDusk)
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun power(driver: GameTestDriver, id: EntityId) = driver.state.projectedState.getPower(id)
    fun toughness(driver: GameTestDriver, id: EntityId) = driver.state.projectedState.getToughness(id)
    fun hasMenace(driver: GameTestDriver, id: EntityId) =
        driver.state.projectedState.hasKeyword(id, Keyword.MENACE)

    test("at exactly your starting life total she is a plain 4/4 with no menace") {
        val driver = newDriver()
        val me = driver.player1

        val elenda = driver.putCreatureOnBattlefield(me, "Elenda, Saint of Dusk")
        driver.setLifeTotal(me, 20)

        power(driver, elenda) shouldBe 4
        toughness(driver, elenda) shouldBe 4
        hasMenace(driver, elenda) shouldBe false
    }

    test("one life above starting makes her a 5/5 with menace") {
        val driver = newDriver()
        val me = driver.player1

        val elenda = driver.putCreatureOnBattlefield(me, "Elenda, Saint of Dusk")
        driver.setLifeTotal(me, 21)

        power(driver, elenda) shouldBe 5
        toughness(driver, elenda) shouldBe 5
        hasMenace(driver, elenda) shouldBe true
    }

    test("ten life above starting adds the second tier for a 10/10") {
        val driver = newDriver()
        val me = driver.player1

        val elenda = driver.putCreatureOnBattlefield(me, "Elenda, Saint of Dusk")
        driver.setLifeTotal(me, 30)

        power(driver, elenda) shouldBe 10
        toughness(driver, elenda) shouldBe 10
        hasMenace(driver, elenda) shouldBe true
    }

    test("the buffs fall away again when your life drops back to starting") {
        val driver = newDriver()
        val me = driver.player1

        val elenda = driver.putCreatureOnBattlefield(me, "Elenda, Saint of Dusk")
        driver.setLifeTotal(me, 30)
        power(driver, elenda) shouldBe 10

        driver.setLifeTotal(me, 20)
        power(driver, elenda) shouldBe 4
        hasMenace(driver, elenda) shouldBe false
    }

    test("an opponent's instant can't target her, but their sorcery can") {
        val driver = newDriver()
        // player1 is the active player, so it can cast both an instant and a sorcery this turn.
        val caster = driver.player1
        val elenda = driver.putCreatureOnBattlefield(driver.player2, "Elenda, Saint of Dusk")

        // Lightning Bolt is an instant — hexproof from instants makes her an illegal target.
        val bolt = driver.putCardInHand(caster, "Lightning Bolt")
        driver.giveMana(caster, Color.RED, 1)
        driver.castSpell(caster, bolt, listOf(elenda)).isSuccess shouldBe false

        // A sorcery from the same opponent is unaffected by "hexproof from instants".
        val rockslide = driver.putCardInHand(caster, "Rumbling Rockslide")
        driver.giveMana(caster, Color.RED, 4)
        driver.castSpell(caster, rockslide, listOf(elenda)).isSuccess shouldBe true
    }

    test("her own controller can still target her with an instant") {
        val driver = newDriver()
        val me = driver.player1

        val elenda = driver.putCreatureOnBattlefield(me, "Elenda, Saint of Dusk")

        val bolt = driver.putCardInHand(me, "Lightning Bolt")
        driver.giveMana(me, Color.RED, 1)
        driver.castSpell(me, bolt, listOf(elenda)).isSuccess shouldBe true
    }
})
