package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.blb.cards.SeasonedWarrenguard
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Seasoned Warrenguard (BLB): "Whenever this creature attacks while you control a token,
 * this creature gets +2/+0 until end of turn."
 *
 * Regression guard (printed ruling): the token check happens ONLY when the trigger fires
 * (attack declaration). If the token leaves before the ability resolves, the pump still
 * applies. The old implementation re-checked at resolution via a ConditionalEffect.
 */
class SeasonedWarrenguardTest : FunSpec({

    val projector = StateProjector()

    val makeRabbit: CardDefinition = card("Test Rabbit Charm") {
        manaCost = "{W}"
        typeLine = "Instant"
        spell {
            effect = Effects.CreateToken(
                power = 1, toughness = 1,
                colors = setOf(Color.WHITE),
                creatureTypes = setOf("Rabbit")
            )
        }
    }

    val testBreak: CardDefinition = card("Test Break") {
        manaCost = "{B}"
        typeLine = "Instant"
        spell {
            val t = target("target creature to destroy", Targets.Creature)
            effect = Effects.Destroy(t)
        }
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(SeasonedWarrenguard, makeRabbit, testBreak))
        return driver
    }

    fun GameTestDriver.advanceToDeclareAttackersAs(player: com.wingedsheep.sdk.model.EntityId) {
        passPriorityUntil(Step.DECLARE_ATTACKERS)
        var safety = 0
        while (activePlayer != player && safety < 50) {
            bothPass()
            passPriorityUntil(Step.DECLARE_ATTACKERS)
            safety++
        }
    }

    test("losing the token in response does not stop the pump (checked at trigger time only)") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40))
        var active = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        active = driver.activePlayer!!

        val guard = driver.putCreatureOnBattlefield(active, "Seasoned Warrenguard")
        driver.removeSummoningSickness(guard)

        // Make a Rabbit token via a real spell so it is a token.
        driver.giveMana(active, Color.WHITE, 1)
        val charm = driver.putCardInHand(active, "Test Rabbit Charm")
        driver.castSpell(active, charm)
        driver.bothPass()
        val token = driver.findPermanent(active, "Rabbit Token")
        token.shouldNotBeNull()

        driver.advanceToDeclareAttackersAs(active)
        driver.declareAttackers(active, listOf(guard), driver.getOpponent(active))

        // The attack trigger is on the stack (condition was true at declaration).
        // In response, destroy the token before the trigger resolves.
        driver.giveMana(active, Color.BLACK, 1)
        val breakSpell = driver.putCardInHand(active, "Test Break")
        driver.castSpellWithTargets(active, breakSpell, listOf(ChosenTarget.Permanent(token)))
        driver.bothPass() // destroy resolves — token gone
        driver.findPermanent(active, "Rabbit Token") shouldBe null

        driver.bothPass() // the Warrenguard trigger resolves

        // Per the ruling the pump still applies: 1/2 base + 2/+0.
        projector.project(driver.state).getPower(guard) shouldBe 3
    }

    test("attacking with no token doesn't trigger the pump at all") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40))
        var active = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        active = driver.activePlayer!!

        val guard = driver.putCreatureOnBattlefield(active, "Seasoned Warrenguard")
        driver.removeSummoningSickness(guard)

        driver.advanceToDeclareAttackersAs(active)
        driver.declareAttackers(active, listOf(guard), driver.getOpponent(active))
        driver.bothPass()

        projector.project(driver.state).getPower(guard) shouldBe 1
    }
})
