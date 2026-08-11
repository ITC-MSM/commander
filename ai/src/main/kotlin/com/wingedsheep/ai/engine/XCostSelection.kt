package com.wingedsheep.ai.engine

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.legalactions.LegalAction
import com.wingedsheep.engine.legalactions.TargetInfo
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.sdk.model.EntityId

/**
 * Choosing a value for `{X}`.
 *
 * The legal-action enumerator cannot pick X for the AI: X is announced as part of casting
 * (CR 601.2b), so enumeration runs *before* it exists. It hands over the raw materials instead —
 * [LegalAction.maxAffordableX], [LegalAction.minX], and a deliberately permissive
 * [LegalAction.validTargets] — and leaves the choice to whoever is casting. A human makes it in the
 * client's X-selection phase; this is the AI's equivalent.
 *
 * The output is one fully-consistent [LegalAction] per X worth considering, so nothing downstream
 * has to know X was ever open: [Strategist] simulates and scores them like any other candidate, and
 * [TargetSelection] picks targets from a list already narrowed to what is legal at that X.
 *
 * Two things this is deliberately *not*:
 *
 * - **Not a scorer.** It proposes X values; simulation decides between them. The only judgement
 *   here is which handful are worth a simulation, because the affordable range can be a dozen wide
 *   and each candidate costs a full simulation.
 * - **Not a target picker.** It narrows `validTargets` to what the chosen X permits and leaves the
 *   choice among the survivors to [TargetSelection], exactly as the server leaves it to the client.
 */
object XCostSelection {

    /**
     * Cap on how many X values one action is expanded into. Each is a simulation in the
     * Strategist's first pass, so the affordable range is sampled rather than swept.
     */
    const val MAX_X_CANDIDATES = 5

    /**
     * The X values worth simulating for [action], best-first.
     *
     * Two shapes, because "what is a good X" has two different answers:
     *
     * - **X gates which targets are legal** ("mana value X or less", "mana value X", "power X" —
     *   Repeal, Spell Blast, Ent-Draught Basin). X is a function of the target, not a free choice,
     *   so the candidates are exactly the values that make some currently-legal target legal.
     *   Sweeping the affordable range here would mostly generate X values no target matches,
     *   spending the candidate budget on casts that cannot be made.
     * - **X is free of the targets** (Fireball, Genesis Wave, Day of Black Sun, and "up to X target
     *   creatures", where X caps the count rather than gating legality). More X is more effect, so
     *   the top affordable values are the interesting ones.
     *
     * Returns an empty list when no X can legally be chosen — the caller's signal to drop the
     * action rather than submit it at the enumerator's implicit X=0.
     */
    fun candidateXValues(state: GameState, action: LegalAction): List<Int> {
        val maxX = action.maxAffordableX ?: return emptyList()
        val minX = action.minX.coerceAtLeast(0)
        if (maxX < minX) return emptyList()

        targetGatedXValues(state, action)?.let { gated ->
            return gated.filter { it in minX..maxX }.take(MAX_X_CANDIDATES)
        }

        // A free X of 0 is the enumerator's own default and buys nothing, so the sweep starts at 1
        // unless the card forbids it ("X can't be 0" raises `minX`).
        val lowest = maxOf(minX, 1)
        if (maxX < lowest) return emptyList()
        return (maxX downTo maxOf(lowest, maxX - MAX_X_CANDIDATES + 1)).toList()
    }

    /**
     * Bind the single best-looking X to [action] — the caller-picks-one form of
     * [candidateXValues] + [narrowToX], for a caller that cannot afford to simulate the
     * alternatives.
     *
     * That caller is [com.wingedsheep.ai.engine.rollout.PlayoutPolicy]: a playout has already
     * sampled *which* action to take and needs a defensible X for it, and simulating inside a
     * playout is what would make the playout quadratic. Taking the head of the candidate list is
     * exactly the heuristic floor every other choice in a playout uses.
     *
     * Returns [action] unchanged when it has no X to bind, or when no candidate X survives
     * narrowing — the caller has already committed to this action, so an unbound X (which the
     * engine reads as 0) beats returning nothing.
     */
    fun bindBestX(state: GameState, action: LegalAction): LegalAction {
        if (!action.hasXCost) return action
        for (x in candidateXValues(state, action)) {
            narrowToX(state, action, x)?.let { return it.withXValue(x) }
        }
        return action
    }

    /** This action with [x] written into whichever X-carrying `GameAction` shape it wraps. */
    private fun LegalAction.withXValue(x: Int): LegalAction = when (val base = action) {
        is CastSpell -> copy(action = base.copy(xValue = x))
        is ActivateAbility -> copy(action = base.copy(xValue = x))
        else -> this
    }

