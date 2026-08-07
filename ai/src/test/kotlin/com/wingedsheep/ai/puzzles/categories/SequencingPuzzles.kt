package com.wingedsheep.ai.puzzles.categories

import com.wingedsheep.ai.puzzles.AiPuzzle
import com.wingedsheep.ai.puzzles.PuzzleCategory

/**
 * Which play comes first in a main phase.
 *
 * The probe is a single action, so these are all "of the plays available right now, which one?" —
 * land before spell, the land that produces the colour you need, the spell that uses the mana you
 * have.
 */
object SequencingPuzzles {

    fun all(): List<AiPuzzle> = listOf(

        AiPuzzle(
            id = "sequencing-01",
            category = PuzzleCategory.SEQUENCING,
            expectation = "Play the fourth land first — it is what makes the 4-drop castable this turn",
            aiSeat = 1,
            position = { scenario ->
                scenario.withPlayers()
                    .withLandsOnBattlefield(1, "Mountain", 3)
                    .withCardInHand(1, "Mountain")
                    .withCardInHand(1, "Hill Giant")
                    .build()
            },
            check = { shouldPlayLand("Mountain") },
        ),

        AiPuzzle(
            id = "sequencing-02",
            category = PuzzleCategory.SEQUENCING,
            expectation = "On the last card in hand, still make the land drop",
            aiSeat = 1,
            position = { scenario ->
                scenario.withPlayers()
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withCardInHand(1, "Forest")
                    .build()
            },
            // Deliberately paired with sequencing-04, which is the same decision with one more
            // card in hand. If 04 passes and this fails, the defect is precisely
            // `CardAdvantage.cardValue(0) = -3.0`: emptying your hand reads as a disaster, so the
            // AI would rather hold a land forever than play it. Land drops are free — which is what
            // `AiProfile.landDropIsNotCardLoss` finally tells the evaluator.
            check = { shouldPlayLand("Forest") },
        ),

        AiPuzzle(
            id = "sequencing-03",
            category = PuzzleCategory.SEQUENCING,
            expectation = "Four lands up: cast the 3/3, not the 2/2",
            aiSeat = 1,
            position = { scenario ->
                scenario.withPlayers()
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    .withCardInHand(1, "Hill Giant")
                    .withCardInHand(1, "Gray Ogre")
                    .build()
            },
            check = { shouldCast("Hill Giant") },
        ),

        AiPuzzle(
            id = "sequencing-04",
            category = PuzzleCategory.SEQUENCING,
            expectation = "Develop the board rather than pass with mana available",
            aiSeat = 1,
            position = { scenario ->
                scenario.withPlayers()
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withCardInHand(1, "Grizzly Bears")
                    // A second, uncastable card so this is *not* the last-card-in-hand cliff that
                    // sequencing-02 isolates. Same decision, one card of slack.
                    .withCardInHand(1, "Craw Wurm")
                    .build()
            },
            check = { shouldCast("Grizzly Bears") },
        ),

        AiPuzzle(
            id = "sequencing-05",
            category = PuzzleCategory.SEQUENCING,
            expectation = "Anthem before combat: it turns two 2/2s into exactly lethal",
            aiSeat = 1,
            position = { scenario ->
                scenario.withPlayers()
                    .withLandsOnBattlefield(1, "Plains", 3)
                    .withCardInHand(1, "Glorious Anthem")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withLifeTotal(2, 6)
                    .build()
            },
            check = { shouldCast("Glorious Anthem") },
        ),

        AiPuzzle(
            id = "sequencing-06",
            category = PuzzleCategory.SEQUENCING,
            expectation = "Play the Mountain, not the Forest — red is the colour the hand needs",
            aiSeat = 1,
            position = { scenario ->
                scenario.withPlayers()
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withCardInHand(1, "Mountain")
                    .withCardInHand(1, "Forest")
                    .withCardInHand(1, "Lightning Bolt")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .build()
            },
            check = { shouldPlayLand("Mountain") },
        ),
    )
}
