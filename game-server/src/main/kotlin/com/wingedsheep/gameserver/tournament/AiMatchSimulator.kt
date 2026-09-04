package com.wingedsheep.gameserver.tournament

import com.wingedsheep.sdk.model.EntityId
import org.springframework.stereotype.Component
import kotlin.random.Random

/**
 * Decides a bracket match between two AI seats without playing it.
 *
 * A round-robin bracket with one human and N AI seats schedules O(N²) matches, of which only N
 * involve the human; the rest are AI against AI. Each of those costs a full engine game plus two AI
 * controllers on the server, and nobody is in it — the human only ever sees the result as a row in
 * the standings. Simulating them is what keeps a large bracket affordable on a small box.
 *
 * **The winner is a fair coin flip.** That is deliberate, not a placeholder: any cheap deck-strength
 * proxy we could compute here (curve, card ratings, colour count) is unvalidated against how the
 * engine AI actually plays, and baking an unmeasured bias into the standings is worse than an honest
 * 50/50 — it would look like a judgement while being noise with a story attached. If a validated
 * model of AI-vs-AI win probability ever exists, this is the one place it plugs in.
 *
 * Draws are never simulated. A real AI-vs-AI game can draw (the runaway-game backstops), but a coin
 * flip has no principled draw rate, and a simulated draw would move both players' match points on
 * invented grounds.
 */
@Component
class AiMatchSimulator(private val random: Random = Random.Default) {

    /**
     * Whether a match between [player1Id] and [player2Id] may be decided instead of played, given the
     * bracket's [bracketSeats] and which of them are [aiSeats].
     *
     * Two rules, and the second is the one that isn't obvious. Both seats in the match must be AI —
     * a human never has a game decided for them. And the *bracket* must have a human somewhere: a
     * bracket with no human seat was built to be watched (the AI Sandbox at
     * `/api/dev/ai-tournament`, which `just watch-ai-match` drives; a model-comparison run), and
     * simulating there would resolve the whole tournament in a single sweep and leave nothing to
     * open. The saving this exists for is the AI-vs-AI games running *alongside* a human's matches.
     */
    fun shouldSimulate(
        bracketSeats: Set<EntityId>,
        aiSeats: Set<EntityId>,
        player1Id: EntityId,
        player2Id: EntityId,
    ): Boolean =
        player1Id in aiSeats &&
            player2Id in aiSeats &&
            bracketSeats.any { it !in aiSeats }

    /** The winning seat of a simulated [player1Id] vs [player2Id] match. */
    fun pickWinner(player1Id: EntityId, player2Id: EntityId): EntityId =
        if (random.nextBoolean()) player1Id else player2Id
}
