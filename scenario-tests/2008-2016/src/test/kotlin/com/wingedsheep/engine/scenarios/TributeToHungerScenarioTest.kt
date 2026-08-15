package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.isd.cards.TributeToHunger
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tribute to Hunger (ISD #119) — {2}{B} Instant.
 *
 *   Target opponent sacrifices a creature of their choice. You gain life equal to that creature's toughness.
 *
 * The edict ([com.wingedsheep.sdk.scripting.effects.ForceSacrificeEffect]) records the sacrificed
 * permanent in the effect context; the composed [Effects.GainLife] then reads the sacrificed
 * creature's *toughness*. When the target opponent controls exactly one creature it is
 * auto-sacrificed at resolution.
 */
class TributeToHungerScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(TributeToHunger))
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true, startingPlayer = 0)
        return driver
    }

    test("opponent sacrifices their only creature and you gain life equal to its toughness") {
        val driver = createDriver()
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Goblin Guide is a 2/1 — power 2, toughness 1. Life gained must be 1 (toughness), not 2 (power).
        driver.putCreatureOnBattlefield(opponent, "Goblin Guide")
        val lifeBefore = driver.getLifeTotal(you)

        val spell = driver.putCardInHand(you, "Tribute to Hunger")
        driver.giveMana(you, Color.BLACK, 1)
        driver.giveColorlessMana(you, 2)
        driver.castSpell(you, spell, listOf(opponent)).isSuccess shouldBe true
        driver.bothPass()

        driver.getCreatures(opponent).size shouldBe 0
        driver.getLifeTotal(you) shouldBe lifeBefore + 1
    }

    test("resolves harmlessly with no life gain when the opponent controls no creatures") {
        val driver = createDriver()
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val lifeBefore = driver.getLifeTotal(you)

        val spell = driver.putCardInHand(you, "Tribute to Hunger")
        driver.giveMana(you, Color.BLACK, 1)
        driver.giveColorlessMana(you, 2)
        driver.castSpell(you, spell, listOf(opponent)).error shouldBe null
        driver.bothPass()

        driver.getLifeTotal(you) shouldBe lifeBefore
    }
})
