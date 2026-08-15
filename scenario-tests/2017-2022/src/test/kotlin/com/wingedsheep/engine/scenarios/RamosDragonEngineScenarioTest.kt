package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.atq.cards.Ornithopter
import com.wingedsheep.mtg.sets.definitions.c17.cards.RamosDragonEngine
import com.wingedsheep.mtg.sets.definitions.rav.cards.LightningHelix
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class RamosDragonEngineScenarioTest : FunSpec({

    fun setup(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(RamosDragonEngine, LightningHelix, Ornithopter))
        driver.initMirrorMatch(Deck.of("Forest" to 40), skipMulligans = true)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun counters(driver: GameTestDriver, ramos: EntityId): Int =
        driver.state.getEntity(ramos)?.get<CountersComponent>()
            ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    test("the cast trigger counts the spell's colors and gives a colorless spell zero counters") {
        val driver = setup()
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)
        val ramos = driver.putCreatureOnBattlefield(player, "Ramos, Dragon Engine")

        val helix = driver.putCardInHand(player, "Lightning Helix")
        driver.giveMana(player, Color.RED, 1)
        driver.giveMana(player, Color.WHITE, 1)
        driver.castSpell(player, helix, targets = listOf(opponent)).isSuccess shouldBe true
        driver.bothPass() // Ramos trigger

        counters(driver, ramos) shouldBe 2
        driver.bothPass() // Lightning Helix

        val ornithopter = driver.putCardInHand(player, "Ornithopter")
        driver.castSpell(player, ornithopter).isSuccess shouldBe true
        driver.bothPass() // Ramos trigger

        counters(driver, ramos) shouldBe 2
    }

    test("removing five counters adds two mana of each color and is limited to once each turn") {
        val driver = setup()
        val player = driver.activePlayer!!
        val ramos = driver.putCreatureOnBattlefield(player, "Ramos, Dragon Engine")
        driver.replaceState(
            driver.state.updateEntity(ramos) {
                it.with(CountersComponent().withAdded(CounterType.PLUS_ONE_PLUS_ONE, 10))
            }
        )
        val abilityId = RamosDragonEngine.activatedAbilities.single().id

        driver.submitSuccess(ActivateAbility(player, ramos, abilityId))

        val pool = driver.state.getEntity(player)?.get<ManaPoolComponent>() ?: ManaPoolComponent()
        pool.white shouldBe 2
        pool.blue shouldBe 2
        pool.black shouldBe 2
        pool.red shouldBe 2
        pool.green shouldBe 2
        counters(driver, ramos) shouldBe 5

        driver.submit(ActivateAbility(player, ramos, abilityId)).isSuccess shouldBe false
        counters(driver, ramos) shouldBe 5
    }
})
