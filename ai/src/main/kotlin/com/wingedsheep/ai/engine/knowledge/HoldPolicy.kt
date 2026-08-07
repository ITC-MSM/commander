package com.wingedsheep.ai.engine.knowledge

import com.wingedsheep.ai.engine.evaluation.EvaluationWeights
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.mechanics.layers.ProjectedState
import com.wingedsheep.engine.state.ComponentContainer
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.stack.AbilityOnStackComponent
import com.wingedsheep.engine.state.components.stack.ActivatedAbilityOnStackComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.state.components.stack.TargetsComponent
import com.wingedsheep.engine.state.components.stack.TriggeredAbilityOnStackComponent
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
class HoldPolicy(
    private val intents: IntentCatalog,
    /**
     * [AiProfile.combatTricksWaitForBlocks][com.wingedsheep.ai.engine.AiProfile.combatTricksWaitForBlocks]
     * — narrow the combat window to the steps where blocks are already in.
     */
    private val tricksWaitForBlocks: Boolean = false,
    /**
     * [AiProfile.holdRemovalForBetterTargets][com.wingedsheep.ai.engine.AiProfile.holdRemovalForBetterTargets]
     * — charge a removal spell for pointing at a target below a fair trade. See [RemovalPatience],
     * which is where the whole idea lives.
     */
    private val holdRemovalForBetterTargets: Boolean = false,
    /**
     * The profile's `EvaluationWeights.boardPresence`, so [RemovalPatience] can quote its discount
     * in the same currency as the board value it compares against. The default is the compiled
     * fallback's, which is what every profile that does not opt in would have used anyway.
     */
    private val boardPresenceWeight: Double = EvaluationWeights.DEFAULT.boardPresence,
) {

    /** Whether this policy can say anything. False when the agent has no card knowledge. */
    val isEnabled: Boolean get() = intents.isEnabled

    /**
     * The policy's reading of casting [cardName] here — the window it is in, plus what it is being
     * pointed at.
     *
     * The two halves are deliberately asymmetric about speed. **Windows** only judge instant-speed
     * cards: a sorcery has no window to wait for, and penalizing one would just make the AI pass
     * with a full hand. **[RemovalPatience]** judges any removal at all, because "is this creature
     * worth a card?" is the same question for a Pacifism as for a Doom Blade — and the Aura is the
     * case that motivated it.
     *
     * @param cast the materialized spell, when this action is one. Null for an activated ability,
     *   which spends no card and so is never charged for patience.
     */
    fun verdictFor(
        state: GameState,
        playerId: EntityId,
        cardName: String,
        cast: CastSpell? = null,
    ): TimingVerdict {
        val intent = intents.forName(cardName) ?: return TimingVerdict.Neutral

        val window = windowVerdictFor(state, playerId, intent)
        // A card that accomplishes nothing here is already floored below passing; there is no
        // target trade left to price on top of that.
        if (window is TimingVerdict.NoWindow) return window

        val patience = patienceFor(state, playerId, intent, cast)
        if (patience <= 0.0) return window

        val windowDelta = (window as? TimingVerdict.Adjust)?.delta ?: 0.0
        return TimingVerdict.Adjust(windowDelta - patience, reason = "patience")
    }

    /** The patience discount for [cast], or `0.0` when the flag is off or the shape doesn't fit. */
    private fun patienceFor(
        state: GameState,
        playerId: EntityId,
        intent: CardIntent,
        cast: CastSpell?,
    ): Double {
        if (!holdRemovalForBetterTargets || cast == null) return 0.0
        val card = state.getEntity(cast.cardId)?.get<CardComponent>() ?: return 0.0
        return RemovalPatience.discount(
            state, playerId, intent, card, cast.targets, boardPresenceWeight,
        )
    }

    /** The window half — "is this the moment?", which only an instant-speed card can get wrong. */
    private fun windowVerdictFor(state: GameState, playerId: EntityId, intent: CardIntent): TimingVerdict {
        if (intent.speed != Speed.INSTANT) return TimingVerdict.Neutral

        val stackHasSomething = state.stack.isNotEmpty()

        return when {
            // A counterspell with nothing to counter is not a play, it is a discard.
            IntentTag.COUNTERSPELL in intent.tags && !stackHasSomething -> TimingVerdict.NoWindow

            // A pump wears off at cleanup, so it is worth casting only when something will use it
            // before then: a fight in combat, or a spell on the stack it can save the creature
            // from — which is [responseWindowFor]'s question, not "is the stack non-empty".
            // Anywhere else — our own main phase, and specifically the opponent's end step, where
            // the old blanket `passScore - 1.5` discount actively *encouraged* dumping it — it buys
            // nothing at all.
            IntentTag.COMBAT_TRICK in intent.tags -> when {
                state.step in combatWindow -> TimingVerdict.Adjust(COMBAT_WINDOW)
                else -> responseWindowFor(state, playerId, intent)
            }

            // Instant-speed removal: reward the windows that are strictly better than now, and
            // charge nothing for casting it early.
            //
            // The symmetric *window* penalty — "hold it, our own main phase is the wrong time" — is
            // what the plan proposed, and it was built, measured and removed. Holding removal is a
            // preference between two futures, not a provable loss, and a constant cannot price one:
            // the one large enough to change behaviour was large enough to veto casting the removal
            // at all, which is the exact blindness this phase exists to fix.
            //
            // What survives that verdict is [RemovalPatience], and the difference is what it
            // charges *for*. Not the window — the **target**, by how far it falls short of a fair
            // trade, so a 1/1 is charged and the artifact the Disenchant is aimed at is not charged
            // at all. It is applied outside this `when`, to sorcery-speed removal too.
            intent.tags.any { it in REMOVAL_TAGS } -> when {
                stackHasSomething -> TimingVerdict.Adjust(RESPONSE_WINDOW)
                state.activePlayerId != playerId && state.step == Step.END ->
                    TimingVerdict.Adjust(END_STEP_WINDOW)

                else -> TimingVerdict.Neutral
            }

            else -> TimingVerdict.Neutral
        }
    }

    /**
     * What a spell already on the stack is worth to a pump — the "last chance" window.
     *
     * A stack object is not by itself a reason to cast a trick. The bonus was flat for any
     * non-empty stack, which paid the AI to answer a Murder with Giant Growth (a 5/5 is destroyed
     * exactly as fast as a 2/2) and to pump a 6/4 that was already walking off a Bolt. Both are
     * `lastchance` puzzles, and both are the *same* mistake: reading "there is a deadline" as "this
     * card meets it".
     *
     * So the window has to be earned. It is real when something on the stack would kill a creature
     * we control **by size** — damage, or -N/-N — and the extra toughness carries it out of range.
     * That is one comparison, and it separates the three positions the suite pairs: Bolt on a 2/2
     * (dying, +3/+3 saves it → window), Bolt on a 6/4 (not dying → nothing to buy), Murder on
     * anything (no reach, so no amount of toughness answers it).
     *
     * **Silence is not a veto.** [TimingVerdict.NoWindow] floors the candidate below passing, which
     * is only honest where "does nothing" is structurally certain, so a stack object this policy
     * cannot read — an unknown card, or a fight, whose reach is the other creature's power and not
     * a property of the card — keeps the old bonus rather than earning a veto. Which makes it
     * load-bearing that [threatOn] reads *abilities* too, since a trigger on the stack is the
     * commonest stack object there is.
     */
    private fun responseWindowFor(state: GameState, playerId: EntityId, pump: CardIntent): TimingVerdict {
        val projected = state.projectedState
        var unreadable = false

        for (stackId in state.stack) {
            val container = state.getEntity(stackId) ?: continue
            val threat = threatOn(container)
            if (threat == null || IntentTag.FIGHT in threat.tags) {
                unreadable = true
                continue
            }

            // A sweeper names no targets; everything we control is under it.
            val victims = if (IntentTag.SWEEPER in threat.tags) {
                projected.getBattlefieldControlledBy(playerId)
            } else {
                container.get<TargetsComponent>()?.targets.orEmpty()
                    .mapNotNull { (it as? ChosenTarget.Permanent)?.entityId }
                    .filter { projected.getController(it) == playerId }
            }

            // Null reach on a card we *did* read is destruction, exile or bounce — see
            // [CardIntent.removalReach]. Toughness is no defence against any of them.
            val reach = threat.removalReach ?: continue
            if (victims.any { savedByPump(state, projected, it, reach, pump) }) {
                return TimingVerdict.Adjust(RESPONSE_WINDOW)
            }
        }

        return if (unreadable) TimingVerdict.Adjust(RESPONSE_WINDOW) else TimingVerdict.NoWindow
    }

    /**
     * What the stack object [container] threatens, or null when nothing here can be read.
     *
     * A spell is its card. An **ability** is its effect, which the stack object carries itself —
     * it has no [CardComponent] at all — so it has to be read from there rather than from the
     * permanent that produced it, which is a different and much broader question.
     *
     * Reading abilities is what keeps [responseWindowFor]'s "silence is not a veto" fallback
     * honest. Without it every ability on the stack fell through as unreadable and bought a trick
     * the full response bonus, so an ETB token trigger — or a "you may pay {B} to transform this"
     * on the opponent's own main phase — paid the AI to dump a combat trick in a window where the
     * pump provably wears off before any combat.
     */
    private fun threatOn(container: ComponentContainer): CardIntent? {
        val ability = container.get<TriggeredAbilityOnStackComponent>()?.effect
            ?: container.get<ActivatedAbilityOnStackComponent>()?.effect
            ?: container.get<AbilityOnStackComponent>()?.effect
        if (ability != null) return intents.forEffect(ability)
        return container.get<CardComponent>()?.name?.let { intents.forName(it) }
    }

    /**
     * Whether [creature] is dying to [reach] damage and [pump] is enough to change that.
     *
     * Both halves are load-bearing: a creature already out of range buys nothing (there is nothing
     * to save), and neither does one the pump cannot lift out of range (a +1/+0 trick against three
     * damage). Damage already marked counts, because that is what the state-based action will
     * compare against (CR 704.5g).
     */
    private fun savedByPump(
        state: GameState,
        projected: ProjectedState,
        creature: EntityId,
        reach: Int,
        pump: CardIntent,
    ): Boolean {
        if (!projected.isCreature(creature)) return false
        val toughness = projected.getToughness(creature) ?: return false
        val remaining = toughness - (state.getEntity(creature)?.get<DamageComponent>()?.amount ?: 0)
        return remaining <= reach && remaining + pump.pumpToughness > reach
    }

    /**
     * The steps where a combat trick is worth its bonus.
     *
     * [COMBAT_STEPS] is the historical answer and it is one window too wide: it pays the trick in
     * `BEGIN_COMBAT` and `DECLARE_ATTACKERS`, both of which are *before* blocks. Spending a trick
     * there is worse than spending it late for a reason no board evaluation can see — it hands the
     * defender the information. A 2/2 that would have gone unblocked gets chump-blocked once it is
     * visibly a 5/5, and the pump that was exactly lethal buys nothing.
     *
     * [BLOCKS_IN_STEPS] is the constant matching [COMBAT_WINDOW]'s own comment. Every step in it is
     * one where blocks are already declared, whichever side we are on: on our turn we only receive
     * priority in `DECLARE_BLOCKERS` after the defender has declared, and on theirs we are the one
     * who just declared. Before that a trick falls through to [responseWindowFor], which pays it
     * only for something on the stack it can actually answer — so "hold it until blocks are in"
     * costs the AI no legitimate response.
     */
    private val combatWindow: Set<Step> get() = if (tricksWaitForBlocks) BLOCKS_IN_STEPS else COMBAT_STEPS

    private companion object {
        /** Blockers are in; a trick decides the fight. */
        const val COMBAT_WINDOW = 1.0

        /** Something on the stack this card can actually answer — see [responseWindowFor]. */
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

        /** [COMBAT_STEPS] minus the two windows that come before blocks are declared. */
        val BLOCKS_IN_STEPS = setOf(
            Step.DECLARE_BLOCKERS, Step.FIRST_STRIKE_COMBAT_DAMAGE, Step.COMBAT_DAMAGE,
        )

        val REMOVAL_TAGS = setOf(IntentTag.REMOVAL, IntentTag.EXILE_REMOVAL, IntentTag.SWEEPER)
    }
}

/** What [HoldPolicy] makes of casting a particular card in a particular window. */
sealed interface TimingVerdict {
    /** Timing says nothing here; the board score stands as it is. */
    data object Neutral : TimingVerdict

    /**
     * A nudge in the board score's own units.
     *
     * [reason] is for the local testing mode's decision panel and nothing else: a candidate the AI
     * passed over despite a strong board score is only explicable if the panel can name which half
     * of the policy moved it. Null means the window half, which is what an `Adjust` used to be.
     */
    data class Adjust(val delta: Double, val reason: String? = null) : TimingVerdict

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
