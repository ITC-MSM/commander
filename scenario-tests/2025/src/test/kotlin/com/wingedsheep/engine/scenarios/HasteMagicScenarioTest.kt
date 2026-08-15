package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.HasteMagic
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Haste Magic (FIN #140) — {1}{R} Instant.
 *
 *   "Target creature gets +3/+1 and gains haste until end of turn. Exile the top card of
 *    your library. You may play it until your next end step."
 *
 * Exercises the pump + haste grant plus an impulse-draw with the extended
 * [com.wingedsheep.sdk.scripting.effects.MayPlayExpiry.UntilNextEndStep] permission window.
 */
class HasteMagicScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + HasteMagic)
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        return driver
    }

    test("pumps the target +3/+1, grants haste, and impulse-exiles the top card") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Grizzly Bears is a vanilla 2/2 with no haste.
        val bears = driver.putCreatureOnBattlefield(me, "Grizzly Bears")
        driver.state.projectedState.getPower(bears) shouldBe 2
        driver.state.projectedState.getToughness(bears) shouldBe 2
        driver.state.projectedState.hasKeyword(bears, Keyword.HASTE) shouldBe false

        driver.putCardOnTopOfLibrary(me, "Mountain")
        val spell = driver.putCardInHand(me, "Haste Magic")
        driver.giveMana(me, Color.RED, 2)

        driver.castSpell(me, spell, listOf(bears)).isSuccess shouldBe true
        driver.bothPass()

        // Pump + haste applied (2/2 Grizzly Bears -> 5/3).
        driver.state.projectedState.getPower(bears) shouldBe 5
        driver.state.projectedState.getToughness(bears) shouldBe 3
        driver.state.projectedState.hasKeyword(bears, Keyword.HASTE) shouldBe true

        // Top card exiled and flagged playable.
        driver.getExileCardNames(me) shouldBe listOf("Mountain")
        val exiled = driver.getExile(me).single()
        driver.state.mayPlayPermissions.any { exiled in it.cardIds } shouldBe true

        // It can actually be played from exile this turn.
        driver.playLand(me, exiled).isSuccess shouldBe true
        driver.getExile(me).contains(exiled) shouldBe false
    }

    test("the play permission expires at the controller's own next end step") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val bears = driver.putCreatureOnBattlefield(me, "Grizzly Bears")
        driver.putCardOnTopOfLibrary(me, "Mountain")
        val spell = driver.putCardInHand(me, "Haste Magic")
        driver.giveMana(me, Color.RED, 2)

        driver.castSpell(me, spell, listOf(bears)).isSuccess shouldBe true
        driver.bothPass()

        val exiled = driver.getExile(me).single()
        // Live on the turn it was cast — "your next end step" is this turn's end step.
        driver.state.mayPlayPermissions.any { exiled in it.cardIds } shouldBe true

        // Once we pass this turn's end step into the opponent's turn, the window has closed.
        driver.passPriorityUntil(Step.END, maxPasses = 300)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN, maxPasses = 300)
        driver.state.activePlayerId shouldBe opp
        driver.state.mayPlayPermissions.any { exiled in it.cardIds } shouldBe false
    }
})
