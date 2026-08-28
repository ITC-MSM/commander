package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Dreamshackle Geist (VOW #58) — {1}{U}{U} Creature — Spirit, 3/1, Flying.
 *
 *   At the beginning of combat on your turn, choose up to one —
 *   • Tap target creature.
 *   • Target creature doesn't untap during its controller's next untap step.
 *
 * Exercises the "choose up to one" modal on a *triggered* ability: each mode, and the decline.
 * Mode 2's duration is keyed to the affected creature's controller, so the test unties the two
 * players' untap steps by checking the flag rather than only the tapped state.
 */
class DreamshackleGeistScenarioTest : ScenarioTestBase() {

    private fun beginCombatModal(game: TestGame): ChooseOptionDecision {
        game.passUntilPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
        var iterations = 0
        while (game.getPendingDecision() !is ChooseOptionDecision && iterations++ < 20) {
            game.passPriority()
        }
        return game.getPendingDecision() as? ChooseOptionDecision
            ?: error("expected the begin-combat modal; got ${game.getPendingDecision()}")
    }

    init {
        context("Dreamshackle Geist — begin-combat 'choose up to one'") {

            test("mode 1 taps the target creature") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Dreamshackle Geist", summoningSickness = false)
                    .withCardOnBattlefield(2, "Grizzly Bears", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                val modal = beginCombatModal(game)
                game.submitDecision(OptionChosenResponse(modal.id, optionIndex = 0))
                if (game.hasPendingDecision()) game.selectTargets(listOf(bears))
                game.resolveStack()

                withClue("mode 1 taps the chosen creature") {
                    game.state.getEntity(bears)?.has<TappedComponent>() shouldBe true
                }
            }

            test("mode 2 flags the target so it doesn't untap next untap step") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Dreamshackle Geist", summoningSickness = false)
                    .withCardOnBattlefield(2, "Grizzly Bears", tapped = true, summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                val modal = beginCombatModal(game)
                game.submitDecision(OptionChosenResponse(modal.id, optionIndex = 1))
                if (game.hasPendingDecision()) game.selectTargets(listOf(bears))
                game.resolveStack()

                withClue("the target carries the doesn't-untap flag") {
                    game.state.projectedState.hasKeyword(bears, AbilityFlag.DOESNT_UNTAP) shouldBe true
                }
                withClue("it was already tapped and stays tapped") {
                    game.state.getEntity(bears)?.has<TappedComponent>() shouldBe true
                }
            }

            test("declining both modes does nothing") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Dreamshackle Geist", summoningSickness = false)
                    .withCardOnBattlefield(2, "Grizzly Bears", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                val modal = beginCombatModal(game)
                val declineIndex = modal.options.indexOfFirst { it.contains("Don't choose", ignoreCase = true) }
                withClue("a decline option should be offered (minChooseCount = 0)") {
                    (declineIndex >= 0) shouldBe true
                }
                game.submitDecision(OptionChosenResponse(modal.id, optionIndex = declineIndex))
                game.resolveStack()

                withClue("nothing was tapped and no flag was granted") {
                    game.state.getEntity(bears)?.has<TappedComponent>() shouldBe false
                    game.state.projectedState.hasKeyword(bears, AbilityFlag.DOESNT_UNTAP) shouldBe false
                }
            }
        }
    }
}
