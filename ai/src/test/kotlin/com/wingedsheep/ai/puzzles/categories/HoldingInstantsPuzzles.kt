package com.wingedsheep.ai.puzzles.categories

import com.wingedsheep.ai.puzzles.AiPuzzle
import com.wingedsheep.ai.puzzles.PuzzleCategory
import com.wingedsheep.ai.puzzles.advanceToDeclaration
import com.wingedsheep.ai.puzzles.advanceToPriority
import com.wingedsheep.sdk.core.Step

/**
 * Instant timing: hold it until it does something, then actually use it.
 *
 * Half of these are negative controls (don't fire the trick in your own main phase) and half are
 * positive (fire it in the window where it wins the fight). A category made only of "don't cast"
 * puzzles would score 100% for an AI that never casts anything, which measures nothing.
 */
object HoldingInstantsPuzzles {

    fun all(): List<AiPuzzle> = listOf(

        AiPuzzle(
            id = "instants-01",
            category = PuzzleCategory.HOLDING_INSTANTS,
            expectation = "Hold Giant Growth in our own main phase — +3/+3 still loses to a 6/4 blocker",
            aiSeat = 1,
            position = { scenario ->
                scenario.withPlayers()
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withCardInHand(1, "Giant Growth")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardOnBattlefield(2, "Craw Wurm")
                    .build()
            },
            check = { shouldNotCast("Giant Growth") },
        ),

        AiPuzzle(
            id = "instants-02",
            category = PuzzleCategory.HOLDING_INSTANTS,
            expectation = "Giant Growth on the blocked attacker: the 2/2 becomes a 5/5 and eats the 3/3",
            aiSeat = 1,
            position = { scenario ->
                scenario.withPlayers()
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withCardInHand(1, "Giant Growth")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardOnBattlefield(2, "Hill Giant")
                    .build()
                    .advanceToDeclaration(1, Step.DECLARE_ATTACKERS)
                    .also { it.declareAttackers(mapOf("Grizzly Bears" to 2)) }
                    .advanceToDeclaration(2, Step.DECLARE_BLOCKERS)
                    .also { it.declareBlockers(mapOf("Hill Giant" to listOf("Grizzly Bears"))) }
                    .advanceToPriority(1, Step.DECLARE_BLOCKERS)
            },
            check = {
                shouldCast("Giant Growth")
                shouldTarget("Grizzly Bears")
            },
        ),

        AiPuzzle(
            id = "instants-03",
            category = PuzzleCategory.HOLDING_INSTANTS,
            expectation = "Titanic Growth on our blocker: the 3/3 becomes a 7/7 and kills the 6/4",
            aiSeat = 2,
            position = { scenario ->
                scenario.withPlayers()
                    .withLandsOnBattlefield(2, "Forest", 2)
                    .withCardInHand(2, "Titanic Growth")
                    .withCardOnBattlefield(1, "Craw Wurm")
                    .withCardOnBattlefield(2, "Hill Giant")
                    .build()
                    .advanceToDeclaration(1, Step.DECLARE_ATTACKERS)
                    .also { it.declareAttackers(mapOf("Craw Wurm" to 2)) }
                    .advanceToDeclaration(2, Step.DECLARE_BLOCKERS)
                    .also { it.declareBlockers(mapOf("Hill Giant" to listOf("Craw Wurm"))) }
                    .advanceToPriority(2, Step.DECLARE_BLOCKERS)
            },
            check = {
                shouldCast("Titanic Growth")
                shouldTarget("Hill Giant")
            },
        ),

        AiPuzzle(
            id = "instants-04",
            category = PuzzleCategory.HOLDING_INSTANTS,
            expectation = "Hold Fog in our own main phase — there is no combat damage to prevent",
            aiSeat = 1,
            position = { scenario ->
                scenario.withPlayers()
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withCardInHand(1, "Fog")
                    .withCardOnBattlefield(2, "Craw Wurm")
                    .build()
            },
            check = { shouldNotCast("Fog") },
        ),

        AiPuzzle(
            id = "instants-05",
            category = PuzzleCategory.HOLDING_INSTANTS,
            expectation = "Fog the lethal alpha strike at 2 life",
            aiSeat = 2,
            position = { scenario ->
                scenario.withPlayers()
                    .withLandsOnBattlefield(2, "Forest", 1)
                    .withCardInHand(2, "Fog")
                    .withCardOnBattlefield(1, "Craw Wurm")
                    .withCardOnBattlefield(1, "Hill Giant")
                    .withLifeTotal(2, 2)
                    .build()
                    .advanceToDeclaration(1, Step.DECLARE_ATTACKERS)
                    .also { it.declareAttackers(mapOf("Craw Wurm" to 2, "Hill Giant" to 2)) }
                    .advanceToPriority(2, Step.DECLARE_BLOCKERS)
            },
            check = { shouldCast("Fog") },
        ),

        AiPuzzle(
            id = "instants-06",
            category = PuzzleCategory.HOLDING_INSTANTS,
            expectation = "Do not dump Giant Growth on the opponent's end step just because mana would be wasted",
            aiSeat = 2,
            position = { scenario ->
                scenario.withPlayers()
                    .withLandsOnBattlefield(2, "Forest", 1)
                    .withCardInHand(2, "Giant Growth")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .build()
                    .advanceToPriority(2, Step.END)
            },
            // Probes `Strategist`'s hard-coded `passScore - 1.5` end-step discount directly: the
            // pump wears off in cleanup, so spending it here throws the card away for nothing.
            check = { shouldNotCast("Giant Growth") },
        ),

        AiPuzzle(
            id = "instants-07",
            category = PuzzleCategory.HOLDING_INSTANTS,
            expectation = "They took the 2/2 unblocked at 5 life — Giant Growth is exactly lethal",
            aiSeat = 1,
            position = { scenario ->
                scenario.withPlayers()
                    .withLifeTotal(2, 5)
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withCardInHand(1, "Giant Growth")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    // The blocker they declined to use. It is also a second legal target for the
                    // trick, which is the whole difficulty: with only one creature on the board the
                    // AI finds this line on every profile ever measured.
                    .withCardOnBattlefield(2, "Llanowar Elves")
                    .build()
                    .advanceToDeclaration(1, Step.DECLARE_ATTACKERS)
                    .also { it.declareAttackers(mapOf("Grizzly Bears" to 2)) }
                    .advanceToDeclaration(2, Step.DECLARE_BLOCKERS)
                    .also { it.declareBlockers(emptyMap()) }
                    .advanceToPriority(1, Step.DECLARE_BLOCKERS)
            },
            check = {
                shouldCast("Giant Growth")
                shouldTarget("Grizzly Bears")
            },
        ),

        AiPuzzle(
            id = "instants-08",
            category = PuzzleCategory.HOLDING_INSTANTS,
            expectation = "Do not pump before blockers — a visible 5/5 gets chump-blocked by the 1/1",
            aiSeat = 1,
            position = { scenario ->
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
            },
            // The negative control for 07: the same board, the same lethal trick, one priority
            // window earlier. Casting now is not merely premature, it *loses* the kill — blocking
            // a 2/2 with a 1/1 is a bad trade the defender declines, and chump-blocking a 5/5 to
            // live is one they take. No board evaluation can see that, because the information the
            // cast leaks is not on the board.
            check = { shouldNotCast("Giant Growth") },
        ),
    )
}
