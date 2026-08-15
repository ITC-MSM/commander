package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.tmp.cards.CalderaLake
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Caldera Lake (TMP #316)
 * Land — enters tapped
 * {T}: Add {C}.
 * {T}: Add {U} or {R}. This land deals 1 damage to you.
 */
class CalderaLakeScenarioTest : FunSpec({

    val colorlessAbilityId = CalderaLake.activatedAbilities[0].id
    val blueAbilityId = CalderaLake.activatedAbilities[1].id
    val redAbilityId = CalderaLake.activatedAbilities[2].id

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(CalderaLake)
        return driver
    }

    test("the {U} ability adds blue mana and deals 1 damage") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val activePlayer = driver.activePlayer!!
        val lake = driver.putPermanentOnBattlefield(activePlayer, "Caldera Lake")
        driver.untapPermanent(lake) // ensure available regardless of ETB-tapped placement
        val before = driver.getLifeTotal(activePlayer)

        val result = driver.submit(
            ActivateAbility(playerId = activePlayer, sourceId = lake, abilityId = blueAbilityId)
        )
        result.isSuccess shouldBe true

        val pool = driver.state.getEntity(activePlayer)?.get<ManaPoolComponent>()
        pool?.blue shouldBe 1
        driver.getLifeTotal(activePlayer) shouldBe (before - 1)
    }

    test("the {R} ability adds red mana and deals 1 damage") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val activePlayer = driver.activePlayer!!
        val lake = driver.putPermanentOnBattlefield(activePlayer, "Caldera Lake")
        driver.untapPermanent(lake)
        val before = driver.getLifeTotal(activePlayer)

        val result = driver.submit(
            ActivateAbility(playerId = activePlayer, sourceId = lake, abilityId = redAbilityId)
        )
        result.isSuccess shouldBe true

        val pool = driver.state.getEntity(activePlayer)?.get<ManaPoolComponent>()
        pool?.red shouldBe 1
        driver.getLifeTotal(activePlayer) shouldBe (before - 1)
    }

    test("the {C} ability adds colorless mana and deals NO damage") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val activePlayer = driver.activePlayer!!
        val lake = driver.putPermanentOnBattlefield(activePlayer, "Caldera Lake")
        driver.untapPermanent(lake)
        val before = driver.getLifeTotal(activePlayer)

        val result = driver.submit(
            ActivateAbility(playerId = activePlayer, sourceId = lake, abilityId = colorlessAbilityId)
        )
        result.isSuccess shouldBe true

        val pool = driver.state.getEntity(activePlayer)?.get<ManaPoolComponent>()
        pool?.colorless shouldBe 1
        driver.getLifeTotal(activePlayer) shouldBe before
    }
})
