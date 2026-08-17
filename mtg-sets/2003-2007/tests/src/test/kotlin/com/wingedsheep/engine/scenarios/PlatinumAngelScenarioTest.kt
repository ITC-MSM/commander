package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
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
 * Platinum Angel (MRD #228) — {7} 4/4 Artifact Creature — Angel.
 *
 * "Flying
 *  You can't lose the game and your opponents can't win the game."
 *
 * Both halves are pinned, plus the thing neither static says out loud: the lock is anchored to the
 * permanent, so it ends the moment the Angel does. A card that only ever tested the "still alive at
 * -5" case would pass just as well if the grant had been made permanent.
 */
class PlatinumAngelScenarioTest : FunSpec({

    // Instants, so the non-active player can cast them while holding priority.
    val angelTestVictory = card("Angel Test Instant Victory") {
        manaCost = "{1}"
        typeLine = "Instant"
        oracleText = "You win the game."
        spell { effect = Effects.WinGame(EffectTarget.Controller) }
    }

    val angelTestLoseTen = card("Angel Test Lifeloss Ten") {
        manaCost = "{1}"
        typeLine = "Instant"
        oracleText = "You lose 10 life."
        spell { effect = Effects.LoseLife(10, EffectTarget.Controller) }
    }

    fun driver(startingLife: Int = 20): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(angelTestVictory, angelTestLoseTen))
        d.initMirrorMatch(
            deck = Deck.of("Plains" to 30),
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
        d.putCreatureOnBattlefield(p1, "Platinum Angel")

        d.castCheap(p1, "Angel Test Lifeloss Ten")

        d.getLifeTotal(p1) shouldBe -5
        d.state.gameOver.shouldBeFalse()
    }

    // The grant lives on the permanent, not on the player: once the Angel is destroyed the
    // life-total state-based action catches up on the very next check. Platinum Angel is an
    // *artifact* creature, so Shatter is legal removal for it.
    test("the loss lands as soon as the Angel leaves the battlefield") {
        val d = driver(startingLife = 5)
        val p1 = d.activePlayer!!
        val p2 = d.getOpponent(p1)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val angel = d.putCreatureOnBattlefield(p1, "Platinum Angel")

        d.castCheap(p1, "Angel Test Lifeloss Ten")
        d.getLifeTotal(p1) shouldBe -5
        d.state.gameOver.shouldBeFalse()

        val holder = d.state.priorityPlayerId
        if (holder != null && holder != p2) d.passPriority(holder)
        val shatter = d.putCardInHand(p2, "Shatter")
        d.giveMana(p2, Color.RED, 2)
        d.castSpellWithTargets(p2, shatter, listOf(ChosenTarget.Permanent(angel))).isSuccess shouldBe true
        d.settle()

        d.state.gameOver.shouldBeTrue()
        d.state.winnerId shouldBe p2
    }

    test("an opponent's 'you win the game' effect does nothing") {
        val d = driver()
        val p1 = d.activePlayer!!
        val p2 = d.getOpponent(p1)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        d.putCreatureOnBattlefield(p1, "Platinum Angel")

        d.castCheap(p2, "Angel Test Instant Victory")

        // Fizzled outright — nobody won, nobody lost, the game goes on.
        d.state.gameOver.shouldBeFalse()
        d.state.winnerId shouldBe null
    }

    test("without the Angel, the same win effect ends the game") {
        val d = driver()
        val p1 = d.activePlayer!!
        val p2 = d.getOpponent(p1)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.castCheap(p2, "Angel Test Instant Victory")

        d.state.gameOver.shouldBeTrue()
        d.state.winnerId shouldBe p2
    }
})
