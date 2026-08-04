package com.wingedsheep.ai.engine

import com.wingedsheep.ai.engine.advisor.CardAdvisorRegistry
import com.wingedsheep.ai.engine.advisor.CastContext
import com.wingedsheep.ai.engine.budget.BudgetPolicy
import com.wingedsheep.ai.engine.budget.DecisionBudget
import com.wingedsheep.ai.engine.budget.LegacyBudgetPolicy
import com.wingedsheep.ai.engine.evaluation.BoardEvaluator
import com.wingedsheep.ai.engine.evaluation.BoardPresence
import com.wingedsheep.ai.engine.knowledge.HoldPolicy
import com.wingedsheep.ai.engine.knowledge.IntentCatalog
import com.wingedsheep.ai.engine.knowledge.TimingVerdict
import com.wingedsheep.ai.engine.rollout.CandidateEvaluator
import com.wingedsheep.ai.engine.rollout.PlayoutPolicy
import com.wingedsheep.ai.engine.rollout.RolloutCandidateEvaluator
import com.wingedsheep.ai.engine.rollout.StaticCandidateEvaluator
import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.GameAction
import com.wingedsheep.engine.legalactions.LegalAction
import com.wingedsheep.engine.legalactions.MeaningfulActionFilter
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.sdk.core.Format
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.ManaSymbol
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import com.wingedsheep.sdk.scripting.AlternativePaymentChoice
import com.wingedsheep.sdk.scripting.ConvokePayment

