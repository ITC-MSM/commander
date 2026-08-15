package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.engine.view.ClientStateTransformer
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

/**
 * Tests for Goblin Spy.
 *
 * Goblin Spy: {R}
 * Creature — Goblin Rogue
 * 1/1
 * Play with the top card of your library revealed.
 *
 * Goblin Spy reveals the top card of its controller's library to ALL players (public), but —
 * unlike Future Sight — grants no permission to play it from there. The public reveal shares the
 * [com.wingedsheep.engine.view.ClientStateTransformer] path with Future Sight's
 * `PlayFromTopOfLibrary`; this test pins both the shared visibility and the *absence* of play
 * permission.
 */
class GoblinSpyTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        return driver
    }

    fun transformer(d: GameTestDriver): ClientStateTransformer =
        ClientStateTransformer(cardRegistry = d.cardRegistry)

    test("top card is revealed to the opponent while Goblin Spy is on the battlefield") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Island" to 20, "Plains" to 20),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(activePlayer, "Goblin Spy")
        val topCard = driver.putCardOnTopOfLibrary(activePlayer, "Lightning Bolt")

        // From the opponent's viewpoint, the controller's top library card must be public:
        // it is transformed into the shared `cards` map and listed in the library zone.
        val view = transformer(driver).transform(driver.state, viewingPlayerId = opponent)

        view.cards.keys shouldContain topCard
        val libraryZone = view.zones.first {
            it.zoneId.ownerId == activePlayer && it.zoneId.zoneType == Zone.LIBRARY
        }
        libraryZone.cardIds shouldContain topCard
    }

    test("top card is NOT revealed when no reveal source is present") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Island" to 20, "Plains" to 20),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val topCard = driver.putCardOnTopOfLibrary(activePlayer, "Lightning Bolt")

        val view = transformer(driver).transform(driver.state, viewingPlayerId = opponent)

        // With no reveal source, the opponent must not see the controller's top card.
        view.cards.keys shouldNotContain topCard
    }

    test("Goblin Spy does NOT grant permission to cast from the top of the library") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Island" to 20, "Plains" to 20),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(activePlayer, "Goblin Spy")
        val boltOnTop = driver.putCardOnTopOfLibrary(activePlayer, "Lightning Bolt")
        driver.giveMana(activePlayer, Color.RED, 1)
        val creature = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")

        // Unlike Future Sight, the revealed top card cannot be cast from the library.
        val castResult = driver.castSpell(activePlayer, boltOnTop, listOf(creature))
        castResult.isSuccess shouldBe false
    }
})
