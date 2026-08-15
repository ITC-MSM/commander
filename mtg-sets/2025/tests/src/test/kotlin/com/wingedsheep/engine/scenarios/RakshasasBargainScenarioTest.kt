package com.wingedsheep.engine.scenarios
import com.wingedsheep.sdk.dsl.Patterns

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Scenario test for Rakshasa's Bargain (TDM #214) — {2/B}{2/G}{2/U} Instant.
 *
 * "Look at the top four cards of your library. Put two of them into your hand and the rest
 *  into your graveyard."
 *
 * Confirms the Patterns.Library.lookAtTopAndKeep(count = 4, keepCount = 2) pipeline: the player is
 * presented with the four top cards, keeps two (→ hand), and the other two go to the graveyard.
 */
class RakshasasBargainScenarioTest : ScenarioTestBase() {

    init {
        context("Rakshasa's Bargain dig") {

            test("look at top four, keep two to hand, rest to graveyard") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Rakshasa's Bargain")
                    // Six payable hybrid pips → six lands cover {2/B}{2/G}{2/U} via generic.
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withLandsOnBattlefield(1, "Island", 2)
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Hill Giant")
                    .withCardInLibrary(1, "Glory Seeker")
                    .withCardInLibrary(1, "Swamp")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val handBefore = game.handSize(1)
                val graveyardBefore = game.graveyardSize(1)

                game.castSpell(1, "Rakshasa's Bargain").error shouldBe null
                game.resolveStack()

                // The spell pauses for a select-two-of-four decision.
                withClue("Rakshasa's Bargain pauses for a keep-2 selection") {
                    game.hasPendingDecision() shouldBe true
                }
                val decision = game.getPendingDecision().shouldBeInstanceOf<SelectCardsDecision>()
                withClue("Looks at the top four cards") {
                    decision.options.size shouldBe 4
                }
                decision.minSelections shouldBe 2
                decision.maxSelections shouldBe 2

                game.selectCards(decision.options.take(2)).error shouldBe null

                // Hand: -1 (the spell, now in graveyard) + 2 (kept cards).
                withClue("Two cards kept go to hand (net +1 after the spell leaves hand)") {
                    game.handSize(1) shouldBe handBefore - 1 + 2
                }
                // Graveyard: +2 (the rest) + 1 (the spell itself).
                withClue("Remaining two cards plus the spell go to the graveyard") {
                    game.graveyardSize(1) shouldBe graveyardBefore + 2 + 1
                }
            }
        }
    }
}
