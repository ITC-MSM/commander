package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Impossible Inferno (DSK #140) — {4}{R} Instant.
 *
 * "Impossible Inferno deals 6 damage to target creature.
 *  Delirium — If there are four or more card types among cards in your graveyard, exile the
 *  top card of your library. You may play it until the end of your next turn."
 *
 * Composes a 6-damage DealDamageEffect with a Conditions.Delirium-gated impulse-exile body
 * (GatherCards(top 1) -> MoveCollection(EXILE) -> GrantMayPlayFromExile, UntilEndOfNextTurn).
 * No new SDK surface.
 */
class ImpossibleInfernoScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(Deck.of("Mountain" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    test("deals 6 damage to the target creature, destroying it") {
        val driver = newDriver()
        // 2/2 target dies to 6 damage.
        val target = driver.putCreatureOnBattlefield(driver.player2, "Grizzly Bears")
        val inferno = driver.putCardInHand(driver.player1, "Impossible Inferno")

        // No delirium: fewer than four card types in graveyard.
        val exileBefore = driver.getExile(driver.player1).size

        driver.giveMana(driver.player1, Color.RED, 5)
        driver.castSpell(driver.player1, inferno, listOf(target)).isSuccess shouldBe true
        driver.bothPass()

        driver.findPermanent(driver.player2, "Grizzly Bears") shouldBe null
        // Delirium not active -> no card exiled.
        driver.getExile(driver.player1).size shouldBe exileBefore
    }

    test("delirium active: exiles the top card and grants permission to play it") {
        val driver = newDriver()
        val target = driver.putCreatureOnBattlefield(driver.player2, "Grizzly Bears")
        val inferno = driver.putCardInHand(driver.player1, "Impossible Inferno")

        // Four card types in your graveyard: creature, instant, sorcery, enchantment.
        driver.putCardInGraveyard(driver.player1, "Grizzly Bears")
        driver.putCardInGraveyard(driver.player1, "Lightning Bolt")
        driver.putCardInGraveyard(driver.player1, "Careful Study")
        driver.putCardInGraveyard(driver.player1, "Test Enchantment")

        // Known top card to be exiled.
        driver.putCardOnTopOfLibrary(driver.player1, "Mountain")
        val exileBefore = driver.getExile(driver.player1).size

        driver.giveMana(driver.player1, Color.RED, 5)
        driver.castSpell(driver.player1, inferno, listOf(target)).isSuccess shouldBe true
        driver.bothPass()

        driver.findPermanent(driver.player2, "Grizzly Bears") shouldBe null
        // Delirium active -> top card exiled and playable.
        driver.getExile(driver.player1).size shouldBe exileBefore + 1
        val exiled = driver.getExile(driver.player1).last()
        driver.getExileCardNames(driver.player1).contains("Mountain") shouldBe true
        driver.state.mayPlayPermissions.any { exiled in it.cardIds } shouldBe true
    }

    test("delirium inactive with only three card types: no exile") {
        val driver = newDriver()
        val target = driver.putCreatureOnBattlefield(driver.player2, "Grizzly Bears")
        val inferno = driver.putCardInHand(driver.player1, "Impossible Inferno")

        // Only three card types: creature, instant, sorcery.
        driver.putCardInGraveyard(driver.player1, "Grizzly Bears")
        driver.putCardInGraveyard(driver.player1, "Lightning Bolt")
        driver.putCardInGraveyard(driver.player1, "Careful Study")

        val exileBefore = driver.getExile(driver.player1).size

        driver.giveMana(driver.player1, Color.RED, 5)
        driver.castSpell(driver.player1, inferno, listOf(target)).isSuccess shouldBe true
        driver.bothPass()

        driver.getExile(driver.player1).size shouldBe exileBefore
    }
})
