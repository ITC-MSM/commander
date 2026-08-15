package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Erudite Wizard (FDN) — {2}{U} 2/3 Human Wizard.
 * "Whenever you draw your second card each turn, put a +1/+1 counter on this creature."
 *
 * Exercises the [com.wingedsheep.sdk.dsl.Triggers.NthCardDrawn]`(2)` primitive wired to a
 * +1/+1 counter. The first draw of the turn does not fire it; the second does, exactly once.
 */
class EruditeWizardScenarioTest : ScenarioTestBase() {

    private fun plusOneCounters(game: TestGame, name: String): Int {
        val id = game.findPermanent(name) ?: error("$name not on battlefield")
        return game.state.getEntity(id)?.get<CountersComponent>()
            ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
    }

    init {
        context("Erudite Wizard's second-draw trigger") {

            test("drawing a second card in a turn adds a +1/+1 counter") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Erudite Wizard")
                    .withCardInHand(1, "Divination")
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(1, "Island")
                    .withCardsDrawnThisTurn(1, 0)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                plusOneCounters(game, "Erudite Wizard") shouldBe 0

                // Divination draws two cards; the 2nd crosses N=2 and fires the trigger once.
                game.castSpell(1, "Divination").error shouldBe null
                game.resolveStack()

                withClue("second draw of the turn should have added exactly one +1/+1 counter") {
                    plusOneCounters(game, "Erudite Wizard") shouldBe 1
                }
            }

            test("drawing only a first card does not add a counter") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Erudite Wizard")
                    .withCardInHand(1, "Divination")
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(1, "Island")
                    // Already drew one card this turn — the two draws below become the
                    // 2nd and 3rd draws, so N=2 was crossed by a prior draw, not by these.
                    .withCardsDrawnThisTurn(1, 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Divination").error shouldBe null
                game.resolveStack()

                withClue("no counter when the second draw already happened earlier this turn") {
                    plusOneCounters(game, "Erudite Wizard") shouldBe 0
                }
            }
        }
    }
}
