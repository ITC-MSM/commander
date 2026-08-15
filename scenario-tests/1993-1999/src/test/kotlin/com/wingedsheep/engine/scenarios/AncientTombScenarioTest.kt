package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.tmp.cards.AncientTomb
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Ancient Tomb (TMP #315)
 * Land
 * {T}: Add {C}{C}. This land deals 2 damage to you.
 */
class AncientTombScenarioTest : FunSpec({

    val manaAbilityId = AncientTomb.activatedAbilities[0].id

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(AncientTomb)
        return driver
    }

    test("tapping Ancient Tomb adds {C}{C} and deals 2 damage to its controller") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val activePlayer = driver.activePlayer!!
        val tomb = driver.putPermanentOnBattlefield(activePlayer, "Ancient Tomb")
        val before = driver.getLifeTotal(activePlayer)

        val result = driver.submit(
            ActivateAbility(playerId = activePlayer, sourceId = tomb, abilityId = manaAbilityId)
        )
        result.isSuccess shouldBe true

        driver.isTapped(tomb) shouldBe true

        val pool = driver.state.getEntity(activePlayer)?.get<ManaPoolComponent>()
        pool?.colorless shouldBe 2

        driver.getLifeTotal(activePlayer) shouldBe (before - 2)
    }
})
