package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.combat.AttackingComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.tdm.cards.ShockBrigade
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.mobilize
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Tests for the Mobilize N keyword (Tarkir: Dragonstorm, Mardu).
 *
 * "Whenever this creature attacks, create N tapped and attacking 1/1 red Warrior creature
 * tokens. Sacrifice those tokens at the beginning of the next end step."
 *
 * The keyword is wired by the `mobilize(n)` builder helper: a display-only keyword ability
 * plus an attack-triggered CreateTokenEffect (tapped + attacking) whose `sacrificeAtStep`
 * schedules one delayed SacrificeTargetEffect per created token at the next end step.
 *
 * Shock Brigade (Mobilize 1) is the real card under test; an inline "Test Mobilizer" with
 * Mobilize 2 covers the N>1 path.
 */
class MobilizeTest : FunSpec({

    // Inline Mobilize 2 creature for the N>1 path.
    val mobilizer2 = card("Test Mobilizer") {
        manaCost = "{2}{R}"
        typeLine = "Creature — Orc Warrior"
        power = 2
        toughness = 2
        mobilize(2)
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(ShockBrigade, mobilizer2))
        return driver
    }

    fun GameTestDriver.warriorTokens(playerId: EntityId): List<EntityId> =
        getPermanents(playerId).filter { id ->
            val entity = state.getEntity(id)
            entity?.has<TokenComponent>() == true &&
                entity.get<CardComponent>()?.name == "Warrior Token"
        }

    test("Mobilize 1 creates one tapped and attacking Warrior token when the creature attacks") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)

        val attacker = driver.activePlayer!!
        val defender = driver.getOpponent(attacker)

        val brigade = driver.putCreatureOnBattlefield(attacker, "Shock Brigade")
        driver.removeSummoningSickness(brigade)

        driver.passPriorityUntil(com.wingedsheep.sdk.core.Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attacker, listOf(brigade), defender)
        driver.bothPass() // resolve the Mobilize trigger

        val tokens = driver.warriorTokens(attacker)
        tokens.size shouldBe 1

        val token = tokens.first()
        driver.state.getEntity(token)?.has<TappedComponent>() shouldBe true
        val attacking = driver.state.getEntity(token)?.get<AttackingComponent>()
        attacking shouldNotBe null
        attacking!!.defenderId shouldBe defender

        // One delayed sacrifice trigger should have been scheduled for the token.
        driver.state.delayedTriggers.size shouldBe 1
        driver.state.delayedTriggers.first().fireAtStep shouldBe com.wingedsheep.sdk.core.Step.END
    }

    test("Mobilize tokens are sacrificed at the next end step") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)

        val attacker = driver.activePlayer!!
        val defender = driver.getOpponent(attacker)

        val brigade = driver.putCreatureOnBattlefield(attacker, "Shock Brigade")
        driver.removeSummoningSickness(brigade)

        driver.passPriorityUntil(com.wingedsheep.sdk.core.Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attacker, listOf(brigade), defender)
        driver.bothPass()

        driver.warriorTokens(attacker).size shouldBe 1

        // Advance to the end step; the delayed trigger goes on the stack — resolve it.
        driver.passPriorityUntil(com.wingedsheep.sdk.core.Step.END)
        while (driver.state.stack.isNotEmpty()) driver.bothPass()

        // The token has been sacrificed and no longer exists on the battlefield.
        driver.warriorTokens(attacker).size shouldBe 0
        driver.state.delayedTriggers.size shouldBe 0

        // Shock Brigade itself is unaffected.
        driver.findPermanent(attacker, "Shock Brigade") shouldNotBe null
    }

    test("Mobilize 2 creates two Warrior tokens, both sacrificed at the next end step") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)

        val attacker = driver.activePlayer!!
        val defender = driver.getOpponent(attacker)

        val mobilizer = driver.putCreatureOnBattlefield(attacker, "Test Mobilizer")
        driver.removeSummoningSickness(mobilizer)

        driver.passPriorityUntil(com.wingedsheep.sdk.core.Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attacker, listOf(mobilizer), defender)
        driver.bothPass()

        driver.warriorTokens(attacker).size shouldBe 2
        driver.state.delayedTriggers.size shouldBe 2

        driver.passPriorityUntil(com.wingedsheep.sdk.core.Step.END)
        while (driver.state.stack.isNotEmpty()) driver.bothPass()

        driver.warriorTokens(attacker).size shouldBe 0
        driver.state.delayedTriggers.size shouldBe 0
    }

    test("Mobilize token attacks and deals combat damage") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)

        val attacker = driver.activePlayer!!
        val defender = driver.getOpponent(attacker)

        val brigade = driver.putCreatureOnBattlefield(attacker, "Shock Brigade")
        driver.removeSummoningSickness(brigade)

        driver.passPriorityUntil(com.wingedsheep.sdk.core.Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attacker, listOf(brigade), defender)
        driver.bothPass()

        // Shock Brigade (1 power) + one 1/1 Warrior token = 2 combat damage to the defender.
        driver.passPriorityUntil(com.wingedsheep.sdk.core.Step.POSTCOMBAT_MAIN)
        driver.assertLifeTotal(defender, 18)
    }
})
