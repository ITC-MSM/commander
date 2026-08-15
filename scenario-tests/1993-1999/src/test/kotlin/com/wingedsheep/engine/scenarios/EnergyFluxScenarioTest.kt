package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Energy Flux (ATQ #9).
 *
 * {2}{U} Enchantment
 * "All artifacts have \"At the beginning of your upkeep, sacrifice this artifact unless you pay
 *  {2}.\""
 *
 * Exercises the [com.wingedsheep.sdk.scripting.GrantTriggeredAbility] over all artifacts: each
 * artifact's controller is taxed on their own upkeep, paying {2} to keep it or sacrificing it. The
 * grant reaches opponents' artifacts too, and "your upkeep" resolves per the affected artifact's
 * controller.
 */
class EnergyFluxScenarioTest : ScenarioTestBase() {

    init {
        context("Energy Flux") {

            test("on your upkeep, paying {2} keeps your artifact") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Energy Flux")
                    .withCardOnBattlefield(1, "Ornithopter")
                    .withLandsOnBattlefield(1, "Island", 2)
                    // Start on the opponent's turn so a clean step transition into player 1's
                    // upkeep fires the granted trigger.
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(8) { builder = builder.withCardInLibrary(1, "Island") }
                repeat(8) { builder = builder.withCardInLibrary(2, "Island") }
                val game = builder.build()

                // Advance into the controller's (player 1's) upkeep: the granted trigger fires and
                // presents the pay-{2}-or-sacrifice decision.
                game.passUntilPhase(Phase.ENDING, Step.END)
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.resolveStack()

                game.answerYesNo(true) // pay {2}

                withClue("Paying {2} keeps the artifact") {
                    game.isOnBattlefield("Ornithopter") shouldBe true
                }
            }

            test("on your upkeep, declining to pay {2} sacrifices your artifact") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Energy Flux")
                    .withCardOnBattlefield(1, "Ornithopter")
                    .withLandsOnBattlefield(1, "Island", 2)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(8) { builder = builder.withCardInLibrary(1, "Island") }
                repeat(8) { builder = builder.withCardInLibrary(2, "Island") }
                val game = builder.build()

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.resolveStack()
                game.answerYesNo(false) // decline
                game.resolveStack()

                withClue("Declining the {2} sacrifices the artifact") {
                    game.isOnBattlefield("Ornithopter") shouldBe false
                }
            }

            test("an opponent's artifact is taxed on the opponent's upkeep and sacrificed when they can't pay") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Energy Flux")
                    // Opponent's artifact, and the opponent has NO mana to pay {2}.
                    .withCardOnBattlefield(2, "Ornithopter")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(8) { builder = builder.withCardInLibrary(1, "Island") }
                repeat(8) { builder = builder.withCardInLibrary(2, "Island") }
                val game = builder.build()

                // It is not the opponent's upkeep yet, so their artifact is untouched.
                withClue("Before the opponent's upkeep, their artifact remains") {
                    game.isOnBattlefield("Ornithopter") shouldBe true
                }

                // Advance to the opponent's upkeep. With no mana available, the pay-or-sacrifice
                // auto-suffers and the opponent's artifact is sacrificed.
                game.passUntilPhase(Phase.ENDING, Step.END)
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.resolveStack()

                withClue("Unable to pay {2}, the opponent's artifact is sacrificed on their upkeep") {
                    game.isOnBattlefield("Ornithopter") shouldBe false
                }
                withClue("Energy Flux is an enchantment, not an artifact — it is unaffected") {
                    game.isOnBattlefield("Energy Flux") shouldBe true
                }
            }
        }
    }
}
