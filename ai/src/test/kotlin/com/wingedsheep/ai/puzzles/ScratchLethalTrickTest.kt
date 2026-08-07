package com.wingedsheep.ai.puzzles

import com.wingedsheep.ai.engine.AiProfile
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Step

/** Scratch: what does the AI do with a lethal combat trick, before vs after blockers? */
class ScratchLethalTrickTest : ScenarioTestBase() {

    private val runner by lazy { PuzzleRunner(cardRegistry) { scenario() } }

    private fun probe(id: String, position: (ScenarioBuilder) -> TestGame) {
        val puzzle = AiPuzzle(
            id = id,
            category = PuzzleCategory.LETHAL_DETECTION,
            expectation = "scratch",
            aiSeat = 1,
            position = position,
            check = { },
        )
        val result = runner.run(puzzle, AiProfile.PRODUCTION_CANDIDATE_LANDDROP)
        println(">>> $id: ${result.move} ${result.failure ?: ""}")
    }

    init {
        test("scratch probe") {
            // Opponent at 5, our unblocked 2/2, Giant Growth in hand = exactly lethal.
            // They hold a 1/1 that would be a bad block against a plain 2/2.
            probe("scratch-before-blockers") { scenario ->
                scenario.withPlayers()
                    .withLifeTotal(2, 5)
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withCardInHand(1, "Giant Growth")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardOnBattlefield(2, "Llanowar Elves")
                    .build()
                    .advanceToDeclaration(1, Step.DECLARE_ATTACKERS)
                    .also { it.declareAttackers(mapOf("Grizzly Bears" to 2)) }
                    .advanceToPriority(1, Step.DECLARE_ATTACKERS)
            }

            probe("scratch-after-no-blocks") { scenario ->
                scenario.withPlayers()
                    .withLifeTotal(2, 5)
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withCardInHand(1, "Giant Growth")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardOnBattlefield(2, "Llanowar Elves")
                    .build()
                    .advanceToDeclaration(1, Step.DECLARE_ATTACKERS)
                    .also { it.declareAttackers(mapOf("Grizzly Bears" to 2)) }
                    .advanceToDeclaration(2, Step.DECLARE_BLOCKERS)
                    .also { it.declareBlockers(emptyMap()) }
                    .advanceToPriority(1, Step.DECLARE_BLOCKERS)
            }

            // Same window, but the pump is not lethal (opponent at 12): a control for whether
            // "cast after no blocks" is about lethal or just about the window.
            probe("scratch-after-no-blocks-not-lethal") { scenario ->
                scenario.withPlayers()
                    .withLifeTotal(2, 12)
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withCardInHand(1, "Giant Growth")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardOnBattlefield(2, "Llanowar Elves")
                    .build()
                    .advanceToDeclaration(1, Step.DECLARE_ATTACKERS)
                    .also { it.declareAttackers(mapOf("Grizzly Bears" to 2)) }
                    .advanceToDeclaration(2, Step.DECLARE_BLOCKERS)
                    .also { it.declareBlockers(emptyMap()) }
                    .advanceToPriority(1, Step.DECLARE_BLOCKERS)
            }
        }
    }
}
