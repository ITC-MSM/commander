package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Scenario tests for Lotuslight Dancers (TDM #204).
 *
 * {2}{B}{G}{U} Creature — Zombie Bard, 3/6, Lifelink.
 *   "When this creature enters, search your library for a black card, a green card, and a blue
 *    card. Put those cards into your graveyard, then shuffle."
 *
 * The ETB is three sequential single-card searches (one per color), each routed to the graveyard.
 * We confirm lifelink is present and that all three colored cards end up in the graveyard.
 */
class LotuslightDancersScenarioTest : ScenarioTestBase() {

    init {
        context("Lotuslight Dancers") {

            test("enters with lifelink and tutors a black, green, and blue card to the graveyard") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Lotuslight Dancers")
                    // {2}{B}{G}{U}: one source of each color plus two generic.
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withLandsOnBattlefield(1, "Island", 1)
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    // Library: one card of each required color plus a decoy.
                    .withCardInLibrary(1, "Black Creature")    // {1}{B}
                    .withCardInLibrary(1, "Centaur Courser")   // {2}{G}
                    .withCardInLibrary(1, "Phantom Warrior")   // {1}{U}{U}
                    .withCardInLibrary(1, "Mountain")          // colorless decoy
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Lotuslight Dancers").error shouldBe null
                game.resolveStack() // creature enters → first (black) search prompt

                // Three sequential search selections: black, then green, then blue.
                repeat(3) {
                    val decision = game.getPendingDecision()
                    decision.shouldBeInstanceOf<SelectCardsDecision>()
                    // Exactly one eligible card per color search.
                    game.selectCards(listOf(decision.options.first()))
                    game.resolveStack()
                }

                val dancers = game.findPermanent("Lotuslight Dancers")!!
                val projected = StateProjector().project(game.state)
                withClue("Lotuslight Dancers has lifelink") {
                    projected.hasKeyword(dancers, Keyword.LIFELINK) shouldBe true
                }

                val graveyardNames = game.state.getGraveyard(game.player1Id).mapNotNull { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name
                }
                withClue("All three colored cards are in the graveyard: $graveyardNames") {
                    graveyardNames.contains("Black Creature") shouldBe true
                    graveyardNames.contains("Centaur Courser") shouldBe true
                    graveyardNames.contains("Phantom Warrior") shouldBe true
                }
                withClue("The colorless decoy stays in the library") {
                    game.findCardsInLibrary(1, "Mountain").size shouldBe 1
                }
            }
        }
    }
}