    /**
     * Re-derive [action] as it would look with X bound to [x]: X-gated target lists narrowed to
     * what is legal, and an X-driven target cap resolved to the real number.
     *
     * This mirrors what the web client does once the player picks X (`pipelinePhases.ts`'s
     * `applyXFilters` / `resolveMaxByX`) — the enumerator is permissive on purpose, and whoever
     * binds X owes the narrowing. Without it the AI would choose a target the server then rejects.
     *
     * Returns null when the narrowing leaves a mandatory requirement with nothing to target, i.e.
     * this X cannot legally be cast at all.
     */
    fun narrowToX(state: GameState, action: LegalAction, x: Int): LegalAction? {
        var narrowed = action

        action.targetRequirements?.let { requirements ->
            narrowed = narrowed.copy(
                targetRequirements = requirements.map { requirement ->
                    narrowRequirement(state, requirement, x) ?: return null
                }
            )
        }

        action.validTargets?.let { targets ->
            val flat = narrowRequirement(state, flatRequirement(action, targets), x) ?: return null
            narrowed = narrowed.copy(
                validTargets = flat.validTargets,
                targetCount = flat.maxTargets,
                // The action's own minimum, only ever clamped down by an X-driven cap. The
                // requirement view raises it to 1 for a `requiresTargets` action so the narrowing
                // treats an emptied list as fatal; that floor is a local device and must not leak
                // back out as a stricter minimum than the enumerator declared.
                minTargets = minOf(action.minTargets, flat.maxTargets),
            )
        }

        return narrowed
    }

    /**
     * Narrow one requirement to [x], or null when doing so makes it unsatisfiable.
     *
     * A requirement is unsatisfiable when it *must* be filled ([TargetInfo.minTargets] > 0) and
     * either nothing legal survives the filter or the X-driven cap has fallen below the minimum.
     * A requirement that may be left empty ("up to X target creatures") is never fatal — casting it
     * for nothing is legal, and the Strategist scores that against passing like any other line.
     */
    private fun narrowRequirement(state: GameState, requirement: TargetInfo, x: Int): TargetInfo? {
        val valid = filterByX(
            state, requirement.validTargets, x,
            atMostManaValue = requirement.xConstrainsManaValue,
            exactManaValue = requirement.xConstrainsManaValueExactly,
            exactPower = requirement.xConstrainsPower,
        )
        // An X-driven cap *replaces* the enumerator's placeholder rather than clamping it: at
        // enumeration time the count could not be resolved, so the static value carries no
        // information (see LegalAction.targetCount).
        val maxTargets = if (requirement.xConstrainsCount) x else requirement.maxTargets
        if (requirement.minTargets > 0 && (valid.isEmpty() || maxTargets < requirement.minTargets)) {
            return null
        }
        return requirement.copy(
            validTargets = valid,
            maxTargets = maxTargets,
            minTargets = minOf(requirement.minTargets, maxTargets),
        )
    }

    /**
     * The single-requirement shape ([LegalAction.validTargets] plus the flat `xConstrains*` fields)
     * expressed as a [TargetInfo], so one set of rules covers both shapes.
     */
    private fun flatRequirement(action: LegalAction, targets: List<EntityId>): TargetInfo = TargetInfo(
        index = 0,
        description = action.targetDescription ?: "",
        minTargets = if (action.requiresTargets) maxOf(action.minTargets, 1) else action.minTargets,
        maxTargets = action.targetCount,
        validTargets = targets,
        xConstrainsManaValue = action.xConstrainsTargetManaValue,
        xConstrainsManaValueExactly = action.xConstrainsTargetManaValueExactly,
        xConstrainsPower = action.xConstrainsTargetPower,
        xConstrainsCount = action.xConstrainsTargetCount,
    )

    /**
     * The X values that make some currently-legal target legal, best target first — or null when no
     * requirement gates target legality on X, which tells [candidateXValues] to sweep instead.
     *
     * For "mana value X **or less**" the candidate is the target's own mana value: any larger X hits
     * the same permanent for more mana, so it is dominated. For the equality forms the candidate is
     * simply the value that matches.
     *
     * Ordered by descending value (the biggest thing X could reach first) because the list is
     * truncated to [MAX_X_CANDIDATES]; simulation, not this order, decides what is actually cast.
     */
    private fun targetGatedXValues(state: GameState, action: LegalAction): List<Int>? {
        val requirements = allRequirements(action)
        if (requirements.none { it.xConstrainsManaValue || it.xConstrainsManaValueExactly || it.xConstrainsPower }) {
            return null
        }
        return requirements
            .flatMap { requirement ->
                requirement.validTargets.mapNotNull { id ->
                    when {
                        requirement.xConstrainsManaValue || requirement.xConstrainsManaValueExactly ->
                            manaValueOf(state, id)
                        requirement.xConstrainsPower -> state.projectedState.getPower(id)
                        else -> null
                    }
                }
            }
            .distinct()
            .sortedDescending()
    }

    /** Every requirement of [action], in either shape, as one list. */
    private fun allRequirements(action: LegalAction): List<TargetInfo> =
        action.targetRequirements.orEmpty() +
            (action.validTargets?.let { listOf(flatRequirement(action, it)) } ?: emptyList())

    private fun filterByX(
        state: GameState,
        ids: List<EntityId>,
        x: Int,
        atMostManaValue: Boolean,
        exactManaValue: Boolean,
        exactPower: Boolean,
    ): List<EntityId> {
        if (!atMostManaValue && !exactManaValue && !exactPower) return ids
        return ids.filter { id ->
            (!atMostManaValue || (manaValueOf(state, id) ?: return@filter false) <= x) &&
                (!exactManaValue || manaValueOf(state, id) == x) &&
                // Power is a projected characteristic — a lord's +1/+1 changes what "power X" can
                // reach, so this must not read the printed stats.
                (!exactPower || state.projectedState.getPower(id) == x)
        }
    }

    private fun manaValueOf(state: GameState, id: EntityId): Int? =
        state.getEntity(id)?.get<CardComponent>()?.manaValue
}
