package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.scg.cards.Stifle
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.TimingRule
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Legal-action regression for the Stifle UI path.
 *
 * The engine represents activated and triggered abilities as stack entities, not
 * spell cards.  The response player's cast offer therefore has to include that
 * entity as a valid target before the web client can show "Cast Stifle".
 */
class StifleLegalActionTest : FunSpec({
    val testAbilitySource = card("Stifle Test Ability Source") {
        manaCost = "{1}"
        typeLine = "Artifact"
        activatedAbility {
            cost = AbilityCost.Tap
            effect = Effects.DrawCards(1)
            timing = TimingRule.InstantSpeed
        }
    }

    test("Stifle is offered to an opponent while an activated ability is on the stack") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(Stifle, testAbilitySource))
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val activator = driver.activePlayer!!
        val responder = driver.getOpponent(activator)
        val source = driver.putPermanentOnBattlefield(activator, "Stifle Test Ability Source")
        driver.removeSummoningSickness(source)
        val stifle = driver.putCardInHand(responder, "Stifle")
        driver.putLandOnBattlefield(responder, "Island")

        val abilityId = driver.cardRegistry.requireCard("Stifle Test Ability Source").activatedAbilities.single().id
        driver.submitSuccess(ActivateAbility(activator, source, abilityId))
        val activatedAbilityOnStack = driver.getTopOfStack()!!

        // The activating player passes; the opponent now has the response window.
        driver.passPriority(activator).isSuccess shouldBe true

        val stifleAction = driver.legalActions(responder)
            .singleOrNull { (it.action as? CastSpell)?.cardId == stifle }

        stifleAction?.validTargets?.contains(activatedAbilityOnStack) shouldBe true
    }
})
