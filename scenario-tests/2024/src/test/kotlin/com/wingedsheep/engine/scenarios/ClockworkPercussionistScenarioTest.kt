package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Clockwork Percussionist (DSK #130) — {R} 1/1 Artifact Creature — Monkey Toy, Haste.
 *
 * "When this creature dies, exile the top card of your library. You may play it until the
 *  end of your next turn."
 *
 * Composes Triggers.Dies with the impulse-exile body (GatherCards(top 1) -> MoveCollection(EXILE)
 * -> GrantMayPlayFromExile, MayPlayExpiry.UntilEndOfNextTurn) plus printed Haste. No new SDK surface.
 */
class ClockworkPercussionistScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(Deck.of("Mountain" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    test("has haste") {
        val driver = newDriver()
        val perc = driver.putCreatureOnBattlefield(driver.player1, "Clockwork Percussionist")
        driver.state.projectedState.hasKeyword(perc, Keyword.HASTE) shouldBe true
    }

    test("dying exiles the top card and grants permission to play it") {
        val driver = newDriver()
        val perc = driver.putCreatureOnBattlefield(driver.player1, "Clockwork Percussionist")
        driver.putCardOnTopOfLibrary(driver.player1, "Mountain")

        val bolt = driver.putCardInHand(driver.player1, "Lightning Bolt")
        val exileBefore = driver.getExile(driver.player1).size

        // Lethal damage to the 1/1 -> it dies -> dies trigger fires.
        driver.giveMana(driver.player1, Color.RED, 1)
        driver.castSpell(driver.player1, bolt, listOf(perc)).isSuccess shouldBe true
        driver.bothPass() // resolve bolt (and SBA death)
        driver.bothPass() // resolve the dies trigger

        driver.findPermanent(driver.player1, "Clockwork Percussionist") shouldBe null
        driver.getExile(driver.player1).size shouldBe exileBefore + 1
        val exiled = driver.getExile(driver.player1).last()
        driver.getExileCardNames(driver.player1).contains("Mountain") shouldBe true
        driver.state.mayPlayPermissions.any { exiled in it.cardIds } shouldBe true
    }
})
