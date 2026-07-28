package com.wingedsheep.ai.engine.knowledge

import com.wingedsheep.engine.state.GameState
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId

/**
 * "Is this the window for this card?" — the timing half of Phase 6.
 *
 * A one-ply evaluator has no notion of a window. It scores the board right after the spell
 * resolves, and a combat trick cast in your own main phase scores exactly as well there as one cast
 * after blockers, because `ThreatAssessment` reads the pumped power either way. The result is an AI
 * that dumps its instants at the first opportunity.
 *
 * Before Phase 6 the only correction was one hardcoded line in `Strategist`: `passScore - 1.5` on
 * the opponent's end step, applied to *every* candidate. That is both too blunt — it *encourages*
 * dumping a pump that is about to wear off in cleanup, exactly as much as it encourages a removal
 * spell that would otherwise rot in hand — and too narrow, since it says nothing about our own main
 * phase, which is where the mistake actually happens. This replaces it with a per-card verdict
 * driven by [CardIntent].
 *
 * A [TimingVerdict.Adjust] is in the board score's own units, so a point here is a point of board
 * value. A [TimingVerdict.NoWindow] is a different kind of claim; see there.
 */
class HoldPolicy(private val intents: IntentCatalog) {

    /** Whether this policy can say anything. False when the agent has no card knowledge. */
    val isEnabled: Boolean get() = intents.isEnabled

    /**
     * The policy's reading of *this* window for [cardName].
     *
     * Only instant-speed cards are ever judged: a sorcery has no window to wait for, and
     * penalizing one would just make the AI pass with a full hand.
     */
    fun verdictFor(state: GameState, playerId: EntityId, cardName: String): TimingVerdict {
        val intent = intents.forName(cardName) ?: return TimingVerdict.Neutral
        if (intent.speed != Speed.INSTANT) return TimingVerdict.Neutral

        val stackHasSomething = state.stack.isNotEmpty()

        return when {
            // A counterspell with nothing to counter is not a play, it is a discard.
            IntentTag.COUNTERSPELL in intent.tags && !stackHasSomething -> TimingVerdict.NoWindow

            // A pump wears off at cleanup, so it is worth casting only when something will use it
            // before then: a fight in combat, or a spell on the stack it can save the creature
            // from. Anywhere else — our own main phase, and specifically the opponent's end step,
            // where the old blanket `passScore - 1.5` discount actively *encouraged* dumping it —
            // it buys nothing at all.
            IntentTag.COMBAT_TRICK in intent.tags -> when {
                state.step in COMBAT_STEPS -> TimingVerdict.Adjust(COMBAT_WINDOW)
                stackHasSomething -> TimingVerdict.Adjust(RESPONSE_WINDOW)
                else -> TimingVerdict.NoWindow
            }

            // Instant-speed removal: reward the windows that are strictly better than now, and
            // charge nothing for casting it early.
            //
            // The symmetric penalty — "hold it, our own main phase is the wrong time" — is what the
            // plan proposed, and it was built, measured and removed. Holding removal is a
            // *preference* between two futures, not a provable loss: it costs the option of a
            // better target later and buys certainty now. A constant cannot price that, and the one
            // large enough to change behaviour was large enough to veto casting the removal at all
            // — the exact blindness this phase exists to fix. Pricing "what if a better target
            // shows up" needs the rollout evaluator (Phase 7), not a literal.
            intent.tags.any { it in REMOVAL_TAGS } -> when {
                stackHasSomething -> TimingVerdict.Adjust(RESPONSE_WINDOW)
                state.activePlayerId != playerId && state.step == Step.END ->
                    TimingVerdict.Adjust(END_STEP_WINDOW)

                else -> TimingVerdict.Neutral
            }

            else -> TimingVerdict.Neutral
        }
    }

    private companion object {
        /** Blockers are in; a trick decides the fight. */
        const val COMBAT_WINDOW = 1.0

        /** Something is on the stack to answer. */
        const val RESPONSE_WINDOW = 1.0

        /**
         * Their end step: the last moment holding it is still free. Replaces the blanket
         * `passScore - 1.5`, but only for the cards that actually want the window.
         */
        const val END_STEP_WINDOW = 1.5

        val COMBAT_STEPS = setOf(
            Step.BEGIN_COMBAT, Step.DECLARE_ATTACKERS, Step.DECLARE_BLOCKERS,
            Step.FIRST_STRIKE_COMBAT_DAMAGE, Step.COMBAT_DAMAGE,
        )

        val REMOVAL_TAGS = setOf(IntentTag.REMOVAL, IntentTag.EXILE_REMOVAL, IntentTag.SWEEPER)
    }
}

/** What [HoldPolicy] makes of casting a particular card in a particular window. */
sealed interface TimingVerdict {
    /** Timing says nothing here; the board score stands as it is. */
    data object Neutral : TimingVerdict

    /** A nudge in the board score's own units. */
    data class Adjust(val delta: Double) : TimingVerdict

    /**
     * The card **cannot accomplish anything** in this window — a counterspell with an empty stack,
     * a pump that will wear off before any combat.
     *
     * This is a floor, not a penalty, and the difference matters. A penalty is a constant racing an
     * unbounded number, and it loses: `ThreatAssessment` reads a Giant Growth's +3/+3 as a
     * permanently faster clock and pays about 10 points for it, so no defensible literal could
     * outvote it. A verdict says the thing worth saying instead — *whatever the simulation reports,
     * this is not better than passing* — and the Strategist scores it just below the pass score.
     * Reserved for cases where "does nothing" is structurally certain, never for a preference.
     */
    data object NoWindow : TimingVerdict
}
