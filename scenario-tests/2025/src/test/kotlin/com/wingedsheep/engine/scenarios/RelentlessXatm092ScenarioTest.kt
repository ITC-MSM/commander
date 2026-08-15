package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.RelentlessXatm092
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Relentless X-ATM092 — {6} Artifact Creature — Robot Spider, 6/5.
 *
 * "This creature can't be blocked except by three or more creatures.
 *  {8}: Return this card from your graveyard to the battlefield tapped with a finality counter on
 *  it."
 *
 * Exercises the graveyard-activated recursion that returns the card to the battlefield tapped with a
 * finality counter. (The generalized-menace evasion is the shared `CantBeBlockedByFewerThan(3)`
 * static, already covered by `TrollOfKhazadDumScenarioTest`.)
 */
class RelentlessXatm092ScenarioTest : FunSpec({

    val abilityId = RelentlessXatm092.activatedAbilities.first().id

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(RelentlessXatm092)
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    test("{8} returns it from the graveyard to the battlefield tapped with a finality counter") {
        val driver = createDriver()
        val me = driver.activePlayer!!

        val xatm = driver.putCardInGraveyard(me, "Relentless X-ATM092")
        driver.giveMana(me, Color.RED, 8)

        driver.submit(ActivateAbility(playerId = me, sourceId = xatm, abilityId = abilityId)).isSuccess shouldBe true
        driver.bothPass()
        driver.isPaused shouldBe false

        // Back on the battlefield, out of the graveyard.
        driver.state.getZone(ZoneKey(me, Zone.BATTLEFIELD)).contains(xatm) shouldBe true
        driver.state.getZone(ZoneKey(me, Zone.GRAVEYARD)).contains(xatm) shouldBe false

        // It entered tapped, with one finality counter.
        driver.isTapped(xatm) shouldBe true
        val counters = driver.state.getEntity(xatm)?.get<CountersComponent>()?.counters ?: emptyMap()
        counters[CounterType.FINALITY] shouldBe 1
    }
})
