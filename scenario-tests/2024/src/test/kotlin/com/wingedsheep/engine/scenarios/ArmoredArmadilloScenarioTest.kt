package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.otj.cards.ArmoredArmadillo
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Armored Armadillo (OTJ #3) — {W} Armadillo, 0/4, Ward {1}.
 *
 *   "{3}{W}: This creature gets +X/+0 until end of turn, where X is its toughness."
 *
 * Verifies the toughness-scaled self-pump activated ability resolves with X equal to the
 * Armadillo's current toughness (4), making it a 4/4 until end of turn.
 */
class ArmoredArmadilloScenarioTest : FunSpec({

    val pumpAbility = ArmoredArmadillo.activatedAbilities.first().id

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(ArmoredArmadillo)
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    test("the activated ability adds the Armadillo's toughness to its power") {
        val driver = createDriver()
        val player = driver.activePlayer!!

        val armadillo = driver.putCreatureOnBattlefield(player, "Armored Armadillo")
        driver.giveMana(player, Color.WHITE, 4) // {3}{W}

        driver.state.projectedState.getPower(armadillo) shouldBe 0
        driver.state.projectedState.getToughness(armadillo) shouldBe 4

        driver.submitSuccess(ActivateAbility(player, armadillo, pumpAbility))
        driver.bothPass() // resolve the ability

        // X = its toughness = 4, so +4/+0 → 4/4.
        driver.state.projectedState.getPower(armadillo) shouldBe 4
        driver.state.projectedState.getToughness(armadillo) shouldBe 4
    }

    test("the pump wears off at end of turn") {
        val driver = createDriver()
        val player = driver.activePlayer!!

        val armadillo = driver.putCreatureOnBattlefield(player, "Armored Armadillo")
        driver.giveMana(player, Color.WHITE, 4)

        driver.submitSuccess(ActivateAbility(player, armadillo, pumpAbility))
        driver.bothPass()
        driver.state.projectedState.getPower(armadillo) shouldBe 4

        driver.passPriorityUntil(Step.POSTCOMBAT_MAIN)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.state.projectedState.getPower(armadillo) shouldBe 0
        driver.state.projectedState.getToughness(armadillo) shouldBe 4
    }
})
