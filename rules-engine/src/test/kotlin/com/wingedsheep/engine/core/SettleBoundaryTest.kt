package com.wingedsheep.engine.core

import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.ons.cards.WordsOfWind
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.effects.MayEffect
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * The settle boundary ([Settler]) at the three places where work used to be lost after a pause.
 * The boundary runs the same way whether an action finished at once or finished with the answer to
 * a question, so each test here takes the path through a question.
 */
class SettleBoundaryTest : FunSpec({

    val drawStepWatcher = card("Draw Step Watcher") {
        manaCost = "{1}"
        typeLine = "Enchantment"
        triggeredAbility {
            trigger = Triggers.YourDrawStep
            effect = Effects.GainLife(1)
        }
    }

    val flightUntilYourNextTurn = card("Flight Until Your Next Turn") {
        manaCost = "{0}"
        typeLine = "Instant"
        spell {
            val creature = target("target creature", Targets.Creature)
            effect = Effects.GrantKeyword(Keyword.FLYING, creature, Duration.UntilYourNextTurn)
        }
    }

    val askThenEndTheTurn = card("Ask Then End the Turn") {
        manaCost = "{0}"
        typeLine = "Sorcery"
        spell {
            effect = Effects.Composite(MayEffect(Effects.GainLife(2)), Effects.EndTheTurn)
        }
    }

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(drawStepWatcher, flightUntilYourNextTurn, askThenEndTheTurn))
        d.initMirrorMatch(deck = Deck.of("Grizzly Bears" to 40), startingLife = 20)
        return d
    }

    test("an untap-step choice still ends 'until your next turn' effects once it is answered") {
        val d = driver()
        val me = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val weaponry = d.putPermanentOnBattlefield(me, "Tawnos's Weaponry")
        d.tapPermanent(weaponry)
        val bear = d.putCreatureOnBattlefield(me, "Grizzly Bears")
        d.castSpell(me, d.putCardInHand(me, "Flight Until Your Next Turn"), targets = listOf(bear))
        d.bothPass()
        d.state.projectedState.hasKeyword(bear, Keyword.FLYING) shouldBe true

        // My next untap step asks whether to untap the Weaponry. Answering must finish the untap
        // step exactly as the unpaused path does, which includes ending the flight.
        d.passPriorityUntil(Step.UNTAP)
        d.activePlayer shouldBe me
        d.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        d.submitCardSelection(me, emptyList())

        d.state.projectedState.hasKeyword(bear, Keyword.FLYING) shouldBe false
        d.state.step shouldBe Step.UPKEEP
        d.state.priorityPlayerId shouldBe me
    }

    test("a draw-step trigger still goes on the stack when the draw stops for a choice") {
        val d = driver()
        val first = d.activePlayer!!
        val me = d.getOpponent(first)
        d.putPermanentOnBattlefield(me, "Draw Step Watcher")
        d.putPermanentOnBattlefield(me, "Words of Wind")
        repeat(2) {
            d.putCreatureOnBattlefield(me, "Grizzly Bears")
            d.putCreatureOnBattlefield(first, "Grizzly Bears")
        }

        // In my upkeep, set up "the next time you would draw a card this turn, each player returns
        // a permanent they control instead". The draw step's draw then stops for those choices.
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        d.passPriorityUntil(Step.UPKEEP)
        d.activePlayer shouldBe me
        d.giveColorlessMana(me, 1)
        d.submitSuccess(ActivateAbility(me, d.findPermanent(me, "Words of Wind")!!, WordsOfWind.activatedAbilities.first().id))
        d.bothPass()

        d.passPriorityUntil(Step.DRAW)
        d.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        while (d.pendingDecision is SelectCardsDecision) {
            val decision = d.pendingDecision as SelectCardsDecision
            val bear = decision.options.first { d.state.getEntity(it)?.get<CardComponent>()?.name == "Grizzly Bears" }
            d.submitCardSelection(decision.playerId, listOf(bear))
        }

        // "At the beginning of your draw step" triggered as the step began; the bounce choices
        // didn't swallow it.
        d.stackSize shouldBe 1
        d.bothPass()
        d.getLifeTotal(me) shouldBe 21
    }

    test("an end-the-turn effect that asks a question first still ends the turn") {
        val d = driver()
        val me = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.castSpell(me, d.putCardInHand(me, "Ask Then End the Turn"))
        d.bothPass()
        d.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        d.submitYesNo(me, true)

        // CR 724.1: the resolution that requested it is over, answered or not, so the turn ends.
        d.getLifeTotal(me) shouldBe 22
        d.activePlayer shouldBe d.getOpponent(me)
        d.getExileCardNames(me).contains("Ask Then End the Turn") shouldBe true
    }
})
