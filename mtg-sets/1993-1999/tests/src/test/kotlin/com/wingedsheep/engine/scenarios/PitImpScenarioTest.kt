package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Pit Imp ({B} Creature — Imp 0/1):
 * Flying. {B}: This creature gets +1/+0 until end of turn. Activate no more than twice each turn.
 */
class PitImpScenarioTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(
            deck = Deck.of("Swamp" to 40),
            skipMulligans = true
        )
        return driver
    }

    fun pumpAbilityId(driver: GameTestDriver) =
        driver.cardRegistry.getCard("Pit Imp")!!.script.activatedAbilities[0].id

    test("Pit Imp has flying") {
        val driver = createDriver()
        val you = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val imp = driver.putCreatureOnBattlefield(you, "Pit Imp")
        projector.project(driver.state).hasKeyword(imp, Keyword.FLYING) shouldBe true
    }

    test("Pit Imp pump gives +1/+0 and can be activated twice but not a third time") {
        val driver = createDriver()
        val you = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val imp = driver.putCreatureOnBattlefield(you, "Pit Imp")
        projector.getProjectedPower(driver.state, imp) shouldBe 0

        driver.giveMana(you, Color.BLACK, 3)

        val first = driver.submit(ActivateAbility(playerId = you, sourceId = imp, abilityId = pumpAbilityId(driver)))
        first.isSuccess shouldBe true
        driver.bothPass()
        projector.getProjectedPower(driver.state, imp) shouldBe 1
        projector.getProjectedToughness(driver.state, imp) shouldBe 1

        val second = driver.submit(ActivateAbility(playerId = you, sourceId = imp, abilityId = pumpAbilityId(driver)))
        second.isSuccess shouldBe true
        driver.bothPass()
        projector.getProjectedPower(driver.state, imp) shouldBe 2

        // Third activation in the same turn is illegal (MaxPerTurn(2)).
        val third = driver.submit(ActivateAbility(playerId = you, sourceId = imp, abilityId = pumpAbilityId(driver)))
        third.isSuccess shouldBe false
    }
})
