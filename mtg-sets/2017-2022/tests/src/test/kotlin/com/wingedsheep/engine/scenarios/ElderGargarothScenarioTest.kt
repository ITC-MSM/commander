package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Elder Gargaroth (M21).
 *
 * {3}{G}{G} Creature — Beast 6/6, Reach, Vigilance, Trample.
 * "Whenever this creature attacks or blocks, choose one —
 *  • Create a 3/3 green Beast creature token.
 *  • You gain 3 life.
 *  • Draw a card."
 *
 * Mode order in the card: 0 create token, 1 gain 3 life, 2 draw a card.
 */
class ElderGargarothScenarioTest : ScenarioTestBase() {

    init {
        fun chooseMode(game: TestGame, modeIndex: Int) {
            val modeDecision = game.getPendingDecision() as ChooseOptionDecision
            game.submitDecision(OptionChosenResponse(modeDecision.id, modeIndex))
        }

        context("Elder Gargaroth attacking") {

            test("attacking triggers the modal choice; choosing token creation makes a 3/3 Beast") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Elder Gargaroth", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                game.declareAttackers(mapOf("Elder Gargaroth" to 2)).error shouldBe null
                game.resolveStack()

                chooseMode(game, 0)
                game.resolveStack()

                val beasts = game.findPermanents("Beast Token")
                withClue("A 3/3 green Beast token was created") {
                    beasts.size shouldBe 1
                }
            }

            test("attacking triggers the modal choice; choosing gain life adds 3 life") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Elder Gargaroth", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                val lifeBefore = game.getLifeTotal(1)

                game.declareAttackers(mapOf("Elder Gargaroth" to 2)).error shouldBe null
                game.resolveStack()

                chooseMode(game, 1)
                game.resolveStack()

                withClue("Gains exactly 3 life") {
                    game.getLifeTotal(1) shouldBe lifeBefore + 3
                }
            }

            test("attacking triggers the modal choice; choosing draw a card draws one card") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Elder Gargaroth", summoningSickness = false)
                    .withCardInLibrary(1, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                val handSizeBefore = game.handSize(1)

                game.declareAttackers(mapOf("Elder Gargaroth" to 2)).error shouldBe null
                game.resolveStack()

                chooseMode(game, 2)
                game.resolveStack()

                withClue("Drew exactly one card") {
                    game.handSize(1) shouldBe handSizeBefore + 1
                }
            }
        }

        context("Elder Gargaroth blocking") {

            test("blocking also triggers the modal choice") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardOnBattlefield(2, "Elder Gargaroth", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                val lifeBefore = game.getLifeTotal(2)

                game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)

                game.declareBlockers(mapOf("Elder Gargaroth" to listOf("Grizzly Bears"))).error shouldBe null
                game.resolveStack()

                chooseMode(game, 1)
                game.resolveStack()

                withClue("Blocking controller gains 3 life from the chosen mode") {
                    game.getLifeTotal(2) shouldBe lifeBefore + 3
                }
            }
        }
    }
}
