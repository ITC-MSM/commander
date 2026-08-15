package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.tdm.cards.SageOfTheSkies
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Sage of the Skies' "when you cast this spell" cast trigger.
 *
 * Sage of the Skies: {2}{W}
 * Creature — Human Monk · 2/3 · Flying, lifelink
 * "When you cast this spell, if you've cast another spell this turn, copy this spell.
 *  (The copy becomes a token.)"
 *
 * Rules exercised:
 *  - CR 603.2: the cast trigger fires from the stack on Sage's own cast.
 *  - CR 603.4: the intervening "if" ("you've cast another spell this turn") gates the trigger
 *    at detection time — Sage cast as the first spell of the turn does nothing.
 *  - CR 707.10f: copying a permanent spell yields a token permanent.
 *  - CR 707.10: a copy is put on the stack, not cast, so it does not re-trigger (no copy-of-copy).
 */
class SageOfTheSkiesTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(SageOfTheSkies))
        return driver
    }

    /** Pass priority back and forth until the stack is empty (one object resolves per round). */
    fun GameTestDriver.resolveStack(p1: com.wingedsheep.sdk.model.EntityId, p2: com.wingedsheep.sdk.model.EntityId) {
        var guard = 0
        while (stackSize > 0 && pendingDecision == null && guard++ < 25) {
            passPriority(p1)
            passPriority(p2)
        }
    }

    fun GameTestDriver.sages(playerId: com.wingedsheep.sdk.model.EntityId) =
        getCreatures(playerId).filter { getCardName(it) == "Sage of the Skies" }

    fun GameTestDriver.castSage(playerId: com.wingedsheep.sdk.model.EntityId) {
        val sage = putCardInHand(playerId, "Sage of the Skies")
        giveMana(playerId, Color.WHITE, 1)
        giveColorlessMana(playerId, 2)
        castSpell(playerId, sage, emptyList())
    }

    test("copies itself when cast as your second spell this turn (token)") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 20, "Lightning Bolt" to 20), startingLife = 20)
        val player1 = driver.activePlayer!!
        val player2 = driver.getOpponent(player1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // First spell of the turn.
        val bolt = driver.putCardInHand(player1, "Lightning Bolt")
        driver.giveMana(player1, Color.RED, 1)
        driver.castSpell(player1, bolt, listOf(player2))
        driver.resolveStack(player1, player2)

        // Sage is the second spell: its cast trigger fires and copies it.
        driver.castSage(player1)
        // Stack: [cast trigger, Sage].
        driver.stackSize shouldBe 2
        driver.resolveStack(player1, player2)

        // Two Sages on the battlefield: the real one plus exactly one token copy (CR 707.10f).
        val sages = driver.sages(player1)
        sages.size shouldBe 2
        sages.count { driver.state.getEntity(it)?.has<TokenComponent>() == true } shouldBe 1
    }

    test("does not copy when cast as your first spell this turn") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 20), startingLife = 20)
        val player1 = driver.activePlayer!!
        val player2 = driver.getOpponent(player1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Sage is the only/first spell: intervening "if" is false, no trigger goes on the stack.
        driver.castSage(player1)
        driver.stackSize shouldBe 1 // just Sage, no cast trigger
        driver.resolveStack(player1, player2)

        val sages = driver.sages(player1)
        sages.size shouldBe 1
        sages.count { driver.state.getEntity(it)?.has<TokenComponent>() == true } shouldBe 0
    }

    test("copies when cast as a later spell — 'another spell', not 'exactly the second'") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 20, "Lightning Bolt" to 20), startingLife = 20)
        val player1 = driver.activePlayer!!
        val player2 = driver.getOpponent(player1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Cast two prior spells so Sage is the third spell of the turn.
        repeat(2) {
            val bolt = driver.putCardInHand(player1, "Lightning Bolt")
            driver.giveMana(player1, Color.RED, 1)
            driver.castSpell(player1, bolt, listOf(player2))
            driver.resolveStack(player1, player2)
        }

        driver.castSage(player1)
        driver.stackSize shouldBe 2 // cast trigger still fires on the third spell
        driver.resolveStack(player1, player2)

        driver.sages(player1).size shouldBe 2
    }

    test("the token copy does not itself re-trigger (no copy of a copy)") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 20, "Lightning Bolt" to 20), startingLife = 20)
        val player1 = driver.activePlayer!!
        val player2 = driver.getOpponent(player1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val bolt = driver.putCardInHand(player1, "Lightning Bolt")
        driver.giveMana(player1, Color.RED, 1)
        driver.castSpell(player1, bolt, listOf(player2))
        driver.resolveStack(player1, player2)

        driver.castSage(player1)
        driver.resolveStack(player1, player2)

        // Exactly one copy was made. A copy is put on the stack, not cast (CR 707.10), so it does
        // not fire "when you cast this spell" again — there is no runaway copy chain.
        driver.sages(player1).size shouldBe 2
        driver.stackSize shouldBe 0
    }
})
