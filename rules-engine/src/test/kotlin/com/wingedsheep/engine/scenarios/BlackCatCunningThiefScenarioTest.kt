package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Scenario test for Black Cat, Cunning Thief (SPM #52) — {3}{B}{B} Legendary Creature 2/3.
 *
 *   When Black Cat enters, look at the top nine cards of target opponent's library, exile two of
 *   them face down, then put the rest on the bottom of their library in a random order. You may play
 *   the exiled cards for as long as they remain exiled. Mana of any type can be spent to cast spells
 *   this way.
 *
 * Covers the ETB dig (look at the top nine of the opponent's library, exile exactly two, bottom the
 * remaining seven) and the impulse-with-any-mana payoff: the exiled off-color card carries a
 * permanent may-play permission whose {G} pip is paid entirely from the Black Cat controller's
 * Swamps — impossible without `withAnyManaType`.
 */
class BlackCatCunningThiefScenarioTest : ScenarioTestBase() {

    init {
        context("Black Cat, Cunning Thief") {

            test("exiles two of the opponent's top nine, bottoms the rest, and an exiled off-color card is castable with any mana") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Black Cat, Cunning Thief")
                    // {3}{B}{B} for Black Cat (5) + {1}{G} for the stolen Grizzly Bears (2) = 7 Swamps.
                    .withLandsOnBattlefield(1, "Swamp", 7)
                    // Opponent's library = the nine looked-at cards: one off-color creature + eight lands.
                    .withCardInLibrary(2, "Grizzly Bears")
                    .withCardInLibrary(2, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpell(1, "Black Cat, Cunning Thief")
                withClue("Casting Black Cat should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                // Resolve the creature; the ETB trigger auto-targets the sole opponent and pauses on
                // the "exile two of them" selection.
                game.resolveStack()

                withClue("Black Cat resolved onto the battlefield") {
                    game.isOnBattlefield("Black Cat, Cunning Thief") shouldBe true
                }

                val decision = game.getPendingDecision()
                    .shouldBeInstanceOf<SelectCardsDecision>()
                withClue("The dig looks at nine cards and forces exactly two to be exiled") {
                    decision.options.size shouldBe 9
                    decision.minSelections shouldBe 2
                    decision.maxSelections shouldBe 2
                }

                // Exile the off-color Grizzly Bears plus one Forest.
                val grizzly = decision.options.first {
                    game.state.getEntity(it)?.get<CardComponent>()?.name == "Grizzly Bears"
                }
                val aForest = decision.options.first {
                    game.state.getEntity(it)?.get<CardComponent>()?.name == "Forest"
                }
                game.selectCards(listOf(grizzly, aForest))

                withClue("Exactly two cards were exiled (to the owner's exile), the other seven bottomed") {
                    namesInExile(game, 2).size shouldBe 2
                    game.state.getExile(game.player2Id).contains(grizzly).shouldBeTrue()
                    game.librarySize(2) shouldBe 7
                }

                withClue("The Black Cat controller holds a permanent, any-mana play permission for the exiled card") {
                    val permission = game.state.mayPlayPermissions.single { grizzly in it.cardIds }
                    permission.controllerId shouldBe game.player1Id
                    permission.permanent shouldBe true
                    permission.withAnyManaType shouldBe true
                }

                // Cast the exiled Grizzly Bears from the opponent's exile as Player1, paying its {G}
                // pip entirely from Swamps — only possible because mana of any type may be spent.
                val castExiled = game.execute(CastSpell(game.player1Id, grizzly))
                withClue("Casting the exiled Grizzly Bears from exile should succeed: ${castExiled.error}") {
                    castExiled.error shouldBe null
                }
                game.resolveStack()

                withClue("The exiled card resolved onto the Black Cat controller's battlefield") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                    game.state.getExile(game.player2Id).contains(grizzly) shouldBe false
                }
            }
        }
    }

    private fun namesInExile(game: TestGame, playerNumber: Int): List<String> {
        val playerId = if (playerNumber == 1) game.player1Id else game.player2Id
        return game.state.getExile(playerId).mapNotNull { id ->
            game.state.getEntity(id)?.get<CardComponent>()?.name
        }
    }
}
