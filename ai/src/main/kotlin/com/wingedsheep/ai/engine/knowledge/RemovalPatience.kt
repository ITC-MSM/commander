package com.wingedsheep.ai.engine.knowledge

import com.wingedsheep.ai.engine.evaluation.BoardPresence
import com.wingedsheep.ai.engine.evaluation.ThreatAssessment
import com.wingedsheep.ai.engine.isOpponentTo
import com.wingedsheep.engine.mechanics.layers.ProjectedState
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.EntityId

/**
 * "Is this creature worth the removal spell?" — the half of the hold question a *window* cannot
 * answer.
 *
 * [HoldPolicy] asks whether this is the right moment; this asks whether this is the right target,
 * and it is the reason the AI would otherwise Pacifism the first 1/1 that shows up. A one-ply
 * evaluator scores the board right after the removal resolves, sees an opposing creature gone, and
 * has no term at all for the option the card *was*. The removal therefore fires at the first legal
 * target on the board, every game, whatever it is.
 *
 * ## Why the previous attempt failed, and what is different here
 *
 * Phase 6 built this as a **constant** — "instant removal in our own main phase, −2.0" — measured
 * it, and removed it. `HoldPolicy`'s own KDoc records the verdict: a penalty large enough to change
 * behaviour also vetoed casting a Disenchant at the one artifact on the table, which is the exact
 * play card knowledge exists to enable. The diagnosis in
 * `backlog/engine-ai-improvement.md` § Phase 6 is that holding removal is *a preference between two
 * futures*, and a constant cannot price one.
 *
 * That diagnosis is right about a constant and wrong about the question. The mistake is not "the AI
 * casts removal in its main phase" — it is "the AI casts removal at a **target that is not worth a
 * card**", which is a comparison, not a preference, and it has an answer that scales:
 *
 * > A removal spell should answer a creature at least as expensive as itself. The penalty is what
 * > the target is *short* of that bar, priced at the rate the evaluator already prices board value.
 *
 * A 1/1 under a Murder is 2.8 points short and takes a real penalty. A 3/3 is exactly fair and
 * takes none. A 6/4 is a bargain and takes none. Nothing here fires on a target the removal
 * genuinely wants, so the Disenchant that killed the constant is untouched twice over — it is not
 * a creature, and [discount] declines on non-creature targets by construction.
 *
 * ## The three releases
 *
 * Patience that never ends is just a dead card, so it ends four ways:
 *
 *  1. **We are dying.** [facingLethal] is a hard veto, not a discount: on a turn where doing
 *     nothing loses the game there is no bar at all. The evaluator would very probably have
 *     outvoted the discount here anyway — that is an argument about magnitudes, and this is a rule.
 *  2. **The hand is full.** At [com.wingedsheep.engine.core.MaximumHandSize.DEFAULT] cards the next
 *     draw is a discard, so holding stops being free and the discount goes to zero outright.
 *  3. **The game moves on.** Patience is a bet that a better target is coming, and the bet gets
 *     worse every turn — see [patienceFactor]. By [PATIENCE_SPENT_BY_TURN] the AI simply spends its
 *     removal.
 *  4. **The board outvotes it.** Short of lethal, the discount is still only a nudge in the
 *     evaluator's own units, capped by the removal's mana value, so a creature that is racing us or
 *     a blocker we need gone is priced by `ThreatAssessment` and `LifeDifferential` on a scale this
 *     cannot reach. That is what keeps the pressure cases below outright lethal honest without a
 *     second threshold to tune.
 *
 * Every number below is a hand-set prior, in the same sense as `CardIntent.staticPriorValue` — and
 * carried on [com.wingedsheep.ai.engine.AiProfile.holdRemovalForBetterTargets], so the arena can
 * price the whole idea at one flag.
 */
object RemovalPatience {

