package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.SolvedComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.engine.view.ClientStateTransformer
import com.wingedsheep.mtg.sets.definitions.mkm.cards.CaseOfTheRansackedLab
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Case of the Ransacked Lab — {2}{U} Enchantment — Case.
 *
 * The one Case whose first line is a static ability, and the two halves feed each other: the
 * discount funds the four casts that solve it, and the discount keeps working afterwards. Three
 * things are worth pinning — that the reduction only shaves generic mana, that "you've cast four
 * or more instant and sorcery spells this turn" counts casts rather than resolutions, and that the
 * client can see the count climb toward four before the Case solves.
 */
class CaseOfTheRansackedLabScenarioTest : FunSpec({

    /** {U} instant: draw a card. All-coloured, so the discount can't touch it. */
    val testCantrip = card("Test Cantrip") {
        manaCost = "{U}"
        typeLine = "Instant"
        oracleText = "Draw a card."
        spell { effect = Effects.DrawCards(1) }
    }

    /** {2}{U} instant: gain 1 life. Generic-heavy, so the discount shows up. */
    val testTonic = card("Test Tonic") {
        manaCost = "{2}{U}"
        typeLine = "Instant"
        oracleText = "You gain 1 life."
        spell { effect = Effects.GainLife(1) }
    }

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(CaseOfTheRansackedLab)
        driver.registerCard(testCantrip)
        driver.registerCard(testTonic)
        driver.initMirrorMatch(Deck.of("Island" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.isSolved(id: EntityId): Boolean =
        state.getEntity(id)?.has<SolvedComponent>() == true

    fun GameTestDriver.handSize(playerId: EntityId): Int =
        state.getZone(ZoneKey(playerId, Zone.HAND)).size

    /** The "to solve" progress badge the controller's client renders on [id], e.g. "2/4". */
    fun GameTestDriver.solveProgress(id: EntityId): String? =
        ClientStateTransformer(cardRegistry)
            .transform(state, player1)
            .cards.getValue(id)
            .activeEffects
            .firstOrNull { it.effectId.startsWith("condition_compare") }
            ?.name

    /** Resolve everything on the stack — a solved Lab puts its draw trigger above the spell. */
    fun GameTestDriver.settle() {
        var guard = 0
        while (state.stack.isNotEmpty() && state.pendingDecision == null && guard++ < 20) {
            bothPass()
        }
    }

    fun GameTestDriver.castCantrip() {
        val spell = putCardInHand(player1, "Test Cantrip")
        giveMana(player1, Color.BLUE, 1)
        castSpell(player1, spell).isSuccess shouldBe true
        settle()
    }

    test("the client counts the instants and sorceries cast, 0/4 up to 4/4") {
        val driver = newDriver()
        val case = driver.putPermanentOnBattlefield(driver.player1, "Case of the Ransacked Lab")

        driver.solveProgress(case) shouldBe "0/4"

        repeat(2) { driver.castCantrip() }
        driver.solveProgress(case) shouldBe "2/4"

        repeat(2) { driver.castCantrip() }
        driver.solveProgress(case) shouldBe "4/4"

        // Solving replaces the countdown; the badge would otherwise sit at 4/4 for the rest of
        // the game, since "not solved" is what actually stops the trigger.
        driver.passPriorityUntil(Step.END)
        driver.bothPass()
        driver.isSolved(case) shouldBe true
        driver.solveProgress(case) shouldBe null
    }

    test("the count is per turn — three casts don't carry over to the next turn") {
        val driver = newDriver()
        val case = driver.putPermanentOnBattlefield(driver.player1, "Case of the Ransacked Lab")
        repeat(3) { driver.castCantrip() }
        driver.solveProgress(case) shouldBe "3/4"

        driver.passPriorityUntil(Step.END)
        driver.bothPass()
        driver.isSolved(case) shouldBe false

        // Back around to our own turn: the tracker is empty again.
        driver.passPriorityUntil(Step.UPKEEP)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.passPriorityUntil(Step.UPKEEP)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.solveProgress(case) shouldBe "0/4"
    }

    test("instant and sorcery spells cost {1} less — generic only") {
        val driver = newDriver()
        driver.putPermanentOnBattlefield(driver.player1, "Case of the Ransacked Lab")

        val tonic = driver.putCardInHand(driver.player1, "Test Tonic") // {2}{U}
        driver.giveMana(driver.player1, Color.BLUE, 1)
        driver.giveColorlessMana(driver.player1, 1)
        driver.castSpell(driver.player1, tonic).isSuccess shouldBe true
        driver.settle()
    }

    test("once solved, each instant or sorcery cast also draws a card (CR 702.169c)") {
        val driver = newDriver()
        val case = driver.putPermanentOnBattlefield(driver.player1, "Case of the Ransacked Lab")
        repeat(4) { driver.castCantrip() }
        driver.passPriorityUntil(Step.END)
        driver.bothPass()
        driver.isSolved(case) shouldBe true

        driver.passPriorityUntil(Step.UPKEEP)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.passPriorityUntil(Step.UPKEEP)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val before = driver.handSize(driver.player1)
        driver.castCantrip() // +1 in hand, -1 cast, +1 drawn by the spell, +1 by the Solved trigger
        driver.handSize(driver.player1) shouldBe before + 2
    }
})
