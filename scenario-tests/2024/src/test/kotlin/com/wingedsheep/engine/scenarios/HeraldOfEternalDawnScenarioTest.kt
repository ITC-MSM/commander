package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fdn.cards.HeraldOfEternalDawn
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

/**
 * Herald of Eternal Dawn (FDN #17) — {4}{W}{W}{W} 6/6 Creature — Angel.
 *
 * "Flash, Flying
 *  You can't lose the game and your opponents can't win the game."
 *
 * Pins both halves of the lock, against a no-Angel control:
 *  - can't-lose: the controller survives at 0 or less life (that loss SBA is suppressed).
 *  - opponents can't win: an opponent's "you win the game" effect does nothing at all —
 *    it must not even collaterally end the game.
 */
class HeraldOfEternalDawnScenarioTest : FunSpec({

    // Instants so the non-active player can cast them while holding priority.
    val instantVictory = card("Test Instant Victory") {
        manaCost = "{1}"
        typeLine = "Instant"
        oracleText = "You win the game."
        spell { effect = Effects.WinGame(EffectTarget.Controller) }
    }

    val loseTen = card("Test Lifeloss Ten") {
        manaCost = "{1}"
        typeLine = "Instant"
        oracleText = "You lose 10 life."
        spell { effect = Effects.LoseLife(10, EffectTarget.Controller) }
    }

    fun driver(startingLife: Int = 20): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(HeraldOfEternalDawn, instantVictory, loseTen))
        d.initMirrorMatch(
            deck = Deck.of("Plains" to 30, "Grizzly Bears" to 10),
            skipMulligans = true,
            startingLife = startingLife,
        )
        return d
    }

    fun GameTestDriver.settle() {
        var guard = 0
        while (!state.gameOver && state.stack.isNotEmpty() && guard++ < 20) bothPass()
    }

    fun GameTestDriver.castCheap(player: EntityId, cardName: String) {
        // Hand priority to [player] if the other player is holding it.
        val holder = state.priorityPlayerId
        if (holder != null && holder != player) passPriority(holder)

        val spell = putCardInHand(player, cardName)
        giveColorlessMana(player, 1)
        castSpell(player, spell).isSuccess shouldBe true
        settle()
    }

    test("controller doesn't lose the game at 0 or less life") {
        val d = driver(startingLife = 5)
        val p1 = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        d.putCreatureOnBattlefield(p1, HeraldOfEternalDawn.name)

        d.castCheap(p1, "Test Lifeloss Ten")

        d.getLifeTotal(p1) shouldBe -5
        d.state.gameOver.shouldBeFalse()
    }

    test("an opponent's 'you win the game' effect does nothing") {
        val d = driver()
        val p1 = d.activePlayer!!
        val p2 = d.getOpponent(p1)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        d.putCreatureOnBattlefield(p1, HeraldOfEternalDawn.name)

        // p2 resolves an alternate win condition while p1's Angel is out.
        d.castCheap(p2, "Test Instant Victory")

        // Fizzled outright: nobody won, nobody lost, the game goes on.
        d.state.gameOver.shouldBeFalse()
        d.state.winnerId shouldBe null
    }

    test("without the Angel, the same win effect ends the game") {
        val d = driver()
        val p1 = d.activePlayer!!
        val p2 = d.getOpponent(p1)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.castCheap(p2, "Test Instant Victory")

        d.state.gameOver.shouldBeTrue()
        d.state.winnerId shouldBe p2
    }
})
