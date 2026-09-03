package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.engine.view.ClientStateTransformer
import com.wingedsheep.mtg.sets.definitions.inv.cards.GoblinSpy
import com.wingedsheep.mtg.sets.definitions.rav.cards.WizenedSnitches
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain

/**
 * Wizened Snitches (RAV #75) — "Players play with the top card of their libraries revealed."
 *
 * The symmetric sibling of Goblin Spy's self-scoped reveal, and the asymmetry is exactly what
 * these tests pin: one Snitches opens *both* libraries to *both* players, including the library of
 * the player who controls nothing. Goblin Spy is played alongside as the control — its reveal
 * still opens only its own controller's library, so a regression that made every reveal symmetric
 * would be caught here rather than in Goblin Spy's own test.
 */
class WizenedSnitchesScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + WizenedSnitches + GoblinSpy)
        d.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    fun transformer(d: GameTestDriver) = ClientStateTransformer(cardRegistry = d.cardRegistry)

    test("one Snitches reveals both players' top cards to both players") {
        val d = driver()
        val controller = d.player1
        val opponent = d.player2

        d.putCreatureOnBattlefield(controller, "Wizened Snitches")
        val myTop = d.putCardOnTopOfLibrary(controller, "Lightning Bolt")
        val theirTop = d.putCardOnTopOfLibrary(opponent, "Giant Growth")

        val opponentView = transformer(d).transform(d.state, viewingPlayerId = opponent)
        opponentView.cards.keys shouldContain myTop
        opponentView.cards.keys shouldContain theirTop

        val controllerView = transformer(d).transform(d.state, viewingPlayerId = controller)
        controllerView.cards.keys shouldContain myTop
        controllerView.cards.keys shouldContain theirTop
    }

    test("Goblin Spy stays self-scoped: only its controller's top card is public") {
        val d = driver()
        val controller = d.player1
        val opponent = d.player2

        d.putCreatureOnBattlefield(controller, "Goblin Spy")
        val myTop = d.putCardOnTopOfLibrary(controller, "Lightning Bolt")
        val theirTop = d.putCardOnTopOfLibrary(opponent, "Giant Growth")

        val controllerView = transformer(d).transform(d.state, viewingPlayerId = controller)
        controllerView.cards.keys shouldContain myTop
        controllerView.cards.keys shouldNotContain theirTop
    }

    test("with no reveal source, neither top card is public") {
        val d = driver()
        val controller = d.player1
        val opponent = d.player2

        val myTop = d.putCardOnTopOfLibrary(controller, "Lightning Bolt")
        val theirTop = d.putCardOnTopOfLibrary(opponent, "Giant Growth")

        val opponentView = transformer(d).transform(d.state, viewingPlayerId = opponent)
        opponentView.cards.keys shouldNotContain myTop

        val controllerView = transformer(d).transform(d.state, viewingPlayerId = controller)
        controllerView.cards.keys shouldNotContain theirTop
    }
})
