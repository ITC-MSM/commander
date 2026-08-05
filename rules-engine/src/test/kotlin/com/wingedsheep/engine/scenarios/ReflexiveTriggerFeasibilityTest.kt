package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.effects.ReflexiveTriggerEffect
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Engine-level coverage for `ReflexiveTriggerEffectExecutor.isActionFeasible` — the walker that
 * decides whether a "[action]. When you do, [reflexive]" ability's action can happen at all.
 *
 * An impossible action never happens, so CR 603.12's "when you do" never triggers. The bug this
 * guards against is the opposite of a missing prompt: a discard pipeline on an empty hand
 * auto-selects nothing and *reports success*, so without the walker the reflexive payoff fires for
 * a discard that never occurred (Inti, Seneschal of the Sun).
 *
 * The walker must be conservative in the other direction too — it only ever removes an impossible
 * prompt, never a possible one. These cards are defined inline because each pins one branch of that
 * conservatism, and no printed card currently exercises them:
 *
 *  - a **nested composite** (`draw, then discard`): `Effect.then` only flattens when its receiver is
 *    already a composite, so this is `Composite[Draw, Composite[Gather, Select, Move]]`. The gather
 *    sizes are read off pre-action state, so once a non-gather step is seen the bookkeeping must stop
 *    *and stay stopped inside the nested walk* — otherwise the inner discard is judged against the
 *    pre-draw hand and an empty hand wrongly suppresses the whole ability.
 *  - a **count larger than the hand**: `SelectFromCollectionExecutor` clamps a `ChooseExactly` count
 *    down to the collection size, so "discard two cards" with one card in hand discards that one and
 *    succeeds. Only an *empty* collection makes the action impossible.
 *
 * The mandatory (`optional = false`) case is here too: it shares the same gate, so a vacuous action
 * must not pay out even when there was never a yes/no question to skip.
 */
