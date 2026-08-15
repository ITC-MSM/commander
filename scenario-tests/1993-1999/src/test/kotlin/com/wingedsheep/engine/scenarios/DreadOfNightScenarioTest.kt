package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Dread of Night ({B} Enchantment): White creatures get -1/-1.
 *
 * A color-filtered lord modelled with [com.wingedsheep.sdk.scripting.ModifyStats] over a
 * white-creature group filter, affecting every player's white creatures.
 */
class DreadOfNightScenarioTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(
            deck = Deck.of("Plains" to 20, "Swamp" to 20),
            skipMulligans = true
        )
        return driver
    }

    test("Dread of Night gives white creatures -1/-1") {
        val driver = createDriver()
        val you = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // White Knight is a 2/2 white creature.
        val knight = driver.putCreatureOnBattlefield(you, "White Knight")
        projector.getProjectedPower(driver.state, knight) shouldBe 2
        projector.getProjectedToughness(driver.state, knight) shouldBe 2

        driver.putPermanentOnBattlefield(you, "Dread of Night")

        projector.getProjectedPower(driver.state, knight) shouldBe 1
        projector.getProjectedToughness(driver.state, knight) shouldBe 1
    }

    test("Dread of Night affects white creatures regardless of controller and ignores non-white") {
        val driver = createDriver()
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val theirWhite = driver.putCreatureOnBattlefield(opponent, "White Knight")
        // Grizzly Bears is a green 2/2 — unaffected.
        val green = driver.putCreatureOnBattlefield(you, "Grizzly Bears")

        driver.putPermanentOnBattlefield(you, "Dread of Night")

        projector.getProjectedPower(driver.state, theirWhite) shouldBe 1
        projector.getProjectedToughness(driver.state, theirWhite) shouldBe 1
        projector.getProjectedPower(driver.state, green) shouldBe 2
        projector.getProjectedToughness(driver.state, green) shouldBe 2
    }

    test("Two Dread of Night stack to -2/-2") {
        val driver = createDriver()
        val you = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val knight = driver.putCreatureOnBattlefield(you, "White Knight")

        driver.putPermanentOnBattlefield(you, "Dread of Night")
        driver.putPermanentOnBattlefield(you, "Dread of Night")

        projector.getProjectedPower(driver.state, knight) shouldBe 0
        projector.getProjectedToughness(driver.state, knight) shouldBe 0
    }
})
