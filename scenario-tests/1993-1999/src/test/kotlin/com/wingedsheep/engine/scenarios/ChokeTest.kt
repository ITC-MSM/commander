package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Choke (TMP).
 *
 * Choke: {2}{G}
 * Enchantment
 * Islands don't untap during their controllers' untap steps.
 */
class ChokeTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        return driver
    }

    fun advanceToPlayerMain(driver: GameTestDriver, targetPlayer: EntityId) {
        driver.passPriorityUntil(Step.END, maxPasses = 200)
        driver.bothPass()
        if (driver.activePlayer == targetPlayer) {
            driver.passPriorityUntil(Step.PRECOMBAT_MAIN, maxPasses = 200)
            return
        }
        driver.passPriorityUntil(Step.END, maxPasses = 200)
        driver.bothPass()
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN, maxPasses = 200)
    }

    test("Choke prevents Choke controller's own Islands from untapping") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Forest" to 40),
            startingLife = 20
        )

        val player1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(player1, "Choke")
        val p1Island = driver.putLandOnBattlefield(player1, "Island")
        val p1Forest = driver.putLandOnBattlefield(player1, "Forest")
        driver.tapPermanent(p1Island)
        driver.tapPermanent(p1Forest)

        // Advance back to player1's next precombat main — their untap step has run.
        advanceToPlayerMain(driver, player1)
        driver.isTapped(p1Island) shouldBe true
        driver.isTapped(p1Forest) shouldBe false
    }

    test("Choke prevents opponent's Islands from untapping on their untap step") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Forest" to 40),
            startingLife = 20
        )

        val player1 = driver.activePlayer!!
        val player2 = driver.getOpponent(player1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(player1, "Choke")
        val p2Island = driver.putLandOnBattlefield(player2, "Island")
        val p2Forest = driver.putLandOnBattlefield(player2, "Forest")
        driver.tapPermanent(p2Island)
        driver.tapPermanent(p2Forest)

        // Advance to player2's precombat main — their untap step has run.
        advanceToPlayerMain(driver, player2)
        driver.isTapped(p2Island) shouldBe true
        driver.isTapped(p2Forest) shouldBe false
    }

    test("islands untap normally when no Choke is in play") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Forest" to 40),
            startingLife = 20
        )

        val player1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val island = driver.putLandOnBattlefield(player1, "Island")
        driver.tapPermanent(island)

        advanceToPlayerMain(driver, player1)
        driver.isTapped(island) shouldBe false
    }
})
