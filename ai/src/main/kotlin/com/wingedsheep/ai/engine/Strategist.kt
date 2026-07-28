package com.wingedsheep.ai.engine

import com.wingedsheep.ai.engine.advisor.CardAdvisorRegistry
import com.wingedsheep.ai.engine.advisor.CastContext
import com.wingedsheep.ai.engine.budget.BudgetPolicy
import com.wingedsheep.ai.engine.budget.DecisionBudget
import com.wingedsheep.ai.engine.budget.LegacyBudgetPolicy
import com.wingedsheep.ai.engine.evaluation.BoardEvaluator
import com.wingedsheep.ai.engine.evaluation.BoardPresence
import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.legalactions.LegalAction
import com.wingedsheep.engine.legalactions.MeaningfulActionFilter
import com.wingedsheep.engine.legalactions.TargetInfo
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.sdk.core.Format
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.EntityId

/**
 * Chooses which [LegalAction] to take when the AI has priority.
 *
 * **Greedy 1-ply**: every candidate is scored with a single simulation and the max wins.
 *
 * There used to be a second, multi-ply alpha-beta pass here (`Searcher`). It was
 * unreachable — its `recommendDepth` gated on "can the opponent respond?", which opened
 * with `state.priorityPlayerId != playerId` and so was always false on our own priority —
 * and it carried a `Double.MIN_VALUE / 2` "−∞" sentinel that is actually `0.0`. It was
 * deleted rather than repaired; the replacement is the rollout evaluator in Phase 7 of
 * `backlog/engine-ai-improvement.md`, which plugs in at [evaluate1Ply]'s leaf score.
 *
 * Combat decisions are delegated to [CombatAdvisor].
 * Card-specific overrides are handled by [CardAdvisorRegistry].
 */
