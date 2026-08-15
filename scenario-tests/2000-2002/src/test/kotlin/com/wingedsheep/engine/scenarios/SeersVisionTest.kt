package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.engine.view.ClientStateTransformer
import com.wingedsheep.mtg.sets.definitions.inv.cards.SeersVision
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain

/**
 * Seer's Vision (INV #270) — the new [com.wingedsheep.sdk.scripting.OpponentsPlayWithHandsRevealed]
 * static ability.
 *
 * "Your opponents play with their hands revealed." This is the opponent-facing sibling of
 * Goblin Spy's `RevealTopOfLibrary`: while a player controls Seer's Vision, each opponent's hand
 * is publicly visible to that controller. The visibility is applied entirely by the
 * [ClientStateTransformer] hand-masking seam — these tests pin that the opponent's hand cards
 * become visible to the Seer's Vision controller (and to nobody else).
 *
 * The sacrifice-to-discard ability composes already-tested atomic effects (gather hand → select →
 * discard, identical to Addle), so it is not re-exercised here.
 */
class SeersVisionTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(SeersVision))
        return driver
    }

    fun transformer(d: GameTestDriver): ClientStateTransformer =
        ClientStateTransformer(cardRegistry = d.cardRegistry)

    test("opponent's hand is revealed to the Seer's Vision controller") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Island" to 20, "Swamp" to 20),
            startingLife = 20
        )

        val controller = driver.activePlayer!!
        val opponent = driver.getOpponent(controller)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(controller, "Seer's Vision")
        val opponentCard = driver.putCardInHand(opponent, "Lightning Bolt")

        // From the controller's viewpoint, the opponent's hand card is visible.
        val view = transformer(driver).transform(driver.state, viewingPlayerId = controller)

        view.cards.keys shouldContain opponentCard
        val opponentHand = view.zones.first {
            it.zoneId.ownerId == opponent && it.zoneId.zoneType == Zone.HAND
        }
        opponentHand.cardIds shouldContain opponentCard
    }

    test("opponent's hand is NOT revealed without Seer's Vision") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Island" to 20, "Swamp" to 20),
            startingLife = 20
        )

        val controller = driver.activePlayer!!
        val opponent = driver.getOpponent(controller)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val opponentCard = driver.putCardInHand(opponent, "Lightning Bolt")

        // With no reveal source, the controller must not see the opponent's hand card.
        val view = transformer(driver).transform(driver.state, viewingPlayerId = controller)

        view.cards.keys shouldNotContain opponentCard
    }

    test("the Seer's Vision controller's own hand is not exposed to the opponent") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Island" to 20, "Swamp" to 20),
            startingLife = 20
        )

        val controller = driver.activePlayer!!
        val opponent = driver.getOpponent(controller)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(controller, "Seer's Vision")
        val controllerCard = driver.putCardInHand(controller, "Lightning Bolt")

        // The opponent controls no reveal source, so the controller's hand stays hidden from them.
        val view = transformer(driver).transform(driver.state, viewingPlayerId = opponent)

        view.cards.keys shouldNotContain controllerCard
    }
})
