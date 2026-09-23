package com.wingedsheep.engine.view

import com.wingedsheep.engine.core.PlotCard
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import com.wingedsheep.engine.core.Outcome

/**
 * A plotted card (CR 718, Outlaws of Thunder Junction) sits face-up in exile carrying a
 * [com.wingedsheep.engine.state.components.identity.PlottedComponent]. [ClientStateTransformer]
 * surfaces that as [ClientCard.isPlotted] so the client can badge it — otherwise a plotted card is
 * indistinguishable from any other exiled card. Plot exiles the card face-up (public information),
 * so the flag is visible to both players.
 */
class PlottedCardVisibilityTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        d.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    fun transformer(d: GameTestDriver): ClientStateTransformer =
        ClientStateTransformer(cardRegistry = d.cardRegistry)

    test("a card in hand (not yet plotted) is not flagged isPlotted") {
        val d = driver()
        val player = d.activePlayer!!
        val aloe = d.putCardInHand(player, "Aloe Alchemist")

        val view = transformer(d).transform(d.state, viewingPlayerId = player)
        view.cards[aloe]?.isPlotted shouldBe false
    }

    test("plotting Aloe Alchemist flags it isPlotted in exile for both players") {
        val d = driver()
        val player = d.activePlayer!!
        val opponent = d.getOpponent(player)

        val target = d.putCreatureOnBattlefield(player, "Grizzly Bears")
        val aloe = d.putCardInHand(player, "Aloe Alchemist")
        d.giveMana(player, Color.GREEN, 2) // plot cost {1}{G}

        (d.submit(PlotCard(player, aloe)).outcome is Outcome.Paused) shouldBe true
        d.submitTargetSelection(player, listOf(target))
        d.bothPass()

        // The card is now plotted in exile.
        d.getExile(player).contains(aloe) shouldBe true

        // Plot is face-up / public, so both the controller and the opponent see the flag.
        val ownerView = transformer(d).transform(d.state, viewingPlayerId = player)
        val opponentView = transformer(d).transform(d.state, viewingPlayerId = opponent)

        ownerView.cards[aloe].shouldNotBeNull().isPlotted shouldBe true
        opponentView.cards[aloe].shouldNotBeNull().isPlotted shouldBe true
    }
})
