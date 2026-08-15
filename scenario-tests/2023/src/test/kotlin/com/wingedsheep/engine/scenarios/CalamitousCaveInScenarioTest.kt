package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.CalamitousCaveIn
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Calamitous Cave-In (LCI #139) — {3}{R} Sorcery.
 *
 * "Calamitous Cave-In deals X damage to each creature and each planeswalker,
 *  where X is the number of Caves you control plus the number of Cave cards
 *  in your graveyard."
 *
 * Covered:
 *  1. Two Caves on the battlefield and none in the graveyard → X = 2.
 *     A 2/2 creature (Grizzly Bears) takes exactly lethal damage and dies;
 *     a sturdier 3/3 creature (Centaur Courser) survives.
 *
 *  2. Two Caves on the battlefield plus one Cave card in the graveyard → X = 3.
 *     Adding the graveyard Cave raises X by 1; the formerly-surviving 3/3
 *     (Centaur Courser) now takes exactly lethal damage and dies.
 */
class CalamitousCaveInScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(CalamitousCaveIn)
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    /**
     * Drain the stack without expecting any interactive decisions: just pass priority
     * until the stack is empty (the sorcery resolves and SBAs clean up dying creatures).
     */
    fun GameTestDriver.drainStack(maxIterations: Int = 20) {
        var guard = 0
        while (state.stack.isNotEmpty() && guard++ < maxIterations) {
            bothPass()
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Test 1: 2 Caves controlled, 0 Cave cards in GY → X = 2
    //         Kills a 2/2; a 3/3 survives.
    // ─────────────────────────────────────────────────────────────────────────
    test("two Caves on the battlefield deal 2 damage to all creatures; 2/2 dies, 3/3 survives") {
        val driver = newDriver()
        val me = driver.player1
        val opp = driver.player2

        // Caster controls two Cave lands (counts as X = 2).
        driver.putPermanentOnBattlefield(me, "Captivating Cave")
        driver.putPermanentOnBattlefield(me, "Captivating Cave")

        // Opponent's board: Grizzly Bears (2/2) should die; Centaur Courser (3/3) should live.
        driver.putCreatureOnBattlefield(opp, "Grizzly Bears")
        driver.putCreatureOnBattlefield(opp, "Centaur Courser")

        val caveInCard = driver.putCardInHand(me, "Calamitous Cave-In")
        // {3}{R}: 3 colorless + 1 red
        driver.giveColorlessMana(me, 3)
        driver.giveMana(me, Color.RED, 1)

        val cast = driver.castSpell(me, caveInCard)
        cast.isSuccess shouldBe true

        driver.drainStack()

        // Grizzly Bears (2 toughness) took 2 damage — exactly lethal → dead.
        driver.findPermanent(opp, "Grizzly Bears") shouldBe null

        // Centaur Courser (3 toughness) took 2 damage — not lethal → alive.
        driver.findPermanent(opp, "Centaur Courser") shouldNotBe null
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Test 2: 2 Caves on the battlefield + 1 Cave card in the GY → X = 3
    //         Graveyard Cave raises X to 3; the 3/3 now takes exactly lethal damage.
    // ─────────────────────────────────────────────────────────────────────────
    test("graveyard Cave increases X by 1; 3/3 that survived X=2 now dies at X=3") {
        val driver = newDriver()
        val me = driver.player1
        val opp = driver.player2

        // Two Cave lands on the battlefield contribute 2 to X.
        driver.putPermanentOnBattlefield(me, "Captivating Cave")
        driver.putPermanentOnBattlefield(me, "Captivating Cave")
        // One Cave card in the graveyard contributes +1 to X → X = 3.
        driver.putCardInGraveyard(me, "Captivating Cave")

        // A 3/3 creature that would survive X = 2 should die at X = 3.
        driver.putCreatureOnBattlefield(opp, "Centaur Courser")

        val caveInCard = driver.putCardInHand(me, "Calamitous Cave-In")
        driver.giveColorlessMana(me, 3)
        driver.giveMana(me, Color.RED, 1)

        val cast = driver.castSpell(me, caveInCard)
        cast.isSuccess shouldBe true

        driver.drainStack()

        // Centaur Courser (3 toughness) took 3 damage — exactly lethal → dead.
        driver.findPermanent(opp, "Centaur Courser") shouldBe null
    }
})