    /**
     * How much to subtract from a removal spell's leaf score for pointing at a target that is not
     * worth it yet, in raw evaluator units. `0.0` whenever the question does not apply — which is
     * most of the time, and every early return below says which case it is.
     *
     * @param card the removal spell being cast. Its [CardComponent.manaValue] sets the bar.
     * @param targets the *materialized* targets the AI would submit, not the requirement list.
     * @param boardPresenceWeight the profile's `EvaluationWeights.boardPresence`, so the discount is
     *   quoted in the same currency as the board value it is comparing against.
     */
    fun discount(
        state: GameState,
        playerId: EntityId,
        intent: CardIntent,
        card: CardComponent,
        targets: List<ChosenTarget>,
        boardPresenceWeight: Double,
    ): Double {
        // A sweeper's worth is the whole board it answers, and it names no targets to judge.
        if (IntentTag.SWEEPER in intent.tags) return 0.0
        // A fight's cost is a creature of ours taking the damage back, which the leaf score already
        // prices, and its reach is the fighter's power rather than anything about the card.
        if (IntentTag.FIGHT in intent.tags) return 0.0
        // A body with a removal rider (Flametongue Kavu) is not a card spent on the removal — you
        // keep the 4/2. Only a card whose *whole* purchase is the answer is making this trade.
        if (card.isCreature) return 0.0
        if (intent.tags.none { it in ANSWERED_BY_ONE_CARD }) return 0.0

        val projected = state.projectedState
        val victim = targets
            .filterIsInstance<ChosenTarget.Permanent>()
            .map { it.entityId }
            .filter { id -> projected.getController(id)?.let { state.isOpponentTo(it, playerId) } == true }
            // Exactly one, deliberately. A spell pointing at two of their permanents is answering a
            // board rather than making a trade, and one aimed at none of them is not removal being
            // spent at all (an Aura we are using as our own pump).
            .singleOrNull()
            ?: return 0.0

        // Creatures only. A creature is the thing a *better one* replaces next turn, which is the
        // entire bet being made here. An opposing artifact or enchantment is a fixed, already-visible
        // quantity — there is nothing better coming for the Disenchant to wait for, and penalizing it
        // is precisely the regression that killed the Phase 6 attempt (`noncreature-01`).
        if (!projected.isCreature(victim)) return 0.0

        if (facingLethal(state, projected, playerId)) return 0.0
        if (mustDiscardSoon(state, playerId)) return 0.0
        val patience = patienceFactor(state.turnNumber)
        if (patience <= 0.0) return 0.0

        val victimCard = state.getEntity(victim)?.get<CardComponent>() ?: return 0.0
        val worth = BoardPresence.permanentValue(state, projected, victim, victimCard)
        val fairTrade = FAIR_TRADE_VALUE_PER_MANA * card.manaValue
        return boardPresenceWeight * patience * (fairTrade - worth).coerceAtLeast(0.0)
    }

    /**
     * Whether the board as it stands kills [playerId] — the release that is a **guarantee** rather
     * than a nudge.
     *
     * Everything else in this file is a discount the evaluator is free to outvote, and on the
     * numbers it does: the discount is capped at `1.4 × manaValue × boardPresence` (about 6 for a
     * three-mana removal spell), while `ThreatAssessment` pays 10.0 raw for being dead on board and
     * `LifeDifferential` prices the life on top of that. So the AI was already going to cast here.
     *
     * That is an argument about magnitudes, and the rule it is standing in for is not: **the AI
     * must never sit on removal on a turn where doing nothing loses the game.** A magnitude
     * argument holds until someone refits the weights (Phase 9 is explicitly going to), and it
     * holds only for the profiles measured. A veto holds always, and it costs one boolean.
     *
     * [ThreatAssessment.lethalOnBoardAgainst] is the same predicate the evaluator's own `−10.0`
     * lethal term uses, so there is one definition of "they have lethal" rather than two that can
     * drift. It re-asks every turn, which is what makes the *narrow* reading sufficient: at 4 life
     * against a lone 2/2 nothing fires yet, and by the time the same board reads lethal — at 2 life
     * — the removal is released with a turn still in hand.
     */
    private fun facingLethal(state: GameState, projected: ProjectedState, playerId: EntityId): Boolean =
        ThreatAssessment.lethalOnBoardAgainst(state, projected, playerId)

