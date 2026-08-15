package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.woe.cards.MockingSprite
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Mocking Sprite — {2}{U} Creature — Faerie Rogue 2/1
 *
 * "Flying
 *  Instant and sorcery spells you cast cost {1} less to cast."
 */
class MockingSpriteScenarioTest : FunSpec({

    // Inline {2} instant that gains 2 life — cheap enough to observe the reduction:
    // with the Sprite out it costs {1}, so a single mana in the pool pays for it.
    val GainTwoLife = CardDefinition.instant(
        name = "Gain Two Life",
        manaCost = ManaCost.parse("{2}"),
        oracleText = "You gain 2 life.",
        script = CardScript.spell(effect = Effects.GainLife(2))
    )

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCards(listOf(MockingSprite, GainTwoLife))
        return driver
    }

    test("instant and sorcery spells you cast cost {1} less") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 30), startingLife = 20)
        driver.putCreatureOnBattlefield(driver.activePlayer!!, "Mocking Sprite")
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val me = driver.activePlayer!!
        val spell = driver.putCardInHand(me, "Gain Two Life")

        // Only one generic mana available. Without the Sprite the {2} spell would be
        // unaffordable; the {1} reduction makes it castable.
        driver.giveColorlessMana(me, 1)
        driver.castSpell(me, spell)
        driver.bothPass()

        driver.getLifeTotal(me) shouldBe 22
    }
})
