package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Necroplasm (RAV #98) — a self-escalating sweeper: one +1/+1 counter each upkeep, and each end
 * step it destroys every creature whose mana value equals its current counter count.
 *
 * The interesting claim is that the destroy filter *reads* the counter count at resolution rather
 * than baking a number in, so the tests pin both halves of that: with one counter it takes the
 * one-drop and leaves the two-drop alone.
 */
class NecroplasmScenarioTest : ScenarioTestBase() {

    private fun plusCounters(game: TestGame, id: com.wingedsheep.sdk.model.EntityId): Int =
        game.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    init {
        context("Necroplasm") {

            test("the upkeep counter sets the mana value the end step sweeps") {
                var builder = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Necroplasm")
                    .withCardOnBattlefield(1, "Votary of the Conclave") // {W}, mana value 1
                    .withCardOnBattlefield(1, "Grizzly Bears") // {1}{G}, mana value 2
                repeat(4) { builder = builder.withCardInLibrary(1, "Forest") }
                repeat(4) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder
                    .withActivePlayer(1)
                    .inPhase(Phase.BEGINNING, Step.UNTAP)
                    .build()

                val necroplasm = game.findPermanent("Necroplasm")!!

                game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                withClue("the upkeep trigger put exactly one counter on") {
                    plusCounters(game, necroplasm) shouldBe 1
                }

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("one counter destroys mana value 1 and nothing else") {
                    game.findPermanent("Votary of the Conclave") shouldBe null
                    game.isInGraveyard(1, "Votary of the Conclave") shouldBe true
                    game.findPermanent("Grizzly Bears") shouldNotBe null
                }
                withClue("Necroplasm's own mana value is 3, so one counter never reaches it") {
                    game.findPermanent("Necroplasm") shouldNotBe null
                }
            }

            test("the sweep is symmetric — it takes an opponent's matching creature too") {
                var builder = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Necroplasm")
                    .withCardOnBattlefield(2, "Votary of the Conclave")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                repeat(4) { builder = builder.withCardInLibrary(1, "Forest") }
                repeat(4) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder
                    .withActivePlayer(1)
                    .inPhase(Phase.BEGINNING, Step.UNTAP)
                    .build()

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                game.findPermanent("Votary of the Conclave") shouldBe null
                game.isInGraveyard(2, "Votary of the Conclave") shouldBe true
                game.findPermanent("Grizzly Bears") shouldNotBe null
            }

            test("dredge 2 replaces a draw by milling two and returning Necroplasm") {
                var builder = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInGraveyard(1, "Necroplasm")
                    .withCardInHand(1, "Inspiration")
                    .withLandsOnBattlefield(1, "Island", 4)
                repeat(5) { builder = builder.withCardInLibrary(1, "Forest") }
                val game = builder
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpellTargetingPlayer(1, "Inspiration", 1).error shouldBe null
                game.resolveStack()

                val offer = game.state.pendingDecision
                (offer as YesNoDecision).context.sourceName shouldBe "Necroplasm"
                game.answerYesNo(true).error shouldBe null
                game.resolveStack()

                withClue("dredge 2 mills two and returns the Ooze; the second draw still happens") {
                    game.isInHand(1, "Necroplasm") shouldBe true
                    game.findCardsInGraveyard(1, "Forest").size shouldBe 2
                    game.librarySize(1) shouldBe 2
                }
            }

            // CR 121.2a: each draw is a separate event, so a two-card draw offers dredge twice.
            // Declining the first must not silently consume the second draw's offer.
            test("declining dredge on both draws draws normally and leaves Necroplasm in the graveyard") {
                var builder = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInGraveyard(1, "Necroplasm")
                    .withCardInHand(1, "Inspiration")
                    .withLandsOnBattlefield(1, "Island", 4)
                repeat(5) { builder = builder.withCardInLibrary(1, "Forest") }
                val game = builder
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpellTargetingPlayer(1, "Inspiration", 1).error shouldBe null
                game.resolveStack()
                game.answerYesNo(false).error shouldBe null
                game.resolveStack()

                withClue("the second draw gets its own offer") {
                    (game.state.pendingDecision as YesNoDecision).context.sourceName shouldBe "Necroplasm"
                }
                game.answerYesNo(false).error shouldBe null
                game.resolveStack()

                withClue("both draws are ordinary draws") {
                    game.isInGraveyard(1, "Necroplasm") shouldBe true
                    game.findCardsInGraveyard(1, "Forest").size shouldBe 0
                    game.librarySize(1) shouldBe 3
                }
            }
        }
    }
}