    /**
     * Whether holding one more card costs [playerId] a discard.
     *
     * `>=` rather than `>`: at exactly the maximum, this turn's cleanup discards nothing but the
     * next draw step puts the hand over, so the card being weighed is already the one that will be
     * pitched. Waiting for the strict overflow would mean spending the removal a turn after the
     * decision stopped being free.
     *
     * [com.wingedsheep.engine.core.MaximumHandSize.DEFAULT] rather than `MaximumHandSize.effective`,
     * which needs a `CardRegistry` and two evaluators this policy has no reason to carry. The cost
     * of the simplification is that a Reliquary Tower makes the AI spend removal it could have kept
     * — the same constant, and the same trade, that `CardAdvantage.cardValue`'s "past 7 cards,
     * you're discarding anyway" branch already makes one file over.
     */
    private fun mustDiscardSoon(state: GameState, playerId: EntityId): Boolean =
        state.getZone(playerId, Zone.HAND).size >= com.wingedsheep.engine.core.MaximumHandSize.DEFAULT

    /**
     * How much of the bar still stands on turn [turnNumber], from `1.0` down to `0.0`.
     *
     * Patience is a bet that a better target is coming. Early it is a good bet — the opponent's
     * best card is still in their deck, and a removal spell held is a removal spell aimed. Late it
     * is a bad one twice over: there are fewer draws left to improve on what is already down, and
     * the mana that would have cast it has been idling for turns. So the bar decays rather than
     * switching off, and by [PATIENCE_SPENT_BY_TURN] the AI is simply spending its removal at
     * whatever is there.
     *
     * [GameState.turnNumber] counts **turns, not rounds** (`TurnManager` increments it on every
     * turn change), so these read as roughly "through round three" and "by round seven".
     */
    private fun patienceFactor(turnNumber: Int): Double = when {
        turnNumber <= PATIENCE_FULL_THROUGH_TURN -> 1.0
        turnNumber >= PATIENCE_SPENT_BY_TURN -> 0.0
        else -> (PATIENCE_SPENT_BY_TURN - turnNumber).toDouble() /
            (PATIENCE_SPENT_BY_TURN - PATIENCE_FULL_THROUGH_TURN)
    }

    /**
     * Board value of the creature a removal spell of mana value 1 should expect to trade with.
     *
     * Read straight off `BoardPresence.creatureValue`'s own scale rather than chosen: a vanilla
     * creature at its rate on the curve prices out at about this per mana — Grizzly Bears (2 mana
     * 2/2) 2.8, Hill Giant (4 mana 3/3) 4.2, Craw Wurm (6 mana 6/4) 7.6, Air Elemental (5 mana 4/4
     * flier) 8.3. So `manaValue × 1.4` is "a creature this spell's size", stated in the units the
     * evaluator already speaks, and the bar moves with the card: a Shock is content with a 1/1 and
     * a Murder is not.
     */
    const val FAIR_TRADE_VALUE_PER_MANA = 1.4

    /** Through this turn the bet on a better target is a good one, and the bar is at full height. */
    const val PATIENCE_FULL_THROUGH_TURN = 6

    /** From this turn on there is no bar at all — see [patienceFactor]. */
    const val PATIENCE_SPENT_BY_TURN = 14

    /** The intents that spend one whole card to answer one permanent. */
    private val ANSWERED_BY_ONE_CARD = setOf(
        IntentTag.REMOVAL, IntentTag.EXILE_REMOVAL, IntentTag.NEUTRALIZE,
    )
}
