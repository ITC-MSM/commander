package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.tmt.cards.KitsunesTechnique
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Kitsune's Technique (TMT #42) — "Target opponent mills half their library, rounded up."
 *
 * Exercises the dynamic mill amount: ceil(librarySize / 2) cards move from the top of the
 * targeted opponent's library to their graveyard.
 */
class KitsunesTechniqueTest : FunSpec({
    test("target opponent mills half their library, rounded up") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(KitsunesTechnique))
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)

        val kitsune = driver.putCardInHand(player, "Kitsune's Technique")
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val libBefore = driver.state.getLibrary(opponent).size
        val gyBefore = driver.getGraveyard(opponent).size
        val expectedMill = (libBefore + 1) / 2 // ceil(libBefore / 2)

        driver.giveMana(player, Color.BLUE, 6) // {4}{U}{U}
        driver.castSpell(player, kitsune, listOf(opponent)).isSuccess shouldBe true
        while (driver.state.stack.isNotEmpty()) driver.bothPass()

        driver.getGraveyard(opponent).size shouldBe gyBefore + expectedMill
        driver.state.getLibrary(opponent).size shouldBe libBefore - expectedMill
    }
})
