package com.wingedsheep.ai.puzzles.categories

import com.wingedsheep.ai.puzzles.AiPuzzle
import com.wingedsheep.ai.puzzles.PuzzleCategory

/**
 * Where the removal spell points.
 *
 * This is the category `Strategist.heuristicTargetRank` owns, plus the simulation refinement in
 * `chooseCommittedTargets` layered on top of it. Positions are built so that the *wrong* target is
 * the one raw creature value would pick.
 */
object RemovalTargetingPuzzles {

    fun all(): List<AiPuzzle> = listOf(

        AiPuzzle(
            id = "removal-01",
            category = PuzzleCategory.REMOVAL_TARGETING,
            expectation = "Murder the 6/4, not the 2/2",
            aiSeat = 1,
            position = { scenario ->
                scenario.withPlayers()
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withCardInHand(1, "Murder")
                    .withCardOnBattlefield(2, "Craw Wurm")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .build()
            },
            check = {
                shouldCast("Murder")
                shouldTarget("Craw Wurm")
            },
        ),

        AiPuzzle(
            id = "removal-02",
            category = PuzzleCategory.REMOVAL_TARGETING,
            expectation = "Bolt the 3/3 it can kill, not the 6/4 it bounces off",
            aiSeat = 1,
            position = { scenario ->
                scenario.withPlayers()
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withCardInHand(1, "Lightning Bolt")
                    .withCardOnBattlefield(2, "Craw Wurm")
                    .withCardOnBattlefield(2, "Hill Giant")
                    .build()
            },
            check = {
                shouldCast("Lightning Bolt")
                shouldTarget("Hill Giant")
            },
        ),

        AiPuzzle(
            id = "removal-03",
            category = PuzzleCategory.REMOVAL_TARGETING,
            expectation = "Do not spend removal on a creature Pacifism has already neutralized",
            aiSeat = 1,
            position = { scenario ->
                scenario.withPlayers()
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withCardInHand(1, "Murder")
                    .withCardOnBattlefield(2, "Craw Wurm")
                    .withCardOnBattlefield(2, "Hill Giant")
                    .withCardAttachedTo(2, "Pacifism", "Craw Wurm")
                    .build()
            },
            check = {
                shouldCast("Murder")
                shouldTarget("Hill Giant")
            },
        ),

        AiPuzzle(
            id = "removal-04",
            category = PuzzleCategory.REMOVAL_TARGETING,
            expectation = "Removal points at the opponent's creature, never at our own bigger one",
            aiSeat = 1,
            position = { scenario ->
                scenario.withPlayers()
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withCardInHand(1, "Murder")
                    .withCardOnBattlefield(1, "Craw Wurm")
                    .withCardOnBattlefield(2, "Hill Giant")
                    .build()
            },
            check = {
                shouldCast("Murder")
                shouldTarget("Hill Giant")
                shouldNotTarget("Craw Wurm")
            },
        ),

        AiPuzzle(
            id = "removal-05",
            category = PuzzleCategory.REMOVAL_TARGETING,
            expectation = "At 4 life, Bolt the 3/3 that is killing us rather than the opponent's face",
            aiSeat = 1,
            position = { scenario ->
                scenario.withPlayers()
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withCardInHand(1, "Lightning Bolt")
                    .withCardOnBattlefield(2, "Hill Giant")
                    .withLifeTotal(1, 4)
                    .build()
            },
            check = {
                shouldCast("Lightning Bolt")
                shouldTarget("Hill Giant")
            },
        ),

        AiPuzzle(
            id = "removal-06",
            category = PuzzleCategory.REMOVAL_TARGETING,
            expectation = "Kill the flier we cannot block, not the bigger creature our Giants hold off",
            aiSeat = 1,
            position = { scenario ->
                scenario.withPlayers()
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withCardInHand(1, "Murder")
                    .withCardOnBattlefield(1, "Hill Giant")
                    .withCardOnBattlefield(1, "Hill Giant")
                    .withCardOnBattlefield(2, "Air Elemental")
                    .withCardOnBattlefield(2, "Craw Wurm")
                    .withLifeTotal(1, 6)
                    .build()
            },
            check = {
                shouldCast("Murder")
                shouldTarget("Air Elemental")
            },
        ),
    )
}
