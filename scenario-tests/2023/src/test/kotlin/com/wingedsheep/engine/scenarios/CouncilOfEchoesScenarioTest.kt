package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.CouncilOfEchoes
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

/**
 * Council of Echoes (LCI #51): {4}{U}{U} 4/4 Creature — Spirit Advisor
 * "Flying
 *  Descend 4 — When this creature enters, if there are four or more permanent cards in your
 *  graveyard, return up to one target nonland permanent other than this creature to its owner's
 *  hand."
 *
 * Tests:
 *  - With four or more permanent cards in the graveyard: ETB trigger fires, the controller
 *    selects a nonland permanent on the battlefield to bounce (not the Council itself), and
 *    it returns to its owner's hand.
 *  - With fewer than four permanent cards in the graveyard: the intervening-if condition
 *    (Descend 4) is not met; the trigger does not fire and no ChooseTargetsDecision is issued.
 *  - "Up to one" is optional: when the condition is met the controller may decline to choose
 *    any target; the trigger resolves as a no-op and the battlefield is unchanged.
 */
class CouncilOfEchoesScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(CouncilOfEchoes))
        driver.initMirrorMatch(
            deck = Deck.of("Island" to 20, "Grizzly Bears" to 20),
            skipMulligans = true,
            startingPlayer = 0
        )
        return driver
    }

    test("ETB trigger fires and bounces chosen nonland permanent when four or more permanent cards are in the graveyard") {
        val driver = createDriver()
        val player = driver.player1
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.activePlayer shouldBe player

        // A nonland permanent on the battlefield for the trigger to target.
        val bounceTarget = driver.putPermanentOnBattlefield(player, "Grizzly Bears")

        // Seed the graveyard with exactly four permanent cards — meets the Descend 4 threshold.
        repeat(4) { driver.putCardInGraveyard(player, "Grizzly Bears") }

        // Cast Council of Echoes ({4}{U}{U}).
        val council = driver.putCardInHand(player, "Council of Echoes")
        driver.giveColorlessMana(player, 4)
        driver.giveMana(player, Color.BLUE, 2)
        driver.castSpell(player, council)
        // Council of Echoes resolves → enters the battlefield → the intervening-if condition
        // is true (four permanent cards in graveyard) → trigger fires and pauses for the
        // controller to choose up to one nonland permanent other than the Council itself.
        driver.bothPass()

        val decision = driver.pendingDecision as ChooseTargetsDecision
        driver.submitTargetSelection(player, listOf(bounceTarget)).isSuccess shouldBe true
        // ETB trigger resolves: return target nonland permanent to its owner's hand.
        driver.bothPass()

        driver.getHand(player) shouldContain bounceTarget
        driver.getPermanents(player) shouldNotContain bounceTarget
    }

    test("ETB trigger does not fire when fewer than four permanent cards are in the graveyard") {
        val driver = createDriver()
        val player = driver.player1
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.activePlayer shouldBe player

        // Seed the graveyard with only three permanent cards — one short of the Descend 4 threshold.
        repeat(3) { driver.putCardInGraveyard(player, "Grizzly Bears") }

        // Cast Council of Echoes ({4}{U}{U}).
        val council = driver.putCardInHand(player, "Council of Echoes")
        driver.giveColorlessMana(player, 4)
        driver.giveMana(player, Color.BLUE, 2)
        driver.castSpell(player, council)
        // Council resolves → enters the battlefield → intervening-if condition is false
        // (only three permanent cards in graveyard) → trigger does not fire; no
        // ChooseTargetsDecision is issued.
        driver.bothPass()

        (driver.pendingDecision is ChooseTargetsDecision) shouldBe false
    }

    test("controller may choose no target when condition is met — trigger resolves as no-op") {
        val driver = createDriver()
        val player = driver.player1
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.activePlayer shouldBe player

        // A nonland permanent eligible to be bounced, but the controller will decline.
        val eligibleTarget = driver.putPermanentOnBattlefield(player, "Grizzly Bears")

        // Seed the graveyard with four permanent cards — condition is met.
        repeat(4) { driver.putCardInGraveyard(player, "Grizzly Bears") }

        val council = driver.putCardInHand(player, "Council of Echoes")
        driver.giveColorlessMana(player, 4)
        driver.giveMana(player, Color.BLUE, 2)
        driver.castSpell(player, council)
        driver.bothPass()

        // Condition is met, so a ChooseTargetsDecision is issued.
        val decision = driver.pendingDecision as ChooseTargetsDecision
        // Decline — choose zero targets ("up to one" allows this).
        driver.submitTargetSelection(player, emptyList()).isSuccess shouldBe true
        driver.bothPass()

        // No bounce occurred; the eligible permanent remains on the battlefield.
        driver.getPermanents(player) shouldContain eligibleTarget
        driver.getHand(player) shouldNotContain eligibleTarget
    }
})
