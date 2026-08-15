package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.tmt.cards.RatKingVerminister
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Rat King, Verminister (TMT) — the same-named graveyard reanimate
 * ({T}, Sacrifice three Rats: return target creature card + all same-named cards from your
 * graveyard to the battlefield tapped), proving ReturnSameNamedFromGraveyardEffect.
 */
class RatKingScenarioTest : FunSpec({

    val testRat = card("Test Rat") {
        manaCost = "{B}"; typeLine = "Creature — Rat"; power = 1; toughness = 1
    }
    val testGrizzly = card("Test Grizzly") {
        manaCost = "{1}{G}"; typeLine = "Creature — Bear"; power = 2; toughness = 2
    }
    val testSnake = card("Test Snake") {
        manaCost = "{1}{G}"; typeLine = "Creature — Snake"; power = 1; toughness = 1
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(RatKingVerminister, testRat, testGrizzly, testSnake))
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    test("returns the target and all same-named cards from the graveyard tapped, leaving others") {
        val d = createDriver()
        val me = d.player1

        val ratKing = d.putCreatureOnBattlefield(me, "Rat King, Verminister")
        d.removeSummoningSickness(ratKing)
        val rat1 = d.putCreatureOnBattlefield(me, "Test Rat")
        val rat2 = d.putCreatureOnBattlefield(me, "Test Rat")
        val rat3 = d.putCreatureOnBattlefield(me, "Test Rat")

        val grizzly1 = d.putCardInGraveyard(me, "Test Grizzly")
        val grizzly2 = d.putCardInGraveyard(me, "Test Grizzly") // second same-named copy
        val snake = d.putCardInGraveyard(me, "Test Snake") // different name — should stay

        d.submit(
            ActivateAbility(
                playerId = me,
                sourceId = ratKing,
                abilityId = RatKingVerminister.activatedAbilities.first().id,
                targets = listOf(ChosenTarget.Card(grizzly1, me, Zone.GRAVEYARD)),
                costPayment = AdditionalCostPayment(sacrificedPermanents = listOf(rat1, rat2, rat3))
            )
        ).error shouldBe null
        d.bothPass() // ability on stack -> resolve

        // Both Test Grizzly returned to the battlefield, tapped; the Snake stayed in the graveyard.
        d.state.getBattlefield().contains(grizzly1) shouldBe true
        d.state.getBattlefield().contains(grizzly2) shouldBe true
        d.state.getEntity(grizzly1)?.has<TappedComponent>() shouldBe true
        d.state.getEntity(grizzly2)?.has<TappedComponent>() shouldBe true
        d.state.getGraveyard(me).contains(snake) shouldBe true
    }
})
