package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.LinkedExileComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Hoarding Dragon (M11 #144, {3}{R}{R}, 4/4 Dragon).
 *
 * Flying.
 * When this creature enters, you may search your library for an artifact card, exile it, then shuffle.
 * When this creature dies, you may put the exiled card into its owner's hand.
 *
 * The novel wiring is the linked exile: the ETB search exiles the found artifact linked to the
 * Dragon (`linkToSource = true`), so the dies trigger can retrieve it through
 * [com.wingedsheep.sdk.scripting.effects.CardSource.FromLinkedExile] even though the Dragon has
 * already left the battlefield. These tests pin the exile+link, the return-to-hand on death, and
 * the decline path.
 */
class HoardingDragonScenarioTest : ScenarioTestBase() {

    init {
        context("Hoarding Dragon") {

            fun newGame() = scenario()
                .withPlayers("Player1", "Player2")
                .withCardInHand(1, "Hoarding Dragon")
                .withCardInHand(1, "Murder")
                // Exactly 2 Mountains force the Dragon's {R}{R} onto them; the 6 Swamps then cover
                // the Dragon's 3 generic and Murder's {1}{B}{B} deterministically, so auto-pay can't
                // strand the black mana.
                .withLandsOnBattlefield(1, "Mountain", 2)
                .withLandsOnBattlefield(1, "Swamp", 6)
                .withCardInLibrary(1, "Ornithopter")
                .withCardInLibrary(1, "Forest")
                .withCardInLibrary(2, "Forest")
                .withCardInLibrary(2, "Forest")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            fun ornithopterInLibrary(game: TestGame): EntityId? =
                game.state.getLibrary(game.player1Id).firstOrNull { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == "Ornithopter"
                }

            test("ETB search exiles the chosen artifact, linked to the Dragon") {
                val game = newGame()

                game.castSpell(1, "Hoarding Dragon")
                game.resolveStack() // Dragon enters → optional ETB "may search" pause
                game.answerYesNo(true)
                val orni = ornithopterInLibrary(game)
                withClue("Ornithopter is available to find in the library") { orni shouldNotBe null }
                if (game.state.pendingDecision != null) game.selectCards(listOf(orni!!))
                game.resolveStack() // exile the found artifact, shuffle

                withClue("Ornithopter left the library") { ornithopterInLibrary(game) shouldBe null }
                withClue("Ornithopter is in player 1's exile") {
                    game.state.getExile(game.player1Id).any { id ->
                        game.state.getEntity(id)?.get<CardComponent>()?.name == "Ornithopter"
                    } shouldBe true
                }
                val dragon = game.findPermanent("Hoarding Dragon")!!
                val linked = game.state.getEntity(dragon)?.get<LinkedExileComponent>()
                withClue("The exiled artifact is linked to Hoarding Dragon") {
                    (linked?.exiledIds?.isNotEmpty()) shouldBe true
                }
            }

            test("dies returns the linked exiled artifact to its owner's hand") {
                val game = newGame()

                game.castSpell(1, "Hoarding Dragon")
                game.resolveStack()
                game.answerYesNo(true)
                val orni = ornithopterInLibrary(game)!!
                if (game.state.pendingDecision != null) game.selectCards(listOf(orni))
                game.resolveStack()

                val dragon = game.findPermanent("Hoarding Dragon")!!
                withClue("Murder is cast on the Dragon") {
                    game.castSpell(1, "Murder", dragon).error shouldBe null
                }
                // Murder resolves and the Dragon dies, firing its dies trigger. Drive the game
                // forward, taking the optional "may return the exiled card" trigger (answering yes
                // if it surfaces a decision), until the linked exiled card comes back.
                var guard = 0
                while (guard++ < 8) {
                    when {
                        game.state.pendingDecision != null -> game.answerYesNo(true)
                        game.state.stack.isNotEmpty() -> game.resolveStack()
                        else -> game.passPriority()
                    }
                    val inHand = game.state.getHand(game.player1Id).any { id ->
                        game.state.getEntity(id)?.get<CardComponent>()?.name == "Ornithopter"
                    }
                    if (inHand) break
                }

                withClue("The Dragon died (it's in the graveyard)") {
                    game.state.getGraveyard(game.player1Id).any { id ->
                        game.state.getEntity(id)?.get<CardComponent>()?.name == "Hoarding Dragon"
                    } shouldBe true
                }
                withClue("The exiled Ornithopter was returned to player 1's hand") {
                    game.state.getHand(game.player1Id).any { id ->
                        game.state.getEntity(id)?.get<CardComponent>()?.name == "Ornithopter"
                    } shouldBe true
                }
                withClue("Nothing is left in exile") {
                    game.state.getExile(game.player1Id).none { id ->
                        game.state.getEntity(id)?.get<CardComponent>()?.name == "Ornithopter"
                    } shouldBe true
                }
            }

            test("declining the ETB search exiles nothing") {
                val game = newGame()

                game.castSpell(1, "Hoarding Dragon")
                game.resolveStack()
                game.answerYesNo(false) // decline "you may search"
                game.resolveStack()

                withClue("Ornithopter stays in the library when the search is declined") {
                    ornithopterInLibrary(game) shouldNotBe null
                }
                withClue("Nothing was exiled") {
                    game.state.getExile(game.player1Id).none { id ->
                        game.state.getEntity(id)?.get<CardComponent>()?.name == "Ornithopter"
                    } shouldBe true
                }
            }
        }
    }
}
