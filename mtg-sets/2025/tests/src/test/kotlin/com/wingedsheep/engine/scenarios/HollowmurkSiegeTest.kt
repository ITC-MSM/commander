package com.wingedsheep.engine.scenarios
import com.wingedsheep.engine.state.components.battlefield.ChoiceValue
import com.wingedsheep.sdk.scripting.ChoiceSlot
import com.wingedsheep.engine.state.components.battlefield.CastChoicesComponent

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.tdm.cards.HollowmurkSiege
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Hollowmurk Siege's two modes.
 *
 * Abzan: "Whenever you attack, put a +1/+1 counter on target attacking creature. It gains
 *         menace until end of turn."
 * Sultai: "Whenever a counter is put on a creature you control, draw a card. This ability
 *          triggers only once each turn." Exercised here by an Abzan Hollowmurk placing the
 *          counter that the Sultai Hollowmurk sees.
 */
class HollowmurkSiegeTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(HollowmurkSiege))
        return driver
    }

    test("Abzan: attacking puts a +1/+1 counter on an attacker and grants it menace") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)

        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)

        val attacker = driver.putCreatureOnBattlefield(you, "Centaur Courser")
        driver.removeSummoningSickness(attacker)

        val siege = driver.putPermanentOnBattlefield(you, "Hollowmurk Siege")
        driver.addComponent(siege, CastChoicesComponent(chosen = mapOf(ChoiceSlot.MODE to ChoiceValue.TextChoice("abzan"))))

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(you, listOf(attacker), opponent)
        // The "whenever you attack" trigger asks for its target (the attacking creature).
        driver.submitTargetSelection(you, listOf(attacker))
        while (driver.state.stack.isNotEmpty()) driver.bothPass()

        driver.state.getEntity(attacker)?.get<CountersComponent>()
            ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
        driver.state.projectedState.hasKeyword(attacker, Keyword.MENACE) shouldBe true
    }

    test("Sultai: drawing once when a counter is put on a creature you control") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)

        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)

        val attacker = driver.putCreatureOnBattlefield(you, "Centaur Courser")
        driver.removeSummoningSickness(attacker)

        // Abzan siege supplies the counter; Sultai siege reacts to it.
        val abzan = driver.putPermanentOnBattlefield(you, "Hollowmurk Siege")
        driver.addComponent(abzan, CastChoicesComponent(chosen = mapOf(ChoiceSlot.MODE to ChoiceValue.TextChoice("abzan"))))
        val sultai = driver.putPermanentOnBattlefield(you, "Hollowmurk Siege")
        driver.addComponent(sultai, CastChoicesComponent(chosen = mapOf(ChoiceSlot.MODE to ChoiceValue.TextChoice("sultai"))))

        val handBefore = driver.getHandSize(you)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(you, listOf(attacker), opponent)
        // Abzan's "whenever you attack" trigger targets the attacker.
        driver.submitTargetSelection(you, listOf(attacker))
        while (driver.state.stack.isNotEmpty()) driver.bothPass()

        // Abzan put one +1/+1 counter on the attacker; Sultai drew exactly one card.
        driver.getHandSize(you) shouldBe handBefore + 1
    }
})
