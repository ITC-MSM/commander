package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.blb.cards.CarrotCake
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Carrot Cake (BLB): "When this artifact enters AND WHEN YOU SACRIFICE IT, create a
 * 1/1 white Rabbit creature token and scry 1."
 *
 * Regression guard: the second trigger is sacrifice-only (Triggers.Sacrificed). It used
 * to be modeled as Triggers.Dies, which wrongly fired on any battlefield→graveyard
 * transition — an opponent destroying the Food handed its controller a free Rabbit + scry.
 */
class CarrotCakeSacrificeTriggerTest : FunSpec({

    val testShatter: CardDefinition = card("Test Shatter") {
        manaCost = "{R}"
        typeLine = "Instant"
        spell {
            val artifact = target("target artifact", Targets.Artifact)
            effect = Effects.Destroy(artifact)
        }
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(CarrotCake, testShatter))
        return driver
    }

    test("sacrificing Carrot Cake to its own ability creates a Rabbit and scrys 1") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40))
        val active = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val cake = driver.putPermanentOnBattlefield(active, "Carrot Cake")
        driver.giveColorlessMana(active, 2)

        val gainLifeAbility = CarrotCake.activatedAbilities.first().id
        driver.submitSuccess(ActivateAbility(playerId = active, sourceId = cake, abilityId = gainLifeAbility))

        val lifeBefore = driver.getLifeTotal(active)

        // Sacrificing as a cost puts the "when you sacrifice it" trigger on the stack above
        // the gain-3-life ability. Resolve both; the trigger's scry 1 pauses for a selection.
        var sawScry = false
        repeat(8) {
            val decision = driver.pendingDecision
            if (decision != null) {
                // Scry 1 pauses twice: the SelectCardsDecision (keep/bottom) and the
                // follow-up ReorderLibraryDecision. Auto-resolve both.
                if (decision is SelectCardsDecision && decision.playerId == active) sawScry = true
                driver.autoResolveDecision()
            } else if (driver.state.priorityPlayerId != null) {
                driver.bothPass()
            }
        }

        sawScry shouldBe true
        driver.findPermanent(active, "Rabbit Token") shouldNotBe null
        driver.getLifeTotal(active) shouldBe lifeBefore + 3
        driver.state.getZone(active, Zone.GRAVEYARD).contains(cake) shouldBe true
    }

    test("an opponent destroying Carrot Cake does NOT create a Rabbit or scry") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40))
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // The opponent controls Carrot Cake; the active player destroys it.
        val cake = driver.putPermanentOnBattlefield(opponent, "Carrot Cake")

        driver.giveMana(active, com.wingedsheep.sdk.core.Color.RED, 1)
        val shatter = driver.putCardInHand(active, "Test Shatter")
        driver.castSpellWithTargets(active, shatter, listOf(ChosenTarget.Permanent(cake)))

        repeat(4) { if (driver.state.priorityPlayerId != null) driver.bothPass() }

        // Carrot Cake was destroyed (died, not sacrificed) — no Rabbit, no scry decision.
        driver.state.getZone(opponent, Zone.GRAVEYARD).contains(cake) shouldBe true
        driver.findPermanent(opponent, "Rabbit Token") shouldBe null
        driver.pendingDecision shouldBe null
    }
})
