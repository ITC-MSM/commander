package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rav.cards.StoneshakerShaman
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Stoneshaker Shaman (RAV #145) — {2}{R} 1/1 Creature — Human Shaman
 *
 * "At the beginning of each player's end step, that player sacrifices an untapped land of their
 *  choice."
 *
 * Two things are easy to get wrong here and both are tested. The trigger is
 * [com.wingedsheep.sdk.dsl.Triggers.EachEndStep], not `YourEndStep`, so it fires on the Shaman's
 * controller's own turn too — this card is symmetric. And the sacrificing player is
 * `Player.TriggeringPlayer` (whoever's end step it is), not the Shaman's controller, which the
 * second test pins by putting the Shaman under the *non-active* player's control.
 *
 * The `untapped()` predicate is the third axis: per the RAV ruling, a player with no *untapped*
 * land sacrifices nothing rather than being forced to give up a tapped one.
 */
class StoneshakerShamanScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(StoneshakerShaman))
        return driver
    }

    fun startGame(driver: GameTestDriver) {
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), skipMulligans = true)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
    }

    fun graveyardSize(driver: GameTestDriver, playerId: EntityId): Int =
        driver.state.getZone(ZoneKey(playerId, Zone.GRAVEYARD)).size

    /**
     * Run the current turn's end step: reach it, let the trigger resolve, and answer the
     * sacrifice prompt if one was raised (a player with no untapped land is never asked).
     */
    fun runEndStep(driver: GameTestDriver) {
        driver.passPriorityUntil(Step.END)
        driver.bothPass()
        if (driver.pendingDecision != null) {
            driver.autoResolveDecision()
        }
    }

    test("the active player sacrifices an untapped land at their own end step") {
        val driver = createDriver()
        startGame(driver)
        val you = driver.activePlayer!!

        driver.putCreatureOnBattlefield(you, "Stoneshaker Shaman")
        driver.putLandOnBattlefield(you, "Mountain")
        driver.putLandOnBattlefield(you, "Mountain")

        val landsBefore = driver.getLands(you).size
        val graveyardBefore = graveyardSize(driver, you)

        runEndStep(driver)

        driver.getLands(you).size shouldBe landsBefore - 1
        graveyardSize(driver, you) shouldBe graveyardBefore + 1
    }

    test("the sacrificing player is whoever's end step it is, not the Shaman's controller") {
        val driver = createDriver()
        startGame(driver)
        val active = driver.activePlayer!!
        val other = driver.getOpponent(active)

        // The Shaman belongs to the *non-active* player; the active player still pays.
        driver.putCreatureOnBattlefield(other, "Stoneshaker Shaman")
        driver.putLandOnBattlefield(active, "Mountain")
        driver.putLandOnBattlefield(active, "Mountain")
        driver.putLandOnBattlefield(other, "Mountain")

        val activeLandsBefore = driver.getLands(active).size
        val otherLandsBefore = driver.getLands(other).size

        runEndStep(driver)

        driver.getLands(active).size shouldBe activeLandsBefore - 1
        driver.getLands(other).size shouldBe otherLandsBefore
    }

    test("a player whose lands are all tapped sacrifices nothing") {
        val driver = createDriver()
        startGame(driver)
        val you = driver.activePlayer!!

        driver.putCreatureOnBattlefield(you, "Stoneshaker Shaman")
        val onlyLand = driver.putLandOnBattlefield(you, "Mountain")
        driver.tapPermanent(onlyLand)

        val landsBefore = driver.getLands(you).size

        runEndStep(driver)

        driver.getLands(you).size shouldBe landsBefore
        driver.findPermanent(you, "Mountain") shouldBe onlyLand
    }
})
