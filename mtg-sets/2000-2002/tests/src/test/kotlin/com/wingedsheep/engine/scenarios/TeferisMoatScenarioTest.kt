package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseColorDecision
import com.wingedsheep.engine.core.ColorChosenResponse
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.inv.cards.TeferisMoat
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Teferi's Moat [INV 279] — {3}{W}{U} Enchantment
 *
 * "As this enchantment enters, choose a color. Creatures of the chosen color without flying can't
 * attack you."
 *
 * The card is a [com.wingedsheep.sdk.scripting.CantBeAttackedBy] over a *two-predicate* filter —
 * `sharingChosenColorWithSource()` **and** `withoutKeyword(FLYING)` — so the test that matters is
 * the conjunction: each half alone must not be enough to stop an attacker. A wrong-colour ground
 * creature and a right-colour flier both have to get through, and only the creature failing both
 * halves is rejected.
 *
 * The colour is chosen for real (the `EntersWithChoice(COLOR)` replacement raises a
 * [ChooseColorDecision] as the enchantment enters), not stamped onto the component, so the
 * chosen-colour predicate is exercised end to end.
 *
 * Every attack test keeps an unrestricted attacker on the attacking side: with no legal attack at
 * all the engine skips the declare-attackers step, and "expect a rejection" would then pass for the
 * wrong reason.
 */
class TeferisMoatScenarioTest : FunSpec({

    /** A defender-side planeswalker, so "can't attack **you**" can be told apart from "can't attack". */
    val testWalker = card("Test Moat Walker") {
        manaCost = "{2}"
        typeLine = "Legendary Planeswalker — Tester"
        startingLoyalty = 3
        loyaltyAbility(1) {
            effect = Effects.GainLife(1)
        }
    }

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(TeferisMoat, testWalker))
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    /** Cast Teferi's Moat for [player] and answer its "choose a color" with [color]. */
    fun GameTestDriver.resolveMoat(player: EntityId, color: Color): EntityId {
        val card = putCardInHand(player, "Teferi's Moat")
        giveMana(player, Color.WHITE, 1)
        giveMana(player, Color.BLUE, 1)
        giveColorlessMana(player, 3)
        castSpell(player, card).error shouldBe null
        bothPass()

        val decision = pendingDecision
        decision.shouldBeInstanceOf<ChooseColorDecision>()
        submitDecision(player, ColorChosenResponse(decision.id, color))

        return findPermanent(player, "Teferi's Moat") ?: error("Teferi's Moat did not resolve")
    }

    /** Reach [player]'s own declare-attackers step, and prove we are there. */
    fun GameTestDriver.attackStepOf(player: EntityId) {
        passPriorityUntil(Step.DECLARE_ATTACKERS)
        if (activePlayer != player) {
            passPriorityUntil(Step.POSTCOMBAT_MAIN)
            passPriorityUntil(Step.DECLARE_ATTACKERS)
        }
        currentStep shouldBe Step.DECLARE_ATTACKERS
        activePlayer shouldBe player
    }

    fun GameTestDriver.readyCreature(player: EntityId, name: String): EntityId {
        val id = putCreatureOnBattlefield(player, name)
        removeSummoningSickness(id)
        return id
    }

    test("a non-flying creature of the chosen color can't attack the Moat's controller") {
        val d = driver()
        val moatPlayer = d.activePlayer!!
        val attacker = d.getOpponent(moatPlayer)

        d.resolveMoat(moatPlayer, Color.GREEN)
        val greenGround = d.readyCreature(attacker, "Grizzly Bears")   // green, no flying
        val decoy = d.readyCreature(attacker, "Savannah Lions")        // white, no flying

        d.attackStepOf(attacker)
        withClue("Creatures of the chosen color without flying can't attack you") {
            d.declareAttackers(attacker, listOf(greenGround), moatPlayer).error shouldNotBe null
        }
        withClue("one restricted attacker makes the whole declaration illegal") {
            d.declareAttackers(attacker, listOf(greenGround, decoy), moatPlayer).error shouldNotBe null
        }
    }

    test("a flying creature of the chosen color attacks freely") {
        val d = driver()
        val moatPlayer = d.activePlayer!!
        val attacker = d.getOpponent(moatPlayer)

        d.resolveMoat(moatPlayer, Color.GREEN)
        val greenFlier = d.readyCreature(attacker, "Birds of Paradise")  // green, flying
        d.readyCreature(attacker, "Savannah Lions")

        d.attackStepOf(attacker)
        withClue("the flying half of the conjunction exempts it") {
            d.declareAttackers(attacker, listOf(greenFlier), moatPlayer).error shouldBe null
        }
    }

    test("a non-flying creature of a different color attacks freely") {
        val d = driver()
        val moatPlayer = d.activePlayer!!
        val attacker = d.getOpponent(moatPlayer)

        d.resolveMoat(moatPlayer, Color.GREEN)
        val whiteGround = d.readyCreature(attacker, "Savannah Lions")   // white, no flying

        d.attackStepOf(attacker)
        withClue("the chosen-color half of the conjunction exempts it") {
            d.declareAttackers(attacker, listOf(whiteGround), moatPlayer).error shouldBe null
        }
    }

    test("a restricted creature may still attack a planeswalker the Moat's controller controls") {
        val d = driver()
        val moatPlayer = d.activePlayer!!
        val attacker = d.getOpponent(moatPlayer)

        d.resolveMoat(moatPlayer, Color.GREEN)
        val walker = d.putPermanentOnBattlefield(moatPlayer, "Test Moat Walker")
        d.replaceState(
            d.state.updateEntity(walker) { c ->
                c.with((c.get<CountersComponent>() ?: CountersComponent()).withAdded(CounterType.LOYALTY, 3))
            }
        )
        val greenGround = d.readyCreature(attacker, "Grizzly Bears")
        d.readyCreature(attacker, "Savannah Lions")

        d.attackStepOf(attacker)
        withClue("the restriction still holds against the player") {
            d.declareAttackers(attacker, listOf(greenGround), moatPlayer).error shouldNotBe null
        }
        withClue("\"can't attack you\" is the player, not their planeswalkers") {
            d.declareAttackers(attacker, mapOf(greenGround to walker)).error shouldBe null
        }
    }
})
