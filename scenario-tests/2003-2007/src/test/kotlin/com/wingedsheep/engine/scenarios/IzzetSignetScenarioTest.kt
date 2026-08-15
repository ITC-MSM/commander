package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.gpt.cards.IzzetSignet
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Izzet Signet: {1}, {T}: Add {U}{R}. Pins that paying {1} (from pool) + tapping the artifact
 * yields one blue and one red mana in the pool.
 */
class IzzetSignetScenarioTest : FunSpec({

    val signetAbilityId = IzzetSignet.activatedAbilities[0].id

    fun setup(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(IzzetSignet)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 20), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    test("activating Izzet Signet adds one blue and one red mana") {
        val driver = setup()
        val player = driver.activePlayer!!

        val signet = driver.putPermanentOnBattlefield(player, "Izzet Signet")
        driver.giveColorlessMana(player, 1) // pays the {1} portion of the cost

        val result = driver.submit(
            ActivateAbility(
                playerId = player,
                sourceId = signet,
                abilityId = signetAbilityId,
                paymentStrategy = PaymentStrategy.FromPool,
            )
        )
        result.isSuccess shouldBe true

        val pool = driver.state.getEntity(player)!!.get<ManaPoolComponent>()!!
        pool.blue shouldBe 1
        pool.red shouldBe 1
    }
})
