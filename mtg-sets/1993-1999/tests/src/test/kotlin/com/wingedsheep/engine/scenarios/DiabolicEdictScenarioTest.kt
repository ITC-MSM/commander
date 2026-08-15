package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Diabolic Edict ({1}{B} Instant): Target player sacrifices a creature of their choice.
 *
 * The card is an edict, modelled with [com.wingedsheep.sdk.scripting.effects.ForceSacrificeEffect]
 * targeting a player. When the targeted player controls exactly one creature, it is
 * auto-sacrificed at resolution.
 */
class DiabolicEdictScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(
            deck = Deck.of("Swamp" to 40),
            skipMulligans = true
        )
        return driver
    }

    test("Diabolic Edict forces the targeted player to sacrifice their only creature") {
        val driver = createDriver()
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Opponent has a single creature -> auto-sacrificed.
        driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")

        val edict = driver.putCardInHand(you, "Diabolic Edict")
        driver.giveMana(you, Color.BLACK, 2)
        driver.castSpell(you, edict, listOf(opponent))
        driver.bothPass()

        driver.getCreatures(opponent).size shouldBe 0
    }

    test("Diabolic Edict can target its own caster") {
        val driver = createDriver()
        val you = driver.activePlayer!!

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putCreatureOnBattlefield(you, "Grizzly Bears")

        val edict = driver.putCardInHand(you, "Diabolic Edict")
        driver.giveMana(you, Color.BLACK, 2)
        driver.castSpell(you, edict, listOf(you))
        driver.bothPass()

        driver.getCreatures(you).size shouldBe 0
    }

    test("Diabolic Edict resolves harmlessly when the targeted player has no creatures") {
        val driver = createDriver()
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val edict = driver.putCardInHand(you, "Diabolic Edict")
        driver.giveMana(you, Color.BLACK, 2)
        val result = driver.castSpell(you, edict, listOf(opponent))
        result.error shouldBe null
        driver.bothPass()

        driver.getCreatures(opponent).size shouldBe 0
    }
})
