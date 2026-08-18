package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mrd.cards.LightningCoils
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class LightningCoilsScenarioTest : FunSpec({

    fun addChargeCounters(driver: GameTestDriver, entityId: EntityId, count: Int) {
        driver.replaceState(
            driver.state.updateEntity(entityId) { container ->
                val existing = container.get<CountersComponent>() ?: CountersComponent()
                container.with(existing.withAdded(CounterType.CHARGE, count))
            }
        )
    }

    fun tokenCount(driver: GameTestDriver, playerId: EntityId): Int =
        driver.state.getBattlefield(playerId).count { id ->
            driver.state.getEntity(id)?.has<TokenComponent>() == true
        }

    test("upkeep removes every charge counter, creates that many hasty tokens, then exiles them") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + LightningCoils)
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))

        val controller = driver.activePlayer!!
        val coils = driver.putPermanentOnBattlefield(controller, "Lightning Coils")
        addChargeCounters(driver, coils, 6)

        driver.passPriorityUntil(Step.UPKEEP)
        driver.bothPass()

        (driver.state.getEntity(coils)!!.get<CountersComponent>()
            ?.getCount(CounterType.CHARGE) ?: 0) shouldBe 0
        tokenCount(driver, controller) shouldBe 6

        driver.passPriorityUntil(Step.END)
        while (driver.stackSize > 0) driver.bothPass()

        tokenCount(driver, controller) shouldBe 0
    }
})
