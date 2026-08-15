package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Demonic Bargain (VOW #103) — {2}{B} Sorcery
 *
 *   Exile the top thirteen cards of your library, then search your library for a card. Put that
 *   card into your hand, then shuffle.
 *
 * Exercises the self-mill-into-exile-then-tutor composite: exactly thirteen cards are exiled, then
 * the caster searches the remaining library for any one card and puts it into hand.
 */
class DemonicBargainScenarioTest : ScenarioTestBase() {

    init {
        context("Demonic Bargain — exile top thirteen, then tutor a card to hand") {

            test("exiles thirteen cards and tutors one remaining card into hand") {
                val builder = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Demonic Bargain")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

                // Seed a 15-card library: thirteen will be exiled, two remain to search among.
                repeat(13) { builder.withCardInLibrary(1, "Swamp") }
                builder.withCardInLibrary(1, "Grizzly Bears")
                builder.withCardInLibrary(1, "Grizzly Bears")

                val game = builder.build()
                game.librarySize(1) shouldBe 15

                game.castSpell(1, "Demonic Bargain").error shouldBe null
                game.resolveStack()

                withClue("thirteen cards were exiled") {
                    game.state.getExile(game.player1Id).size shouldBe 13
                }
                withClue("resolution pauses on the mandatory library search") {
                    game.hasPendingDecision() shouldBe true
                }
                val decision = game.getPendingDecision()
                decision.shouldBeInstanceOf<SelectCardsDecision>()
                withClue("the two surviving library cards are the search options") {
                    decision.options.size shouldBe 2
                }

                val fetched = decision.options.first()
                game.selectCards(listOf(fetched)).error shouldBe null
                game.resolveStack()

                withClue("the fetched card is now in hand") {
                    (fetched in game.state.getHand(game.player1Id)) shouldBe true
                }
                withClue("one card remains in the shuffled library (15 - 13 exiled - 1 to hand)") {
                    game.librarySize(1) shouldBe 1
                }
            }
        }
    }
}
