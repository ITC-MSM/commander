package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.bro.cards.GiantCindermaw
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Giant Cindermaw — {2}{R} Creature — Dinosaur Beast 4/3
 *
 * "Trample
 *  Players can't gain life."
 */
class GiantCindermawScenarioTest : FunSpec({

    val projector = StateProjector()

    // Inline test spell: "You gain 3 life." Verifies life gain is prevented while Cindermaw is out.
    val GainThreeLife = CardDefinition.instant(
        name = "Gain Three Life",
        manaCost = ManaCost.parse("{W}"),
        oracleText = "You gain 3 life.",
        script = CardScript.spell(effect = Effects.GainLife(3))
    )

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCards(listOf(GiantCindermaw, GainThreeLife))
        return driver
    }

    test("has trample") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 30), startingLife = 20)

        val me = driver.activePlayer!!
        val maw = driver.putCreatureOnBattlefield(me, "Giant Cindermaw")

        projector.project(driver.state).hasKeyword(maw, Keyword.TRAMPLE) shouldBe true
    }

    test("prevents all players from gaining life") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 20, "Plains" to 20), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val me = driver.activePlayer!!
        driver.putCreatureOnBattlefield(me, "Giant Cindermaw")

        // A spell would gain 3 life — but no life is gained while Cindermaw is out.
        val gain = driver.putCardInHand(me, "Gain Three Life")
        driver.giveMana(me, Color.WHITE, 1)
        driver.castSpell(me, gain)
        driver.bothPass()

        driver.getLifeTotal(me) shouldBe 20
    }
})
