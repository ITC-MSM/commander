package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.AbyssalGorestalker
import com.wingedsheep.mtg.sets.tokens.PredefinedTokens
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Abyssal Gorestalker (LCI #87) — {4}{B}{B} Creature — Horror 6/6.
 *
 * "When this creature enters, each player sacrifices two creatures of their choice."
 *
 * Proves two cases:
 *  1. When both players control more than two creatures each player is prompted and
 *     independently chooses two to sacrifice (symmetric edict).
 *  2. When a player controls fewer than two creatures the engine auto-sacrifices as
 *     many as possible — no selection prompt is raised.
 */
class AbyssalGorestalkerScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCards(PredefinedTokens.allTokens)
        driver.registerCard(AbyssalGorestalker)
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    /**
     * Drive the stack and sacrifice decisions until stable. For each [SelectCardsDecision]
     * encountered the minimum required number of cards is selected (first N options) so the
     * test is deterministic without hardcoding specific entity IDs.
     */
    fun GameTestDriver.drainStack(maxIterations: Int = 30) {
        var guard = 0
        while (guard++ < maxIterations) {
            val pd = pendingDecision
            when {
                pd is SelectCardsDecision ->
                    submitCardSelection(pd.playerId, pd.options.take(pd.minSelections))
                state.stack.isNotEmpty() -> bothPass()
                else -> return
            }
        }
    }

    test("ETB triggers each player to sacrifice two creatures of their choice") {
        val driver = newDriver()
        val me = driver.player1
        val opp = driver.player2

        // Give each player three creatures so both are prompted to choose two.
        driver.putCreatureOnBattlefield(me, "Grizzly Bears")
        driver.putCreatureOnBattlefield(me, "Grizzly Bears")
        driver.putCreatureOnBattlefield(me, "Grizzly Bears")

        driver.putCreatureOnBattlefield(opp, "Grizzly Bears")
        driver.putCreatureOnBattlefield(opp, "Grizzly Bears")
        driver.putCreatureOnBattlefield(opp, "Grizzly Bears")

        // Cast the Gorestalker — me now has 4 creatures (3 Bears + Gorestalker).
        val gorestalkerCard = driver.putCardInHand(me, "Abyssal Gorestalker")
        driver.giveColorlessMana(me, 4)
        driver.giveMana(me, Color.BLACK, 2)
        driver.castSpell(me, gorestalkerCard).isSuccess shouldBe true

        driver.drainStack()

        // Each player started with 3 bears + me got Gorestalker = 4 for me, 3 for opp.
        // Both were prompted to sacrifice exactly 2 → me has 2 left, opp has 1 left.
        driver.getCreatures(me).size shouldBe 2
        driver.getCreatures(opp).size shouldBe 1

        // Each player's graveyard should have exactly the sacrificed creatures.
        driver.getGraveyardCardNames(me).size shouldBe 2
        driver.getGraveyardCardNames(opp).size shouldBe 2
    }

    test("auto-sacrifices all creatures when a player controls fewer than two") {
        val driver = newDriver()
        val me = driver.player1
        val opp = driver.player2

        // Opponent controls exactly one creature; auto-sacrifice fires without a decision prompt.
        val oppBear = driver.putCreatureOnBattlefield(opp, "Grizzly Bears")

        // Cast the Gorestalker with no other creatures for me (me will have only the Gorestalker).
        val gorestalkerCard = driver.putCardInHand(me, "Abyssal Gorestalker")
        driver.giveColorlessMana(me, 4)
        driver.giveMana(me, Color.BLACK, 2)
        driver.castSpell(me, gorestalkerCard).isSuccess shouldBe true

        // No SelectCardsDecision should appear — both players have ≤ 2 creatures so auto-sacrifice.
        var sawDecision = false
        var guard = 0
        while (guard++ < 30) {
            val pd = driver.pendingDecision
            when {
                pd is SelectCardsDecision -> {
                    sawDecision = true
                    driver.submitCardSelection(pd.playerId, pd.options.take(pd.minSelections))
                }
                driver.state.stack.isNotEmpty() -> driver.bothPass()
                else -> break
            }
        }

        // Neither player should have been prompted (both had ≤ 2 creatures each).
        sawDecision shouldBe false

        // Me had 1 creature (Gorestalker), sacrificed it → me has 0 creatures now.
        driver.getCreatures(me).size shouldBe 0
        // Opp had 1 creature, auto-sacrificed → opp has 0 creatures.
        driver.getCreatures(opp).size shouldBe 0
        driver.findPermanent(opp, "Grizzly Bears") shouldBe null
    }
})
