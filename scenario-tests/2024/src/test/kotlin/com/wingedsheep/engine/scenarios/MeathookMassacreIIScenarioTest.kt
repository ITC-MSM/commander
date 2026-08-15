package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Meathook Massacre II (DSK) — the death-trigger pay/suffer-or-steal flow (DSK engine gap #16).
 *
 * Oracle:
 *  1. When Meathook Massacre II enters, each player sacrifices X creatures of their choice.
 *  2. Whenever a creature you control dies, you may pay 3 life. If you do, return that card under
 *     your control with a finality counter on it.
 *  3. Whenever a creature an opponent controls dies, they may pay 3 life. If they don't, return that
 *     card under your control with a finality counter on it.
 *
 * The headline coverage is the third ability: the pay/suffer decision and the life payment are
 * routed to the *dying creature's controller* (the opponent, via `Player.TriggeringPlayer` now that
 * a dies trigger populates `triggeringPlayerId` from `ZoneChangeEvent.lastKnownController`), while
 * the consequence — returning the card under *your* control — runs under the ability's controller
 * (not the payer). Deaths are driven through the real resolution pipeline (Doom Blade destroying a
 * creature) so the dies-trigger detection actually fires.
 */
class MeathookMassacreIIScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(
            TestCards.all + com.wingedsheep.mtg.sets.tokens.PredefinedTokens.allTokens
        )
        return d
    }

    fun GameTestDriver.creatureCount(playerId: EntityId, name: String): Int =
        state.getBattlefield().count { id ->
            state.getEntity(id)?.get<CardComponent>()?.name == name &&
                state.projectedState.getController(id) == playerId
        }

    fun GameTestDriver.finalityCounters(id: EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.FINALITY) ?: 0

    /** Pass priority until a pending decision appears (the resolving trigger pauses there). */
    fun GameTestDriver.passUntilDecision(maxPasses: Int = 12) {
        repeat(maxPasses) { if (pendingDecision == null) bothPass() }
    }

    // ---------------------------------------------------------------------------------------------
    // Ability 1 — ETB: each player sacrifices X creatures of their choice.
    // ---------------------------------------------------------------------------------------------

    test("ETB: each player sacrifices X creatures of their choice (X = cast value)") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true)
        val active = d.activePlayer!!
        val opp = d.getOpponent(active)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Each player controls two TOKEN creatures; X = 1 means each chooses ONE to sacrifice.
        // Tokens keep the test focused on the sacrifice clause: when they die to the sacrifice,
        // Meathook's own "a creature dies" abilities trigger, but a token has no card to return,
        // so the reanimation is a no-op and the surviving counts stay clean (per the printed ruling).
        val tokens = listOf(active, active, opp, opp).map { d.putCreatureOnBattlefield(it, "Grizzly Bears") }
        tokens.forEach { id ->
            d.replaceState(d.state.updateEntity(id) {
                it.with(com.wingedsheep.engine.state.components.identity.TokenComponent)
            })
        }

        val meathook = d.putCardInHand(active, "Meathook Massacre II")
        // {X}{X}{B}{B}{B}{B} with X = 1 → six mana, four of them black; black covers the generic too.
        d.giveMana(active, Color.BLACK, 6)
        d.castXSpell(active, meathook, xValue = 1).isSuccess shouldBe true

        // Resolve the spell + ETB trigger, answering each player's "sacrifice 1" selection and
        // declining the follow-on "may pay 3 life" prompts from the death of each sacrificed token.
        repeat(30) {
            when (val dec = d.pendingDecision) {
                is SelectCardsDecision -> d.submitCardSelection(dec.playerId, dec.options.take(dec.minSelections))
                is YesNoDecision -> d.submitYesNo(dec.playerId, false)
                null -> d.bothPass()
                else -> error("Unexpected decision: $dec")
            }
        }

        withClue("Each player sacrificed exactly one of their two creatures") {
            d.creatureCount(active, "Grizzly Bears") shouldBe 1
            d.creatureCount(opp, "Grizzly Bears") shouldBe 1
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Ability 2 — your creature dies: you MAY pay 3 life; if you DO, reanimate under your control.
    // ---------------------------------------------------------------------------------------------

    test("your creature dies: pay 3 life → return it under your control with a finality counter") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true)
        val active = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.putPermanentOnBattlefield(active, "Meathook Massacre II")
        val bear = d.putCreatureOnBattlefield(active, "Grizzly Bears")

        val lifeBefore = d.getLifeTotal(active)

        // Kill your own creature with Doom Blade (a real death drives the dies trigger).
        val doomBlade = d.putCardInHand(active, "Doom Blade")
        d.giveMana(active, Color.BLACK, 2)
        d.castSpell(active, doomBlade, listOf(bear)).isSuccess shouldBe true
        d.passUntilDecision()

        withClue("The 'creature you control dies' trigger asks YOU whether to pay") {
            (d.pendingDecision as? YesNoDecision)?.playerId shouldBe active
        }

        d.submitYesNo(active, true)
        d.passUntilDecision()

        withClue("You paid 3 life") { d.getLifeTotal(active) shouldBe lifeBefore - 3 }
        withClue("The card is back under your control") { d.getController(bear) shouldBe active }
        withClue("It returned with a finality counter") { d.finalityCounters(bear) shouldBe 1 }
    }

    test("your creature dies: decline to pay → the card is not returned") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true)
        val active = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.putPermanentOnBattlefield(active, "Meathook Massacre II")
        val bear = d.putCreatureOnBattlefield(active, "Grizzly Bears")

        val lifeBefore = d.getLifeTotal(active)

        val doomBlade = d.putCardInHand(active, "Doom Blade")
        d.giveMana(active, Color.BLACK, 2)
        d.castSpell(active, doomBlade, listOf(bear)).isSuccess shouldBe true
        d.passUntilDecision()

        d.submitYesNo(active, false)
        d.passUntilDecision()

        withClue("Declining costs no life") { d.getLifeTotal(active) shouldBe lifeBefore }
        withClue("The creature stays dead — not on the battlefield") {
            d.getController(bear) shouldBe null
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Ability 3 — an opponent's creature dies: THEY may pay 3 life; if they DON'T, YOU steal it.
    // This is the engine gap: decision + life routed to the opponent, theft under your control.
    // ---------------------------------------------------------------------------------------------

    test("opponent's creature dies + they decline: YOU steal the card under your control with a finality counter") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true)
        val active = d.activePlayer!!
        val opp = d.getOpponent(active)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.putPermanentOnBattlefield(active, "Meathook Massacre II")
        val bear = d.putCreatureOnBattlefield(opp, "Grizzly Bears")

        val yourLifeBefore = d.getLifeTotal(active)
        val oppLifeBefore = d.getLifeTotal(opp)

        val doomBlade = d.putCardInHand(active, "Doom Blade")
        d.giveMana(active, Color.BLACK, 2)
        d.castSpell(active, doomBlade, listOf(bear)).isSuccess shouldBe true
        d.passUntilDecision()

        withClue("The decision is routed to the OPPONENT (the dying creature's controller), not you") {
            (d.pendingDecision as? YesNoDecision)?.playerId shouldBe opp
        }

        // They decline to pay → you steal the card.
        d.submitYesNo(opp, false)
        d.passUntilDecision()

        withClue("Neither player loses life when the opponent declines") {
            d.getLifeTotal(opp) shouldBe oppLifeBefore
            d.getLifeTotal(active) shouldBe yourLifeBefore
        }
        withClue("The opponent's card returns under YOUR control (theft), even though they own it") {
            d.getController(bear) shouldBe active
        }
        withClue("It returns with a finality counter") { d.finalityCounters(bear) shouldBe 1 }
    }

    test("opponent's creature dies + they pay 3 life: the OPPONENT loses the life and keeps their card") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true)
        val active = d.activePlayer!!
        val opp = d.getOpponent(active)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.putPermanentOnBattlefield(active, "Meathook Massacre II")
        val bear = d.putCreatureOnBattlefield(opp, "Grizzly Bears")

        val yourLifeBefore = d.getLifeTotal(active)
        val oppLifeBefore = d.getLifeTotal(opp)

        val doomBlade = d.putCardInHand(active, "Doom Blade")
        d.giveMana(active, Color.BLACK, 2)
        d.castSpell(active, doomBlade, listOf(bear)).isSuccess shouldBe true
        d.passUntilDecision()

        // They pay 3 life to keep the card out of your hands.
        d.submitYesNo(opp, true)
        d.passUntilDecision()

        withClue("The 3 life is charged to the OPPONENT, not to you (the ability's controller)") {
            d.getLifeTotal(opp) shouldBe oppLifeBefore - 3
            d.getLifeTotal(active) shouldBe yourLifeBefore
        }
        withClue("They paid, so you do NOT get the card") {
            d.getController(bear) shouldBe null
        }
    }

    test("opponent can't afford 3 life: you steal the card automatically, charging them nothing") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true)
        val active = d.activePlayer!!
        val opp = d.getOpponent(active)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.putPermanentOnBattlefield(active, "Meathook Massacre II")
        val bear = d.putCreatureOnBattlefield(opp, "Grizzly Bears")

        // Opponent at 2 life cannot pay 3 — the suffer (theft) resolves automatically.
        d.setLifeTotal(opp, 2)

        val doomBlade = d.putCardInHand(active, "Doom Blade")
        d.giveMana(active, Color.BLACK, 2)
        d.castSpell(active, doomBlade, listOf(bear)).isSuccess shouldBe true

        // No pay-or-suffer decision is offered when the opponent can't afford it.
        repeat(8) { if (d.pendingDecision == null) d.bothPass() }

        withClue("They couldn't pay, so they keep their 2 life") { d.getLifeTotal(opp) shouldBe 2 }
        withClue("You steal the card under your control") { d.getController(bear) shouldBe active }
        withClue("It returns with a finality counter") { d.finalityCounters(bear) shouldBe 1 }
    }
})