class Strategist(
    private val simulator: GameSimulator,
    private val evaluator: BoardEvaluator,
    private val combatAdvisor: CombatAdvisor = CombatAdvisor(simulator, evaluator),
    private val advisorRegistry: CardAdvisorRegistry = CardAdvisorRegistry(),
    /**
     * Phase 4a: only propose actions the AI can actually take.
     *
     * Two halves. Candidates come from [MeaningfulActionFilter] instead of the ad-hoc
     * `affordable && !isManaAbility` filter, so a spell whose mandatory target slot is empty stops
     * being a candidate; and [fillableRequirements] fills the slots it *can* rather than
     * abandoning the whole spell. Together they close the "889 of 945 rejected actions were
     * `CastSpell: No valid targets available`" finding Phase 1 quantified and left open.
     */
    private val useMeaningfulFilter: Boolean = false,
    /** Phase 4b. How much search each decision may spend. */
    private val budgetPolicy: BudgetPolicy = LegacyBudgetPolicy,
) {
    fun chooseAction(
        state: GameState,
        legalActions: List<LegalAction>,
        playerId: EntityId
    ): LegalAction {
        // Combat declaration steps need the CombatAdvisor to fill in attacker/blocker maps
        // even when there's only one legal action (which is the common case — the enumerator
        // returns a single DeclareAttackers/DeclareBlockers with an empty default map).
        val combatAction = legalActions.find { it.actionType == "DeclareAttackers" || it.actionType == "DeclareBlockers" }
        if (combatAction != null) {
            val budget = budgetPolicy.budgetFor(state, playerId, listOf(combatAction))
            return handleCombatDeclaration(state, combatAction, playerId, budget)
        }

        if (legalActions.size == 1) return legalActions.first()

        val pass = legalActions.find { it.actionType == "PassPriority" }
        val affordable = expandXCostAbilities(state, preferKickerVariants(candidatesFrom(legalActions)), playerId)

        if (affordable.isEmpty()) return pass ?: legalActions.first()

        val budget = budgetPolicy.budgetFor(state, playerId, affordable)

        // ── 1-ply scoring of all candidates ──
        val passScore = if (pass != null) {
            evaluate1Ply(state, pass, playerId, budget = budget)
        } else {
            evaluator.evaluate(state, state.projectedState, playerId)
        }

        // The anytime contract: the first candidate is always scored, and the budget only cuts the
        // tail short. `maxByOrNull` over a partial list is still a valid (if worse) answer.
        val scored = mutableListOf<Pair<LegalAction, Double>>()
        for (action in affordable) {
            scored += action to evaluate1Ply(state, action, playerId, passScore, budget)
            if (budget.expired()) break
        }

        // On the opponent's end step, unspent mana is about to be wasted.
        // Reduce the pass threshold so the AI is more willing to use instants
        // rather than letting mana evaporate.
        val adjustedPassScore = if (state.activePlayerId != playerId && state.step == Step.END) {
            passScore - 1.5
        } else {
            passScore
        }

        val best = scored.maxByOrNull { it.second }
        return if (best != null && best.second > adjustedPassScore) {
            // Fill in targets on the returned action so the processor can execute it.
            // The committed target is chosen by simulation (not just the heuristic) so the
            // AI sees the real resolved board, including effects already on the stack.
            val chosen = best.first
            if (chosen.requiresTargets) {
                chosen.copy(action = chooseCommittedTargets(state, chosen, playerId, budget))
            } else {
                chosen
            }
        } else {
            pass ?: legalActions.first()
        }
    }

    /**
     * The candidate actions worth scoring.
     *
     * Mana abilities are excluded either way: activating one on its own is never the AI's move —
     * mana is produced as part of paying for something, by the engine's own auto-tap.
     */
    private fun candidatesFrom(legalActions: List<LegalAction>): List<LegalAction> =
        if (useMeaningfulFilter) {
            MeaningfulActionFilter.filterMeaningful(legalActions).filter { it.affordable && !it.isManaAbility }
        } else {
            legalActions.filter { it.affordable && !it.isManaAbility && it.actionType != "PassPriority" }
        }

    private fun handleCombatDeclaration(
        state: GameState,
        legalAction: LegalAction,
        playerId: EntityId,
        budget: DecisionBudget,
    ): LegalAction {
        val action = when (legalAction.actionType) {
            "DeclareAttackers" -> combatAdvisor.chooseAttackers(state, legalAction, playerId, budget)
            "DeclareBlockers" ->
                combatAdvisor.chooseBlockers(state, legalAction, playerId, useSimulation = true, budget = budget)
            else -> legalAction.action
        }
        return legalAction.copy(action = action)
    }

    private fun evaluate1Ply(
        state: GameState,
        action: LegalAction,
        playerId: EntityId,
        passScore: Double? = null,
        budget: DecisionBudget = DecisionBudget.legacy(),
    ): Double {
        // For targeted spells, fill in the best target by simulation before scoring. Without a
        // target the CastSpellHandler rejects the action ("No valid targets") and the spell always
        // scores the same as passing; with only a *heuristic* target, an ability whose ideal target
        // is already taken by something on the stack (e.g. a second "can't block" when the first is
        // still resolving) would be scored at that redundant target and look worthless — so the AI
        // would never play it. Scoring at the best target makes the action's real upside visible.
        val simulationAction = chooseCommittedTargets(state, action, playerId, budget)
        val result = simulator.simulate(state, simulationAction)
        val defaultScore = evaluator.evaluate(result.state, result.state.projectedState, playerId)

        // Check for card-specific advisor override
        val cardName = resolveCardName(state, action) ?: return defaultScore
        val advisor = advisorRegistry.getAdvisor(cardName) ?: return defaultScore
        val context = CastContext(
            state = state,
            projected = state.projectedState,
            playerId = playerId,
            action = action,
            passScore = passScore ?: evaluator.evaluate(state, state.projectedState, playerId),
            defaultScore = defaultScore,
            evaluator = evaluator,
            simulator = simulator
        )
        return advisor.evaluateCast(context) ?: defaultScore
    }

    /**
     * For spells/abilities that require target selection, fill in heuristic
     * targets so the simulation can actually resolve the spell.
     *
     * Multi-target spells: for each requirement, pick the highest-value
     * opponent creature (or lowest-value own creature, depending on context).
     * Single-target spells: pick the best target by creature value.
     *
     * This is the cheap path used while *scoring* candidate actions (one heuristic target
     * per requirement, no extra simulation). The action the AI actually commits routes through
     * [chooseCommittedTargets], which refines the target by simulation.
     */
    private fun resolveTargetsForSimulation(
        state: GameState,
        action: LegalAction,
        playerId: EntityId
    ): com.wingedsheep.engine.core.GameAction {
        if (!action.requiresTargets) return action.action
        // Only CastSpell and ActivateAbility carry a `targets` list the AI fills in. A targeted
        // activated ability (e.g. "{4}{R}, Sacrifice: deal 3 damage to target") that isn't handled
        // here is submitted with no target, rejected by the engine ("requires a target"), and the
        // AI re-picks it forever — an infinite loop.
        val baseAction = action.action
        if (targetsAlreadyFilled(baseAction) != false) return action.action
        val targetInfos = fillableRequirements(action) ?: return action.action

        val chosenTargets = targetInfos.map { info ->
            val best = info.validTargets.maxByOrNull { heuristicTargetRank(state, it, playerId) }
                ?: info.validTargets.first()
            toChosenTarget(state, info, best, playerId)
        }
        return applyTargets(baseAction, chosenTargets)
    }

    /**
     * Pick the targets the AI actually commits to for a chosen targeted action, by simulation
     * rather than the static [heuristicTargetRank]. Simulating each candidate resolves the stack —
     * including spells/abilities already on it — so the evaluator scores the *real* board.
     *
     * This is what stops the classic blunder of aiming two "target creature can't block" effects
     * at the same creature: while the first is still on the stack the heuristic sees that creature
     * at full value and re-picks it, but a simulation that resolves both effects shows re-hitting it
     * gains nothing over neutralizing a second, still-able blocker (which [BoardPresence] now prices
     * lower). Requirements are resolved greedily — others held at their heuristic best — and only
     * the top `budget.allowances.targetCandidates` per requirement are simulated to bound cost. A
     * budget below [com.wingedsheep.ai.engine.budget.BudgetTier.NORMAL] skips the refinement
     * entirely and keeps the heuristic pick: this loop is the most expensive thing a routine
     * priority window can pay for. Falls back to [resolveTargetsForSimulation] when the action
     * carries no usable target metadata.
     */
    private fun chooseCommittedTargets(
        state: GameState,
        action: LegalAction,
        playerId: EntityId,
        budget: DecisionBudget = DecisionBudget.legacy(),
    ): com.wingedsheep.engine.core.GameAction {
        val baseAction = action.action
        if (targetsAlreadyFilled(baseAction) != false) return baseAction
        if (!budget.allowances.refineTargetsBySimulation) {
            return resolveTargetsForSimulation(state, action, playerId)
        }
        val targetInfos = fillableRequirements(action)
            ?: return resolveTargetsForSimulation(state, action, playerId)

        // Heuristic baseline for every requirement, then refine each one by simulation.
        val chosenTargets = targetInfos.map { info ->
            val best = info.validTargets.maxByOrNull { heuristicTargetRank(state, it, playerId) }
                ?: info.validTargets.first()
            toChosenTarget(state, info, best, playerId)
        }.toMutableList()

        for (i in targetInfos.indices) {
            if (budget.expired()) break
            val info = targetInfos[i]
            val candidates = info.validTargets
                .sortedByDescending { heuristicTargetRank(state, it, playerId) }
                .take(budget.allowances.targetCandidates)
            if (candidates.size <= 1) continue
            val best = candidates.maxByOrNull { candidate ->
                val trial = chosenTargets.toMutableList()
                trial[i] = toChosenTarget(state, info, candidate, playerId)
                val result = simulator.simulate(state, applyTargets(baseAction, trial))
                evaluator.evaluate(result.state, result.state.projectedState, playerId)
            } ?: continue
            chosenTargets[i] = toChosenTarget(state, info, best, playerId)
        }
        return applyTargets(baseAction, chosenTargets)
    }

    /**
     * Whether [baseAction]'s targets are already filled. `null` = the action type carries no
     * AI-filled target list (only CastSpell / ActivateAbility do), `true`/`false` otherwise.
     */
    private fun targetsAlreadyFilled(baseAction: com.wingedsheep.engine.core.GameAction): Boolean? =
        when (baseAction) {
            is CastSpell -> baseAction.targets.isNotEmpty()
            is ActivateAbility -> baseAction.targets.isNotEmpty()
            else -> null
        }

    /**
     * The target requirements the AI will actually fill, or null when it cannot build a legal
     * target list at all and should leave the action's targets alone.
     *
     * Targets are submitted as one **flat** list, which the engine slices back into requirements
     * by their max counts (`TargetValidator.validateTargets`). An unfilled slot can therefore only
     * ever be a trailing one — there is no way to say "requirement 0 got nothing, requirement 1
     * got this".
     *
     * That mattered more than it sounds. V0 bails on the *whole spell* the moment any requirement
     * has no legal target, and then submits no targets at all, which the engine rejects with "No
     * valid targets available" — Phase 1 measured that as ~0.9 rejected actions per game, 889 of
     * 945 rejections. The shape behind almost all of them is an **optional** trailing slot:
     * Conduct Electricity's "up to one target creature token" with no token on the board makes the
     * AI decline to target the mandatory creature either.
     *
     * Behind [useMeaningfulFilter] with the rest of "only propose actions you can actually take" —
     * not because the old behaviour is defensible, but because `AiProfile.LEGACY_V0` is the
     * permanent reference opponent that every published number is quoted against, and quietly
     * making it stronger would silently rebase months of arena results.
     */
    private fun fillableRequirements(action: LegalAction): List<TargetInfo>? {
        val all = targetInfosFor(action) ?: return null
        val fillable = all.takeWhile { it.validTargets.isNotEmpty() }
        if (fillable.size == all.size) return all
        if (!useMeaningfulFilter) return null
        val unfilled = all.drop(fillable.size)
        // A mandatory slot with no legal target means the spell cannot be cast at all, and a
        // later slot that *does* have targets cannot be reached past a skipped one.
        if (unfilled.any { it.minTargets > 0 || it.validTargets.isNotEmpty() }) return null
        return fillable
    }

    /** Normalize an action's target metadata into requirements (multi-target or single-target). */
    private fun targetInfosFor(action: LegalAction): List<TargetInfo>? =
        action.targetRequirements
            ?: action.validTargets?.let { targets ->
                listOf(TargetInfo(
                    index = 0,
                    description = action.targetDescription ?: "",
                    minTargets = action.minTargets,
                    maxTargets = action.targetCount,
                    validTargets = targets,
                    targetZone = null
                ))
            }

    /** Heuristic desirability of a target: higher = better. Opponent removal targets rank highest. */
    private fun heuristicTargetRank(state: GameState, entityId: EntityId, playerId: EntityId): Double {
        val projected = state.projectedState
        val controller = projected.getController(entityId)
        // CR 810 — a teammate's permanent is not an opponent's, so removal must not rank it as
        // one. In a game without teams this is exactly the old `controller != playerId`.
        val isOpponent = controller != null && state.isOpponentTo(controller, playerId)
        val isPlayer = state.getEntity(entityId)
            ?.get<com.wingedsheep.engine.state.components.identity.PlayerComponent>() != null

        return if (isPlayer) {
            // Player target — prefer opponent
            if (isOpponent) 5.0 else -5.0
        } else if (projected.isCreature(entityId)) {
            val card = state.getEntity(entityId)?.get<CardComponent>()
            val value = if (card != null) {
                BoardPresence.permanentValue(state, projected, entityId, card)
            } else 0.0
            // Opponent creatures: higher value = better target for removal
            // Own creatures: higher value = better target for pump/bite source
            if (isOpponent) value + 10.0 else -value
        } else {
            0.0
        }
    }

    /** Build the right [ChosenTarget] variant for [entityId] given the requirement's zone. */
    private fun toChosenTarget(
        state: GameState,
        info: TargetInfo,
        entityId: EntityId,
        playerId: EntityId
    ): ChosenTarget = when (info.targetZone) {
        "GRAVEYARD" -> {
            val ownerId = state.getEntity(entityId)
                ?.get<com.wingedsheep.engine.state.components.identity.OwnerComponent>()?.playerId
                ?: playerId
            ChosenTarget.Card(entityId, ownerId, Zone.GRAVEYARD)
        }
        "STACK" -> ChosenTarget.Spell(entityId)
        else -> {
            // `targetZone` is only populated for multi-requirement spells; a single-target
            // spell/ability (Reprieve, or Sandman's "target land card from your graveyard")
            // surfaces `validTargets` with `targetZone = null`. So fall back to authoritative
            // game state and build the variant the target's actual zone demands:
            //  - a spell on the stack must become a `ChosenTarget.Spell`, not a `Permanent`
            //    (else the engine rejects the cast, "Target must be a spell on the stack");
            //  - a card in a non-battlefield zone (graveyard/exile/hand/library/command) must
            //    become a `ChosenTarget.Card` carrying that zone, not a `Permanent` (else the
            //    engine rejects it — e.g. Sandman's graveyard land — and the AI re-picks the
            //    same failing activation forever).
            // Mirrors the web client's target-payload builder (pipelinePhases.ts).
            val isSpell = state.isSpellOnStack(entityId)
            val isPlayer = state.getEntity(entityId)
                ?.get<com.wingedsheep.engine.state.components.identity.PlayerComponent>() != null
            val cardZone = zoneOfCardTarget(state, entityId)
            when {
                isSpell -> ChosenTarget.Spell(entityId)
                isPlayer -> ChosenTarget.Player(entityId)
                cardZone != null -> {
                    val ownerId = state.getEntity(entityId)
                        ?.get<com.wingedsheep.engine.state.components.identity.OwnerComponent>()?.playerId
                        ?: cardZone.ownerId
                    ChosenTarget.Card(entityId, ownerId, cardZone.zoneType)
                }
                else -> ChosenTarget.Permanent(entityId)
            }
        }
    }

    /**
     * The [ZoneKey] of [entityId] when it is a card in a non-battlefield "card target" zone
     * (graveyard, exile, hand, library, command) — the zones a `ChosenTarget.Card` addresses.
     * Returns `null` for battlefield permanents and the stack, which are handled by the
     * `Permanent`/`Spell` variants. Mirrors the client's `CARD_TARGET_ZONES` set.
     */
    private fun zoneOfCardTarget(state: GameState, entityId: EntityId): ZoneKey? {
        val key = state.zones.entries.firstOrNull { entityId in it.value }?.key ?: return null
        return when (key.zoneType) {
            Zone.GRAVEYARD, Zone.EXILE, Zone.HAND, Zone.LIBRARY, Zone.COMMAND -> key
            else -> null
        }
    }

    /** Return [baseAction] with its target list replaced. */
    private fun applyTargets(
        baseAction: com.wingedsheep.engine.core.GameAction,
        targets: List<ChosenTarget>
    ): com.wingedsheep.engine.core.GameAction = when (baseAction) {
        is CastSpell -> baseAction.copy(targets = targets)
        is ActivateAbility -> baseAction.copy(targets = targets)
        else -> baseAction
    }

    /**
     * When both a normal cast and a kicker/offspring variant of the same card are
     * affordable, drop the normal variant. The kicker variant is strictly better —
     * it does everything the normal cast does plus the kicker bonus (e.g., offspring
     * creates an additional token). Keeping both inflates the candidate list and
     * triggers unnecessary deep search (the two variants score close together,
     * tripping the "close call" heuristic).
     */
    private fun preferKickerVariants(actions: List<LegalAction>): List<LegalAction> {
        // Collect cardIds that have an affordable CastWithKicker variant
        val kickedCardIds = mutableSetOf<EntityId>()
        for (action in actions) {
            if (action.actionType == "CastWithKicker") {
                val castSpell = action.action as? CastSpell ?: continue
                kickedCardIds.add(castSpell.cardId)
            }
        }
        if (kickedCardIds.isEmpty()) return actions

        // Remove the normal CastSpell variant for those cards
        return actions.filter { action ->
            if (action.actionType == "CastSpell") {
                val castSpell = action.action as? CastSpell ?: return@filter true
                castSpell.cardId !in kickedCardIds
            } else {
                true
            }
        }
    }

    /**
     * Expand an affordable, no-target X-cost activated ability into one candidate [LegalAction] per
     * concrete X value, filling [ActivateAbility.xValue] and any "discard a card" cost, so the
     * normal simulation-based scoring picks the best X. Submitting the enumerator's bare action runs
     * it at the default `xValue = 0` — e.g. the Momir avatar would only ever look for a mana-value-0
     * creature and make nothing, so the AI would always pass it over. Higher X usually yields a
     * bigger creature, so when many X are affordable we keep the top [MAX_X_CANDIDATES] and let
     * simulation choose among them. Targeted X abilities keep the engine's choose-X decision path
     * (they also need target selection) and pass through untouched, as do abilities with an
     * additional cost we don't know how to pay here.
     */
    private fun expandXCostAbilities(
        state: GameState,
        actions: List<LegalAction>,
        playerId: EntityId
    ): List<LegalAction> = actions.flatMap { action ->
        val base = action.action
        val maxX = action.maxAffordableX
        val info = action.additionalCostInfo
        val payableCost = info == null || info.costType == "DiscardCard"
        if (!action.hasXCost || maxX == null || maxX < 1 ||
            base !is ActivateAbility || action.requiresTargets || !payableCost
        ) {
            return@flatMap listOf(action)
        }
        val discard = chooseActivationDiscard(state, action, playerId)
        if (info?.costType == "DiscardCard" && info.discardCount > 0 && discard == null) {
            return@flatMap emptyList()
        }
        val xCandidates = if (isMomirAvatarActivation(state, action)) {
            momirXCandidates(state, maxX, playerId)
        } else {
            val lowest = maxOf(1, maxX - MAX_X_CANDIDATES + 1)
            (lowest..maxX).toList()
        }
        xCandidates.map { x ->
            action.copy(action = base.copy(xValue = x, costPayment = discard ?: base.costPayment))
        }
    }

    /**
     * Momir Basic strategy is mostly resource management: skip the smallest early activations so
     * the hand lasts long enough to make high-mana creatures, then stop spending extra mana beyond
     * the strong 8-drop band. This follows common Momir guidance: the starting player skips two
     * early drops, the drawing player skips one, then both aim to make 8s every turn.
     */
    private fun momirXCandidates(state: GameState, maxX: Int, playerId: EntityId): List<Int> {
        val wentFirst = state.turnOrder.firstOrNull() == playerId
        val firstStrategicX = if (wentFirst) 3 else 2
        if (maxX < firstStrategicX) return emptyList()
        if (maxX >= MOMIR_TARGET_X) return listOf(MOMIR_TARGET_X)
        return listOf(maxX)
    }

    private fun isMomirAvatarActivation(state: GameState, action: LegalAction): Boolean {
        if (state.format !is Format.MomirBasic) return false
        val activation = action.action as? ActivateAbility ?: return false
        val card = state.getEntity(activation.sourceId)?.get<CardComponent>() ?: return false
        return card.name == MOMIR_AVATAR_NAME
    }

    /**
     * Choose which card(s) to discard for an activated ability whose additional cost is "discard a
     * card". Prefers a land as fodder (the safest discard, and in Momir Basic the whole hand is
     * basics); falls back to the first available card. Returns null when there is no discard cost.
     */
    private fun chooseActivationDiscard(
        state: GameState,
        action: LegalAction,
        playerId: EntityId
    ): com.wingedsheep.sdk.scripting.AdditionalCostPayment? {
        val info = action.additionalCostInfo ?: return null
        if (info.costType != "DiscardCard" || info.discardCount <= 0) return null
        val candidates = info.validDiscardTargets
        if (candidates.isEmpty()) return null
        val ranked = candidates.sortedByDescending { id ->
            if (state.getEntity(id)?.get<CardComponent>()?.isLand == true) 1 else 0
        }
        return com.wingedsheep.sdk.scripting.AdditionalCostPayment(
            discardedCards = ranked.take(info.discardCount)
        )
    }

    /** Resolve the card name from a legal action's underlying GameAction. */
    private fun resolveCardName(state: GameState, action: LegalAction): String? {
        val entityId = when (val gameAction = action.action) {
            is CastSpell -> gameAction.cardId
            is ActivateAbility -> gameAction.sourceId
            else -> return null
        }
        return state.getEntity(entityId)?.get<CardComponent>()?.name
    }

    private companion object {
        /** Cap on the number of X values an X-cost ability is expanded into (keeps the highest). */
        const val MAX_X_CANDIDATES = 5

        const val MOMIR_TARGET_X = 8

        const val MOMIR_AVATAR_NAME = "Momir Vig, Simic Visionary"
    }
}
