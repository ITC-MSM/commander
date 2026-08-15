package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.SunshotMilitia
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Sunshot Militia (LCI #168).
 *
 * Sunshot Militia {1}{R}
 * Creature — Human Soldier 1/3
 * Tap two untapped artifacts and/or creatures you control: This creature deals 1 damage to each
 * opponent. Activate only as a sorcery.
 *
 * Covers:
 *  - Happy path: tapping two creatures deals 1 damage to the opponent and taps both permanents.
 *  - Self-tap: the Militia itself may be one of the two (its cost has no "other").
 *  - Sorcery-speed gate: activation is rejected outside a main phase with an empty stack.
 *  - Cost gate: activation is rejected without two matching untapped permanents.
 */
class SunshotMilitiaScenarioTest : FunSpec({

    val abilityId = SunshotMilitia.activatedAbilities.first().id

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(SunshotMilitia))
        return driver
    }

    test("tapping two creatures deals 1 damage to each opponent") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val militia = driver.putCreatureOnBattlefield(activePlayer, "Sunshot Militia")
        driver.removeSummoningSickness(militia)
        val bear1 = driver.putCreatureOnBattlefield(activePlayer, "Grizzly Bears")
        driver.removeSummoningSickness(bear1)
        val bear2 = driver.putCreatureOnBattlefield(activePlayer, "Grizzly Bears")
        driver.removeSummoningSickness(bear2)

        driver.getLifeTotal(opponent) shouldBe 20

        driver.submitSuccess(
            ActivateAbility(
                playerId = activePlayer,
                sourceId = militia,
                abilityId = abilityId,
                costPayment = AdditionalCostPayment(tappedPermanents = listOf(bear1, bear2))
            )
        )
        driver.bothPass() // resolve the ability

        driver.isTapped(bear1) shouldBe true
        driver.isTapped(bear2) shouldBe true
        driver.getLifeTotal(opponent) shouldBe 19
    }

    test("the Militia itself may be one of the two tapped permanents (no \"other\" in the cost)") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val militia = driver.putCreatureOnBattlefield(activePlayer, "Sunshot Militia")
        driver.removeSummoningSickness(militia)
        val bear = driver.putCreatureOnBattlefield(activePlayer, "Grizzly Bears")
        driver.removeSummoningSickness(bear)

        driver.submitSuccess(
            ActivateAbility(
                playerId = activePlayer,
                sourceId = militia,
                abilityId = abilityId,
                costPayment = AdditionalCostPayment(tappedPermanents = listOf(militia, bear))
            )
        )
        driver.bothPass() // resolve the ability

        driver.isTapped(militia) shouldBe true
        driver.isTapped(bear) shouldBe true
        driver.getLifeTotal(opponent) shouldBe 19
    }

    test("cannot be activated at instant speed (sorcery-speed gate)") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val activePlayer = driver.activePlayer!!

        val militia = driver.putCreatureOnBattlefield(activePlayer, "Sunshot Militia")
        driver.removeSummoningSickness(militia)
        val bear1 = driver.putCreatureOnBattlefield(activePlayer, "Grizzly Bears")
        driver.removeSummoningSickness(bear1)
        val bear2 = driver.putCreatureOnBattlefield(activePlayer, "Grizzly Bears")
        driver.removeSummoningSickness(bear2)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)

        driver.submitExpectFailure(
            ActivateAbility(
                playerId = activePlayer,
                sourceId = militia,
                abilityId = abilityId,
                costPayment = AdditionalCostPayment(tappedPermanents = listOf(bear1, bear2))
            )
        )
    }

    test("cannot be activated without two matching untapped permanents") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val activePlayer = driver.activePlayer!!

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val militia = driver.putCreatureOnBattlefield(activePlayer, "Sunshot Militia")
        driver.removeSummoningSickness(militia)
        // Only Sunshot Militia itself is untapped — cost requires two matching permanents.

        driver.submitExpectFailure(
            ActivateAbility(
                playerId = activePlayer,
                sourceId = militia,
                abilityId = abilityId,
                costPayment = AdditionalCostPayment(tappedPermanents = listOf(militia))
            )
        )
    }
})
