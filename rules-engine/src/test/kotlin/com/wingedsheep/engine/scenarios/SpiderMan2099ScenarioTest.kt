package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.PlayLand
import com.wingedsheep.engine.state.components.player.CardsDiscardedThisTurnComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Spider-Man 2099 (SPM) — end-step intervening-if "if you've played a land or cast a spell this
 * turn from anywhere other than your hand, deals damage equal to his power to any target." Pins the
 * new `Conditions.YouPlayedLandFromNonHandThisTurn` (the land half), exercised by playing Oscorp
 * from the graveyard via Mayhem.
 */
class SpiderMan2099ScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        return driver
    }

    fun markDiscarded(driver: GameTestDriver, playerId: EntityId, cardId: EntityId) {
        val prior = driver.state.getEntity(playerId)
            ?.get<CardsDiscardedThisTurnComponent>() ?: CardsDiscardedThisTurnComponent()
        driver.addComponent(playerId, prior.copy(cardIds = prior.cardIds + cardId, count = prior.count + 1))
    }

    fun resolveStack(driver: GameTestDriver) {
        var guard = 0
        while (guard++ < 30 && driver.state.stack.isNotEmpty() && !driver.isPaused) driver.bothPass()
    }

    test("end step deals power to any target after you play a land from a non-hand zone") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        val opponent = driver.state.turnOrder.first { it != player }
        driver.putCreatureOnBattlefield(player, "Spider-Man 2099") // 2/3
        val oscorp = driver.putCardInGraveyard(player, "Oscorp Industries")

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        markDiscarded(driver, player, oscorp)
        driver.submit(PlayLand(player, oscorp)).error shouldBe null
        resolveStack(driver) // Oscorp's own −2 to the player

        // End step: the intervening-if is satisfied — deal power (2) to the opponent.
        driver.passPriorityUntil(Step.END)
        val decision = driver.pendingDecision as ChooseTargetsDecision
        driver.submitTargetSelection(player, listOf(opponent))
        resolveStack(driver)

        driver.getLifeTotal(opponent) shouldBe 18 // 20 − 2 power
    }

    test("end step does nothing if you only played lands from your hand") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        val opponent = driver.state.turnOrder.first { it != player }
        driver.putCreatureOnBattlefield(player, "Spider-Man 2099")
        val swamp = driver.putCardInHand(player, "Swamp")

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.submit(PlayLand(player, swamp)).error shouldBe null

        driver.passPriorityUntil(Step.END)
        resolveStack(driver)

        // No non-hand land/spell this turn — the trigger's intervening-if fails, no damage.
        driver.getLifeTotal(opponent) shouldBe 20
    }
})