class ReflexiveTriggerFeasibilityTest : FunSpec({

    /** "Whenever you attack, you may discard a card. When you do, you gain 3 life." */
    val Prompter = card("Feasibility Test Prompter") {
        manaCost = "{1}"
        typeLine = "Creature — Human Soldier"
        power = 1
        toughness = 1
        oracleText = "Whenever you attack, you may discard a card. When you do, you gain 3 life."
        triggeredAbility {
            trigger = Triggers.YouAttack
            effect = ReflexiveTriggerEffect(
                action = Effects.Discard(1),
                optional = true,
                reflexiveEffect = Effects.GainLife(3)
            )
        }
    }

    /** "Whenever you attack, you may draw a card, then discard a card. When you do, you gain 3 life." */
    val DrawThenDiscard = card("Feasibility Test Draw Then Discard") {
        manaCost = "{1}"
        typeLine = "Creature — Human Soldier"
        power = 1
        toughness = 1
        oracleText = "Whenever you attack, you may draw a card, then discard a card. " +
            "When you do, you gain 3 life."
        triggeredAbility {
            trigger = Triggers.YouAttack
            effect = ReflexiveTriggerEffect(
                action = Effects.DrawCards(1).then(Patterns.Hand.discardCards(1)),
                optional = true,
                reflexiveEffect = Effects.GainLife(3)
            )
        }
    }

    /** "Whenever you attack, you may discard two cards. When you do, you gain 3 life." */
    val DiscardTwo = card("Feasibility Test Discard Two") {
        manaCost = "{1}"
        typeLine = "Creature — Human Soldier"
        power = 1
        toughness = 1
        oracleText = "Whenever you attack, you may discard two cards. When you do, you gain 3 life."
        triggeredAbility {
            trigger = Triggers.YouAttack
            effect = ReflexiveTriggerEffect(
                action = Effects.Discard(2),
                optional = true,
                reflexiveEffect = Effects.GainLife(3)
            )
        }
    }

    /** "Whenever you attack, discard a card. When you do, you gain 3 life." (mandatory) */
    val MandatoryDiscard = card("Feasibility Test Mandatory Discard") {
        manaCost = "{1}"
        typeLine = "Creature — Human Soldier"
        power = 1
        toughness = 1
        oracleText = "Whenever you attack, discard a card. When you do, you gain 3 life."
        triggeredAbility {
            trigger = Triggers.YouAttack
            effect = ReflexiveTriggerEffect(
                action = Effects.Discard(1),
                optional = false,
                reflexiveEffect = Effects.GainLife(3)
            )
        }
    }

    fun driver(): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all + listOf(Prompter, DrawThenDiscard, DiscardTwo, MandatoryDiscard))
        initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
    }

    fun GameTestDriver.emptyHand(playerId: EntityId) {
        val handZone = ZoneKey(playerId, Zone.HAND)
        var emptied = state
        getHand(playerId).toList().forEach { card -> emptied = emptied.removeFromZone(handZone, card) }
        replaceState(emptied)
    }

    /** Drain the attack triggers, answering any "may" yes/no with [accept]. Returns whether one was asked. */
    fun GameTestDriver.resolveAttackTriggers(you: EntityId, accept: Boolean): Boolean {
        var asked = false
        var guard = 0
        while (guard++ < 60) {
            when (val dec = pendingDecision) {
                is YesNoDecision -> { asked = true; submitYesNo(you, accept) }
                is SelectCardsDecision ->
                    submitCardSelection(you, dec.options.take(dec.minSelections.coerceAtLeast(1)))
                else -> if (state.stack.isNotEmpty()) bothPass() else return asked
            }
        }
        return asked
    }

    /** Attack with a freshly-made [cardName], after [setUpHand] has arranged the hand. */
    fun attackWith(cardName: String, setUpHand: GameTestDriver.(EntityId) -> Unit): GameTestDriver {
        val d = driver()
        val you = d.activePlayer!!
        val attacker = d.putCreatureOnBattlefield(you, cardName)
        d.removeSummoningSickness(attacker)
        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.setUpHand(you)
        d.declareAttackers(you, listOf(attacker), d.getOpponent(you))
        return d
    }

    test("an empty hand is never asked to discard, and never pays out") {
        val d = attackWith("Feasibility Test Prompter") { emptyHand(it) }
        val you = d.activePlayer!!

        d.resolveAttackTriggers(you, accept = true) shouldBe false
        d.getLifeTotal(you) shouldBe 20
    }

    test("a mandatory discard on an empty hand pays out nothing either") {
        val d = attackWith("Feasibility Test Mandatory Discard") { emptyHand(it) }
        val you = d.activePlayer!!

        // No yes/no to skip here — the gate has to stop the vacuous discard from reporting success.
        d.resolveAttackTriggers(you, accept = true) shouldBe false
        d.getLifeTotal(you) shouldBe 20
        d.getGraveyard(you).isEmpty() shouldBe true
    }

    test("draw-then-discard is still offered on an empty hand — the draw supplies the card") {
        val d = attackWith("Feasibility Test Draw Then Discard") { emptyHand(it) }
        val you = d.activePlayer!!

        // The gather is scored against the pre-action (empty) hand, so the bookkeeping must have
        // stopped at the Draw step — including inside the nested composite the discard pipeline is.
        d.resolveAttackTriggers(you, accept = true) shouldBe true
        d.getLifeTotal(you) shouldBe 23
        d.getHandSize(you) shouldBe 0
        d.getGraveyard(you).size shouldBe 1
    }

    test("discard two with only one card in hand discards that one and pays out") {
        val d = attackWith("Feasibility Test Discard Two") { you ->
            emptyHand(you)
            putCardInHand(you, "Mountain")
        }
        val you = d.activePlayer!!

        // The executor clamps ChooseExactly(2) to the single eligible card, so the action succeeds —
        // feasibility must not demand the full count.
        d.resolveAttackTriggers(you, accept = true) shouldBe true
        d.getLifeTotal(you) shouldBe 23
        d.getHandSize(you) shouldBe 0
        d.getGraveyard(you).size shouldBe 1
    }
})
