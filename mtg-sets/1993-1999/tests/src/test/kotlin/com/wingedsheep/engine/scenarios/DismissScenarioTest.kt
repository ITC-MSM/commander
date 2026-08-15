package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Dismiss (TMP).
 *
 * Oracle: "Counter target spell. Draw a card."
 *
 * Reuses existing primitives ([com.wingedsheep.sdk.dsl.Effects.CounterSpell] +
 * [com.wingedsheep.sdk.dsl.Effects.DrawCards]); the test confirms the composed behaviour
 * resolves — the targeted spell is countered and the caster draws a card.
 */
class DismissScenarioTest : ScenarioTestBase() {

    init {
        context("Dismiss — counter target spell, draw a card") {
            test("counters the targeted spell and draws a card") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Dismiss")
                    .withLandsOnBattlefield(1, "Island", 4) // {2}{U}{U}
                    .withCardInLibrary(1, "Island") // something to draw
                    // Player 2 (active player) has a sorcery-speed spell to counter
                    .withCardInHand(2, "Grizzly Bears")
                    .withLandsOnBattlefield(2, "Forest", 2)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val handBefore = game.handSize(1) // player 1 holds only Dismiss

                // Player 2 puts Grizzly Bears on the stack during their main phase
                val castResult = game.castSpell(2, "Grizzly Bears")
                withClue("Casting Grizzly Bears should succeed: ${castResult.error}") {
                    castResult.error shouldBe null
                }

                // Player 2 passes priority so Player 1 can respond
                game.passPriority()

                // Player 1 responds with Dismiss (instant) targeting Grizzly Bears on the stack
                val dismissResult = game.castSpellTargetingStackSpell(1, "Dismiss", "Grizzly Bears")
                withClue("Casting Dismiss should succeed: ${dismissResult.error}") {
                    dismissResult.error shouldBe null
                }

                game.resolveStack()

                withClue("Grizzly Bears should be countered (in player 2's graveyard, not on battlefield)") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                    game.isInGraveyard(2, "Grizzly Bears") shouldBe true
                }
                withClue("Dismiss leaves hand (-1) and draws a card (+1) -> net 0 vs before-cast hand") {
                    game.handSize(1) shouldBe handBefore
                }
            }
        }
    }
}
