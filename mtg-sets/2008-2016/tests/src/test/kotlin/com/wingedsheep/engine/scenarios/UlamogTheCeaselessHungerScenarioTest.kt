package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.bfz.cards.Ulamog
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Ulamog, the Ceaseless Hunger (BFZ #15) — {10} Legendary Creature — Eldrazi, 10/10.
 *
 *   "When you cast this spell, exile two target permanents.
 *    Indestructible
 *    Whenever Ulamog attacks, defending player exiles the top twenty cards of their library."
 *
 * Two pieces of behaviour to pin down:
 *  - The cast trigger resolves *before* Ulamog itself (both targets exiled even though Ulamog is
 *    still on the stack underneath), matching the 2015-08-25 ruling.
 *  - The attack trigger reads the *defending* player, not the caster — exiling from the
 *    opponent's library, not Ulamog's controller's.
 */
class UlamogTheCeaselessHungerScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(Ulamog)
        return driver
    }

    test("cast trigger exiles two target permanents before Ulamog resolves") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val caster = driver.activePlayer!!
        val opponent = driver.getOpponent(caster)

        val ulamog = driver.putCardInHand(caster, "Ulamog, the Ceaseless Hunger")
        driver.giveMana(caster, Color.GREEN, 10)

        val mine = driver.putCreatureOnBattlefield(caster, "Savannah Lions")
        val theirs = driver.putCreatureOnBattlefield(opponent, "Centaur Courser")

        val cast = driver.submit(
            CastSpell(
                playerId = caster,
                cardId = ulamog,
                paymentStrategy = PaymentStrategy.FromPool
            )
        )
        (cast.error == null) shouldBe true

        // The cast trigger's own "target permanents" requirement is chosen when the triggered
        // ability is put on the stack (CR 603.3d) — a separate decision from casting Ulamog
        // itself, which has no target requirement of its own.
        driver.submitTargetSelection(caster, listOf(mine, theirs)).error shouldBe null

        // Top of stack: the cast trigger. Resolve it first — both targets should be exiled
        // while Ulamog itself is still on the stack underneath.
        driver.bothPass()
        driver.getExile(caster).contains(mine) shouldBe true
        driver.getExile(opponent).contains(theirs) shouldBe true
        driver.findPermanent(caster, "Ulamog, the Ceaseless Hunger") shouldBe null

        // Now Ulamog resolves and enters the battlefield.
        driver.bothPass()
        driver.findPermanent(caster, "Ulamog, the Ceaseless Hunger") shouldNotBe null
    }

    test("attack trigger makes the defending player exile the top twenty cards of their library") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)

        val attacker = driver.activePlayer!!
        val defender = driver.getOpponent(attacker)

        val ulamog = driver.putCreatureOnBattlefield(attacker, "Ulamog, the Ceaseless Hunger")
        driver.removeSummoningSickness(ulamog)

        val librarySizeBefore = driver.state.getLibrary(defender).size
        val attackerLibrarySizeBefore = driver.state.getLibrary(attacker).size

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attacker, listOf(ulamog), defender)

        // Resolve the "attacks" trigger.
        driver.bothPass()

        driver.state.getLibrary(defender).size shouldBe (librarySizeBefore - 20)
        driver.getExile(defender).size shouldBe 20
        // The attacking player's own library is untouched.
        driver.state.getLibrary(attacker).size shouldBe attackerLibrarySizeBefore
    }
})
