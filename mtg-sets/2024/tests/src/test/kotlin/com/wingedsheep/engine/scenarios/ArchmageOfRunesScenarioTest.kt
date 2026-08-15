package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Archmage of Runes (FDN #30) — {3}{U}{U} Giant Wizard, 3/6.
 *
 * "Instant and sorcery spells you cast cost {1} less to cast.
 *  Whenever you cast an instant or sorcery spell, draw a card."
 *
 * Verifies both halves: the [Triggers.YouCastInstantOrSorcery] draw payoff, and the
 * ModifySpellCost {1}-generic reduction (proven by casting a spell with only the reduced
 * amount of mana available).
 */
class ArchmageOfRunesScenarioTest : ScenarioTestBase() {

    init {
        context("Archmage of Runes") {

            test("draws a card whenever you cast an instant or sorcery") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Archmage of Runes")
                    .withCardInHand(1, "Shock") // instant
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Divination")
                    .withActivePlayer(1)
                    .build()

                val libBefore = game.state.getLibrary(game.player1Id).size

                game.castSpellTargetingPlayer(1, "Shock", 2).error shouldBe null
                game.resolveStack()

                withClue("casting an instant → Archmage draws one card (library -1)") {
                    game.state.getLibrary(game.player1Id).size shouldBe libBefore - 1
                }
            }

            test("instant and sorcery spells cost {1} less to cast") {
                // Divination normally costs {2}{U}; with the reduction it is {1}{U}. Only two
                // Islands ({U}{U} = 2 mana) are available — enough for {1}{U} but not {2}{U}.
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Archmage of Runes")
                    .withCardInHand(1, "Divination") // sorcery, {2}{U}
                    .withLandsOnBattlefield(1, "Island", 2)
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Shock")
                    .withCardInLibrary(1, "Lightning Bolt")
                    .withActivePlayer(1)
                    .build()

                val result = game.castSpell(1, "Divination")

                withClue("the {1}-less reduction makes Divination castable for {1}{U} off two Islands") {
                    result.error shouldBe null
                }
                withClue("the spell reached the stack") {
                    game.state.stack.isEmpty() shouldNotBe true
                }
            }
        }
    }
}
