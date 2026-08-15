package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.blb.cards.FecundGreenshell
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

/**
 * Fecund Greenshell (BLB) — {3}{G}{G} Creature — Elemental Turtle 4/6, Reach.
 *
 * "Whenever this creature or another creature you control with toughness greater than
 *  its power enters, look at the top card of your library. If it's a land card, you may
 *  put it onto the battlefield tapped. Otherwise, put it into your hand."
 *
 * Regression: the ETB trigger previously fired for *any* creature you control entering,
 * including creatures whose power is greater than or equal to their toughness. It must
 * only fire for creatures with toughness strictly greater than power (CardPredicate
 * `ToughnessGreaterThanPower`).
 *
 * Each test seeds a nonland card (Centaur Courser) on top of the library — when the
 * trigger fires the effect looks at it, sees a nonland, and puts it into hand, giving a
 * clean observable signal.
 */
class FecundGreenshellScenarioTest : FunSpec({

    val allCards = TestCards.all + listOf(FecundGreenshell)

    fun createDriver(): GameTestDriver = GameTestDriver().apply {
        registerCards(allCards)
        initMirrorMatch(deck = Deck.of("Forest" to 20, "Plains" to 20), startingLife = 20)
    }

    /** Resolve the whole stack (the resolving spell plus any triggered abilities it queues). */
    fun GameTestDriver.resolveStack() {
        while (!isPaused && state.stack.isNotEmpty()) bothPass()
    }

    test("trigger fires when another creature you control with toughness greater than power enters") {
        val driver = createDriver()
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val you = driver.activePlayer!!

        driver.putCreatureOnBattlefield(you, "Fecund Greenshell")
        val topCard = driver.putCardOnTopOfLibrary(you, "Centaur Courser")

        // Birds of Paradise is 0/1 — toughness (1) > power (0), so it qualifies.
        val birds = driver.putCardInHand(you, "Birds of Paradise")
        driver.giveMana(you, Color.GREEN, 1)
        driver.castSpell(you, birds)
        driver.resolveStack()

        // Trigger fired: the nonland top card was put into hand.
        driver.state.pendingDecision shouldBe null
        driver.getHand(you) shouldContain topCard
    }

    test("trigger does NOT fire when a creature with power greater than toughness enters") {
        val driver = createDriver()
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val you = driver.activePlayer!!

        driver.putCreatureOnBattlefield(you, "Fecund Greenshell")
        val topCard = driver.putCardOnTopOfLibrary(you, "Centaur Courser")

        // Goblin Guide is 2/1 — power (2) > toughness (1), so it must NOT trigger.
        val goblin = driver.putCardInHand(you, "Goblin Guide")
        driver.giveMana(you, Color.RED, 1)
        driver.castSpell(you, goblin)
        driver.resolveStack()

        driver.state.pendingDecision shouldBe null
        driver.getHand(you) shouldNotContain topCard
    }

    test("trigger does NOT fire when a creature with power equal to toughness enters") {
        val driver = createDriver()
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val you = driver.activePlayer!!

        driver.putCreatureOnBattlefield(you, "Fecund Greenshell")
        val topCard = driver.putCardOnTopOfLibrary(you, "Centaur Courser")

        // Savannah Lions is 1/1 — toughness is not strictly greater than power.
        val lions = driver.putCardInHand(you, "Savannah Lions")
        driver.giveMana(you, Color.WHITE, 1)
        driver.castSpell(you, lions)
        driver.resolveStack()

        driver.state.pendingDecision shouldBe null
        driver.getHand(you) shouldNotContain topCard
    }

    test("trigger fires for Fecund Greenshell itself entering (4/6, toughness greater than power)") {
        val driver = createDriver()
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val you = driver.activePlayer!!

        val topCard = driver.putCardOnTopOfLibrary(you, "Centaur Courser")

        val greenshell = driver.putCardInHand(you, "Fecund Greenshell")
        driver.giveMana(you, Color.GREEN, 5)
        driver.castSpell(you, greenshell)
        driver.resolveStack()

        driver.state.pendingDecision shouldBe null
        driver.getHand(you) shouldContain topCard
    }
})