/**
 * Chooses which [LegalAction] to take when the AI has priority.
 *
 * **One simulation per candidate, then a leaf score.** Candidates come from the enumerator, each is
 * simulated once to the quiet state it produces, and the best-scoring one wins if it beats passing.
 *
 * What "leaf score" means is [candidateEvaluator]'s business, and that is the whole Phase 7 seam:
 * [StaticCandidateEvaluator] is one `BoardEvaluator.evaluate` call — the greedy 1-ply AI, what
 * `AiProfile.LEGACY_V0` runs — and [RolloutCandidateEvaluator] replaces it with the mean of several
 * short playouts. Everything else in this file is identical either way, including the target
 * refinement, the hold policy and the [CardAdvisorRegistry] override path, which is the point: a
 * per-card advisor keeps working, over a much better base.
 *
 * There used to be a second, multi-ply alpha-beta pass here (`Searcher`). It was
 * unreachable — its `recommendDepth` gated on "can the opponent respond?", which opened
 * with `state.priorityPlayerId != playerId` and so was always false on our own priority —
 * and it carried a `Double.MIN_VALUE / 2` "−∞" sentinel that is actually `0.0`. It was
 * deleted rather than repaired, and Phase 7 replaced the mechanism outright rather than reviving
 * it. Its one good idea, extending the search on a close call, survives as
 * `RolloutSettings.criticalHorizonBonus`.
 *
 * Combat decisions are delegated to [CombatAdvisor].
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
     * being a candidate; and [TargetSelection.fillableRequirements] fills the slots it *can*
     * rather than abandoning the whole spell. Together they close the "889 of 945 rejected actions were
     * `CastSpell: No valid targets available`" finding Phase 1 quantified and left open.
     */
    private val useMeaningfulFilter: Boolean = false,
    /** Phase 4b. How much search each decision may spend. */
    private val budgetPolicy: BudgetPolicy = LegacyBudgetPolicy,
    /**
     * Phase 6: structural card knowledge. [IntentCatalog.NONE] is the off position and leaves
     * both consumers here — [TargetSelection.rank] and the hold policy — at their pre-Phase-6
     * behaviour.
     */
    private val intents: IntentCatalog = IntentCatalog.NONE,
    /**
     * Phase 7: how a candidate's post-action state is scored. Defaults to the pre-Phase-7 leaf, so
     * a caller that doesn't opt in gets the greedy 1-ply AI unchanged.
     */
    private val candidateEvaluator: CandidateEvaluator = StaticCandidateEvaluator(evaluator),
    /** Phase 8: a fair complete world, sampled once before any candidate simulation. */
    private val stateSampler: ((GameState, EntityId) -> GameState)? = null,
) {
    private val holdPolicy = HoldPolicy(intents)

    /**
     * Positions this player has already taken a non-pass action from, oldest first.
     *
     * The AI's only memory across decisions, and it exists for one reason: a leaf score cannot see
     * that it is being handed the same position over and over. See [StateProgress] and [remember].
     */
    private val positionsActedFrom = ArrayDeque<Long>()

    fun chooseAction(
        state: GameState,
        legalActions: List<LegalAction>,
        playerId: EntityId
    ): LegalAction {
        val evaluationState = stateSampler?.invoke(state, playerId) ?: state
        // Combat declaration steps need the CombatAdvisor to fill in attacker/blocker maps
        // even when there's only one legal action (which is the common case — the enumerator
        // returns a single DeclareAttackers/DeclareBlockers with an empty default map).
        val combatAction = legalActions.find { it.actionType == "DeclareAttackers" || it.actionType == "DeclareBlockers" }
        if (combatAction != null) {
            val budget = budgetPolicy.budgetFor(state, playerId, listOf(combatAction))
            return handleCombatDeclaration(evaluationState, combatAction, playerId, budget)
        }

        if (legalActions.size == 1) {
            val only = legalActions.first()
            return only.copy(action = chooseCommittedTargets(state, only, playerId))
        }

        val pass = legalActions.find { it.actionType == "PassPriority" }
        val affordable = expandXCostAbilities(state, preferKickerVariants(candidatesFrom(legalActions)), playerId)

        if (affordable.isEmpty()) return pass ?: legalActions.first()

        val budget = budgetPolicy.budgetFor(state, playerId, affordable)
        val here = StateProgress.digest(evaluationState)

        // ── Pass 1: one simulation per candidate, to the quiet state it leads to ──
        // The anytime contract: candidates are simulated in order and the budget only cuts the
        // tail short. `maxByOrNull` over a partial list is still a valid (if worse) answer.
        //
        // The pass is simulated first and scored alongside the rest, so an evaluator that
        // allocates effort across candidates (Phase 7's sequential halving) treats "do nothing" as
        // the real option it is rather than as a separately-computed threshold.
        val leaves = mutableListOf<LegalAction>()
        val leafStates = mutableListOf<GameState>()
        if (pass != null) {
            leaves += pass
            leafStates += simulator.simulate(evaluationState, pass.action).state
        }
        for (action in affordable) {
            val materialized = chooseCommittedTargets(evaluationState, action, playerId, budget)
            val simulation = simulator.simulate(evaluationState, materialized)
            // LegalAction affordability is necessarily a preview for costs such as convoke and
            // modal/additional payments. If materializing the concrete action cannot pass the
            // authoritative processor, it is not a candidate the AI may submit.
            if (simulation is SimulationResult.Illegal) continue
            // A line that walks back into a position we have already acted from has accomplished
            // nothing, whatever the leaf score says — and it is not a one-off mistake, because it
            // hands us back the very position that made it look good. Aphetto Alchemist untapping
            // itself is the degenerate case (`here`); two of them untapping each other is the same
            // thing one step longer. See [StateProgress].
            if (simulation !is SimulationResult.NeedsDecision) {
                val leaf = StateProgress.digest(simulation.state)
                if (leaf == here || leaf in positionsActedFrom) continue
            }
            leaves += action.copy(action = materialized)
            leafStates += simulation.state
            if (budget.expired()) break
        }

        // ── Pass 2: score every leaf at once ──
        val leafScores = candidateEvaluator.scoreAll(evaluationState, leafStates, playerId, budget)
        val passScore = if (pass != null) {
            leafScores.first()
        } else {
            // No pass on offer: the "do nothing" reference is the current position itself.
            candidateEvaluator.score(evaluationState, evaluationState, playerId, budget)
        }

        // ── Pass 3: per-card timing and advisor adjustments, in raw evaluator units ──
        val firstCandidate = if (pass != null) 1 else 0
        val scored = (firstCandidate until leaves.size).map { i ->
            leaves[i] to adjustScore(evaluationState, leaves[i], playerId, leafScores[i], passScore)
        }

        // On the opponent's end step, unspent mana is about to be wasted. Reduce the pass threshold
        // so the AI is more willing to use instants rather than letting mana evaporate.
        //
        // Phase 6 retires this blanket discount for agents with card knowledge: [HoldPolicy] makes
        // the same point per card, and better — a removal spell gets a *larger* end-step bonus,
        // while a pump that is about to wear off in cleanup gets a penalty instead of an
        // encouragement. Keeping both would double-count the first and cancel the second.
        val adjustedPassScore =
            if (!holdPolicy.isEnabled && state.activePlayerId != playerId && state.step == Step.END) {
                passScore - 1.5
            } else {
                passScore
            }

        val best = scored.maxByOrNull { it.second }
        return if (best != null && best.second > adjustedPassScore) {
            // Fill in targets on the returned action so the processor can execute it.
            // The committed target is chosen by simulation (not just the heuristic) so the
            // AI sees the real resolved board, including effects already on the stack.
            remember(here)
            best.first
        } else {
            pass ?: legalActions.first()
        }
    }

    /**
     * Record a position we are about to act from, so a later candidate that leads back to it is
     * recognised as the circle it is.
     *
     * Only positions we *act* from go in: passing may repeat as often as it likes, and recording it
     * would fill the memory with the windows where the AI does nothing. Bounded because a loop is
     * always short — the two-Alchemist cycle is length two — while a game is thousands of positions,
     * and because a digest carries its turn and step, so an entry can only ever match inside the
     * window where matching means going in circles.
     */
    private fun remember(digest: Long) {
        if (digest in positionsActedFrom) return
        positionsActedFrom.addLast(digest)
        if (positionsActedFrom.size > POSITION_MEMORY) positionsActedFrom.removeFirst()
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

    /**
     * Apply the two per-card adjustments to a leaf score, in raw evaluator units.
     *
     * Both are deltas on top of whatever the leaf said, which is what lets the rollout evaluator
     * slot in underneath without any of this changing: a hold-policy penalty and a `CardAdvisor`
     * override compose with a rollout mean exactly as they composed with a static evaluation.
     */
    private fun adjustScore(
        state: GameState,
        action: LegalAction,
        playerId: EntityId,
        leafScore: Double,
        passScore: Double,
    ): Double {
        val cardName = resolveCardName(state, action) ?: return leafScore

        // Phase 6: what the board looks like after this resolves is only half the question; the
        // other half is whether this was the window.
        val timing = holdPolicy.verdictFor(state, playerId, cardName)
        if (timing is TimingVerdict.NoWindow) {
            // The card does nothing here, so nothing the simulation reports should make it beat
            // passing. See [TimingVerdict.NoWindow] for why this is a floor and not a penalty.
            return passScore - 1.0
        }
        val timingDelta = (timing as? TimingVerdict.Adjust)?.delta ?: 0.0

        // Check for card-specific advisor override. Timing is applied outside it, so a per-card
        // advisor still sees the pure board score as its `defaultScore` and a card with both
        // keeps both.
        val advisor = advisorRegistry.getAdvisor(cardName) ?: return leafScore + timingDelta
        val context = CastContext(
            state = state,
            projected = state.projectedState,
            playerId = playerId,
            action = action,
            passScore = passScore,
            defaultScore = leafScore,
            evaluator = evaluator,
            simulator = simulator
        )
        return (advisor.evaluateCast(context) ?: leafScore) + timingDelta
    }

    /**
     * Pick the targets the AI actually commits to for a chosen targeted action, by simulation
     * rather than [TargetSelection]'s static rank. Simulating each candidate resolves the stack —
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
     * priority window can pay for.
     *
     * Deliberately **not** used inside a rollout playout — see [PlayoutPolicy]. Simulating to pick
     * targets inside a simulation is what would make a playout quadratic.
     */
    private fun chooseCommittedTargets(
        state: GameState,
        action: LegalAction,
        playerId: EntityId,
        budget: DecisionBudget = DecisionBudget.legacy(),
    ): com.wingedsheep.engine.core.GameAction {
        val baseAction = withAutomaticPayments(action)
        if (TargetSelection.targetsAlreadyFilled(baseAction) != false) return baseAction
        if (!budget.allowances.refineTargetsBySimulation) {
            return heuristicTargets(state, action, playerId)
        }
        val targetInfos = TargetSelection.fillableRequirements(action, useMeaningfulFilter)
            ?: return heuristicTargets(state, action, playerId)

        // Heuristic baseline for every requirement, then refine each one by simulation.
        val chosenTargets = mutableListOf<com.wingedsheep.engine.state.components.stack.ChosenTarget>()
        val chosenIds = mutableSetOf<EntityId>()
        val chosenTargetIds = mutableListOf<EntityId>()
        for (info in targetInfos) {
            val available = if (info.mustDifferFromEarlier) {
                info.validTargets.filterNot(chosenIds::contains)
            } else {
                info.validTargets
            }
            val selectedId = available.maxByOrNull { TargetSelection.rank(state, it, playerId, intents) }
                ?: return heuristicTargets(state, action, playerId)
            chosenTargets += TargetSelection.toChosenTarget(state, info, selectedId, playerId)
            chosenIds += selectedId
            chosenTargetIds += selectedId
        }

        val here = StateProgress.digest(state)
        for (i in targetInfos.indices) {
            if (budget.expired()) break
            val info = targetInfos[i]
            val priorIds = chosenTargetIds.take(i).toSet()
            val candidates = info.validTargets
                .filterNot { info.mustDifferFromEarlier && it in priorIds }
                .sortedByDescending { TargetSelection.rank(state, it, playerId, intents) }
                .take(budget.allowances.targetCandidates)
            if (candidates.size <= 1) continue
            val best = candidates.maxByOrNull { candidate ->
                val trial = chosenTargets.toMutableList()
                trial[i] = TargetSelection.toChosenTarget(state, info, candidate, playerId)
                val result = simulator.simulate(state, TargetSelection.applyTargets(baseAction, trial))
                // A target that resolves back into the position we are standing in is not a target
                // choice, it is a no-op wearing one — Aphetto Alchemist untapping itself. Rank it
                // below every real option, so `chooseAction` only ever drops the whole ability as
                // inert when *no* target does anything.
                if (StateProgress.digest(result.state) == here) {
                    Double.NEGATIVE_INFINITY
                } else {
                    evaluator.evaluate(result.state, result.state.projectedState, playerId)
                }
            } ?: continue
            chosenTargets[i] = TargetSelection.toChosenTarget(state, info, best, playerId)
            chosenTargetIds[i] = best
        }
        return TargetSelection.applyTargets(baseAction, chosenTargets)
    }

    /** The cheap target pick — one heuristic choice per requirement, no simulation. */
    private fun heuristicTargets(
        state: GameState,
        action: LegalAction,
        playerId: EntityId,
    ): com.wingedsheep.engine.core.GameAction = TargetSelection.fillHeuristically(
        state, action.copy(action = withAutomaticPayments(action)), playerId,
        fillPartialRequirements = useMeaningfulFilter, intents = intents
    )

    /**
     * Materialize deterministic payment choices carried by [LegalAction.additionalCostInfo].
     *
     * The enumerator exposes a Blight path as a distinct legal action, but the processor can only
     * distinguish it from the alternative-mana path through `AdditionalCostPayment.blightTargets`.
     * Keeping that choice only in UI metadata made the built-in AI submit the wrong branch for both
     * spells and activated abilities. The first candidate is deterministic and already filtered by
     * projected controller/type/counter legality.
     */
    private fun withAutomaticPayments(action: LegalAction): GameAction {
        val gameAction = withAutomaticConvoke(action)
        val info = action.additionalCostInfo ?: return gameAction
        val existing = when (gameAction) {
            is CastSpell -> gameAction.additionalCostPayment
            is ActivateAbility -> gameAction.costPayment
            else -> null
        } ?: AdditionalCostPayment()
        val payment = when (info.costType) {
            "Blight" -> existing.copy(blightTargets = info.validBlightTargets.take(1))
            "Behold" -> existing.copy(beheldCards = info.validBeholdTargets.take(info.beholdCount))
            "TapPermanents" -> existing.copy(tappedPermanents = info.validTapTargets.take(info.tapCount))
            "DiscardCard" -> existing.copy(discardedCards = info.validDiscardTargets.take(info.discardCount))
            "SacrificePermanent" -> existing.copy(
                sacrificedPermanents = info.validSacrificeTargets.take(info.sacrificeCount)
            )
            "BouncePermanent" -> existing.copy(bouncedPermanents = info.validBounceTargets.take(info.bounceCount))
            "ExileFromGraveyard" -> existing.copy(exiledCards = info.validExileTargets.take(info.exileMinCount))
            else -> return gameAction
        }
        return when (gameAction) {
            is CastSpell -> gameAction.copy(additionalCostPayment = payment)
            is ActivateAbility -> gameAction.copy(costPayment = payment)
            else -> gameAction
        }
    }

    /**
     * Turn the Convoke candidates advertised by the legal-action enumerator into the payment the
     * cast handler consumes. Colored pips are satisfied first; remaining creatures pay only the
     * generic part of the cost, so the AI never submits an invalid overpayment.
     */
    private fun withAutomaticConvoke(action: LegalAction): GameAction {
        val cast = action.action as? CastSpell ?: return action.action
        val creatures = action.convokeCreatures.orEmpty()
        val costString = action.manaCostString
        if (!action.hasConvoke || creatures.isEmpty() || costString == null) return cast

        val cost = ManaCost.parse(costString)
        val coloredNeeded = cost.symbols
            .filterIsInstance<ManaSymbol.Colored>()
            .groupingBy { it.color }
            .eachCount()
            .toMutableMap()
        var genericNeeded = cost.genericAmount
        val payments = linkedMapOf<EntityId, ConvokePayment>()
        val unused = creatures.toMutableList()

        for ((color, count) in coloredNeeded) {
            repeat(count) {
                val index = unused.indexOfFirst { color in it.colors }
                if (index >= 0) {
                    val creature = unused.removeAt(index)
                    payments[creature.entityId] = ConvokePayment(color)
                }
            }
        }
        while (genericNeeded > 0 && unused.isNotEmpty()) {
            val creature = unused.removeAt(0)
            payments[creature.entityId] = ConvokePayment()
            genericNeeded--
        }
        if (payments.isEmpty()) return cast

        val existing = cast.alternativePayment ?: AlternativePaymentChoice.NONE
        return cast.copy(
            alternativePayment = existing.copy(
                convokedCreatures = existing.convokedCreatures + payments
            )
        )
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

        /** How many acted-from positions [remember] keeps. See it for why a short memory suffices. */
        const val POSITION_MEMORY = 32

        const val MOMIR_TARGET_X = 8

        const val MOMIR_AVATAR_NAME = "Momir Vig, Simic Visionary"
    }
}
