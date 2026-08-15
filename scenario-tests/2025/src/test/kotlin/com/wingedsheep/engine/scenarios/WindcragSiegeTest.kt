package com.wingedsheep.engine.scenarios
import com.wingedsheep.engine.state.components.battlefield.ChoiceValue
import com.wingedsheep.sdk.scripting.ChoiceSlot
import com.wingedsheep.engine.state.components.battlefield.CastChoicesComponent

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.tdm.cards.HollowmurkSiege
import com.wingedsheep.mtg.sets.definitions.tdm.cards.WindcragSiege
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Resolve any number of attack-caused triggers that are each asking for a single target,
 * submitting [attacker] as the target, then draining the stack.
 */
private fun resolveAttackTriggers(driver: GameTestDriver, you: EntityId, attacker: EntityId) {
    var guard = 0
    while ((driver.state.stack.isNotEmpty() || driver.state.pendingDecision is ChooseTargetsDecision) && guard++ < 30) {
        if (driver.state.pendingDecision is ChooseTargetsDecision) {
            driver.submitTargetSelection(you, listOf(attacker))
        } else {
            driver.bothPass()
        }
    }
}

/**
 * Scenario tests for Windcrag Siege's two modes.
 *
 * Mardu: "If a creature attacking causes a triggered ability of a permanent you control to
 *         trigger, that ability triggers an additional time." (new AdditionalAttackTriggers
 *         static ability + duplicateAttackTriggers engine pass).
 * Jeskai: "At the beginning of your upkeep, create a 1/1 red Goblin creature token. It gains
 *          lifelink and haste until end of turn."
 */
class WindcragSiegeTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(WindcragSiege, HollowmurkSiege))
        return driver
    }

    test("Mardu doubles an attack-caused triggered ability of a permanent you control") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)

        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)

        val attacker = driver.putCreatureOnBattlefield(you, "Centaur Courser")
        driver.removeSummoningSickness(attacker)

        // Hollowmurk Abzan: "whenever you attack, put a +1/+1 counter on target attacking creature."
        val hollowmurk = driver.putPermanentOnBattlefield(you, "Hollowmurk Siege")
        driver.addComponent(hollowmurk, CastChoicesComponent(chosen = mapOf(ChoiceSlot.MODE to ChoiceValue.TextChoice("abzan"))))

        // Windcrag Mardu doubles that attack trigger.
        val windcrag = driver.putPermanentOnBattlefield(you, "Windcrag Siege")
        driver.addComponent(windcrag, CastChoicesComponent(chosen = mapOf(ChoiceSlot.MODE to ChoiceValue.TextChoice("mardu"))))

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(you, listOf(attacker), opponent)
        // Two copies of Hollowmurk's attack trigger; each asks for its target (the attacker).
        resolveAttackTriggers(driver, you, attacker)

        driver.state.getEntity(attacker)?.get<CountersComponent>()
            ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 2
    }

    test("Without Mardu, the attack trigger fires only once") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)

        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)

        val attacker = driver.putCreatureOnBattlefield(you, "Centaur Courser")
        driver.removeSummoningSickness(attacker)

        val hollowmurk = driver.putPermanentOnBattlefield(you, "Hollowmurk Siege")
        driver.addComponent(hollowmurk, CastChoicesComponent(chosen = mapOf(ChoiceSlot.MODE to ChoiceValue.TextChoice("abzan"))))

        // Windcrag present but in Jeskai mode — no attack-trigger doubling.
        val windcrag = driver.putPermanentOnBattlefield(you, "Windcrag Siege")
        driver.addComponent(windcrag, CastChoicesComponent(chosen = mapOf(ChoiceSlot.MODE to ChoiceValue.TextChoice("jeskai"))))

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(you, listOf(attacker), opponent)
        resolveAttackTriggers(driver, you, attacker)

        driver.state.getEntity(attacker)?.get<CountersComponent>()
            ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
    }

    test("Jeskai creates a 1/1 red Goblin with lifelink and haste at your upkeep") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)

        val you = driver.activePlayer!!
        val creaturesBefore = driver.getCreatures(you).toSet()

        val windcrag = driver.putPermanentOnBattlefield(you, "Windcrag Siege")
        driver.addComponent(windcrag, CastChoicesComponent(chosen = mapOf(ChoiceSlot.MODE to ChoiceValue.TextChoice("jeskai"))))

        // Advance through the opponent's turn back to OUR next upkeep so the trigger fires.
        var guard = 0
        while (!(driver.state.activePlayerId == you && driver.state.step == Step.UPKEEP) && guard++ < 30) {
            driver.passPriorityUntil(Step.END)
            while (driver.state.stack.isNotEmpty()) driver.bothPass()
            driver.passPriorityUntil(Step.UPKEEP)
        }
        // Resolve the upkeep trigger.
        while (driver.state.stack.isNotEmpty()) driver.bothPass()

        // A 1/1 red Goblin token was created; the one made this upkeep carries lifelink and haste
        // (granted only until end of turn via the CREATED_TOKENS pipeline).
        val newTokens = driver.getCreatures(you).filter { it !in creaturesBefore }
        newTokens.isNotEmpty() shouldBe true
        val freshToken = newTokens.first { driver.state.projectedState.hasKeyword(it, Keyword.HASTE) }
        driver.state.projectedState.hasKeyword(freshToken, Keyword.LIFELINK) shouldBe true
        driver.state.projectedState.hasKeyword(freshToken, Keyword.HASTE) shouldBe true
    }
})
