package com.wingedsheep.gym.simulation

import com.wingedsheep.ai.engine.DecisionResponder
import com.wingedsheep.engine.core.GameAction
import com.wingedsheep.engine.core.GameConfig
import com.wingedsheep.engine.core.ActionProcessorObserver
import com.wingedsheep.engine.core.InvariantCheckingActionObserver
import com.wingedsheep.engine.core.InvariantViolation
import com.wingedsheep.engine.core.InvariantViolationException
import com.wingedsheep.engine.core.OrderTriggeredAbilitiesDecision
import com.wingedsheep.engine.core.SubmitDecision
import com.wingedsheep.engine.core.TriggeredAbilitiesOrderedResponse
import com.wingedsheep.engine.core.TriggerOrderItem
import com.wingedsheep.engine.handlers.actions.decision.DecisionValidators
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.gym.GameEnvironment
import com.wingedsheep.gym.contract.ObservationBuilder
import com.wingedsheep.sdk.model.Deck
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Deterministic, invariant-checked game exploration for fuzz/smoke tests.
 *
 * The transcript is deliberately gym-native: it can later be converted to a
 * server CompactReplay, but gym never depends on the server module just to
 * inspect a broken action sequence.
 */
class SeededSimulationRunner(
    private val registry: CardRegistry,
    private val observerFactory: () -> ActionProcessorObserver = { InvariantCheckingActionObserver() },
) {
    fun run(config: GameConfig, policySeed: Long, maxSteps: Int = 500): SimulationTranscript {
        val environment = GameEnvironment.create(
            registry,
            observer = observerFactory(),
        )
        environment.reset(config)
        val initialStateDigest = digest(environment)
        val random = java.util.Random(policySeed)
        val simulator = com.wingedsheep.ai.engine.GameSimulator(registry)
        val responder = DecisionResponder(simulator, GameEnvironment.defaultEvaluator())
        val trace = mutableListOf<SimulationTraceEntry>()
        var outcome = SimulationOutcome.NO_ACTION
        var failure: SimulationInvariantFailure? = null

        try {
            while (!environment.isTerminal && environment.stepCount < maxSteps) {
                val pendingDecision = environment.pendingDecision
                val action: GameAction? = pendingDecision?.let { decision ->
                    SubmitDecision(decision.playerId, responder.respond(environment.state, decision, decision.playerId))
                } ?: run {
                    val choices = environment.legalActions().filter { it.affordable }
                    choices.takeIf { it.isNotEmpty() }?.let { it[random.nextInt(it.size)].action }
                }
                if (action == null) break
                val preDigest = digest(environment)
                val inputKind = if (pendingDecision == null) SimulationInputKind.LEGAL_ACTION else SimulationInputKind.PENDING_DECISION
                val result = try {
                    environment.step(action)
                } catch (exception: InvariantViolationException) {
                    failure = SimulationInvariantFailure.from(
                        attemptedStepIndex = environment.stepCount + 1,
                        attemptedAction = action,
                        inputKind = inputKind,
                        preDigest = preDigest,
                        triggerOrderItems = (pendingDecision as? OrderTriggeredAbilitiesDecision)?.abilities,
                        exception = exception,
                    )
                    throw exception
                }
                trace += SimulationTraceEntry(
                    stepIndex = environment.stepCount,
                    action = action,
                    inputKind = inputKind,
                    preDigest = preDigest,
                    postDigest = digest(environment),
                    eventTypes = result.events.map { it::class.qualifiedName ?: it::class.simpleName.orEmpty() },
                    turnNumber = result.info.turnNumber,
                    pendingDecisionKind = result.pendingDecision?.let { it::class.simpleName },
                    triggerOrderItems = (pendingDecision as? OrderTriggeredAbilitiesDecision)?.abilities,
                )
            }
            outcome = when {
                environment.isTerminal -> SimulationOutcome.TERMINAL
                environment.stepCount >= maxSteps -> SimulationOutcome.STEP_LIMIT
                else -> SimulationOutcome.NO_ACTION
            }
        } catch (_: InvariantViolationException) {
            outcome = SimulationOutcome.INVARIANT_FAILURE
        }

        return SimulationTranscript(
            gameSeed = requireNotNull(environment.initializationSeed),
            policySeed = policySeed,
            maxSteps = maxSteps,
            config = SimulationConfigEvidence.from(config),
            initialStateDigest = initialStateDigest,
            entries = trace,
            outcome = outcome,
            invariantFailure = failure,
        )
    }

    /**
     * Re-executes an exported action transcript against the supplied, version-pinned game config.
     *
     * A transcript is evidence, not an authority for legality: every replayed priority action is
     * checked against the newly enumerated legal actions and every decision response is checked
     * against the newly-created pending decision before it is submitted. Entity IDs are expected to
     * be reproducible from [SimulationTranscript.gameSeed]; decision IDs are deliberately ignored
     * by the validator because they are transport handles rather than game choices.
     */
    fun replay(config: GameConfig, transcript: SimulationTranscript): SimulationReplayResult {
        val environment = GameEnvironment.create(registry, observer = observerFactory())
        return try {
            // A replay is evidence for one exact initialization configuration.  In particular,
            // explicit player IDs are part of the deterministic entity graph, so accepting a
            // caller-supplied config that differs from the transcript could make an unrelated
            // game appear to validate merely because its actions happen to be legal.
            require(SimulationConfigEvidence.from(config) == transcript.config) {
                "Replay config differs from transcript evidence"
            }
            // A null config seed is intentionally entropy-backed on the first run. The transcript's
            // resolved game seed is the replay authority, so always substitute it here.
            environment.reset(config.copy(seed = transcript.gameSeed))
            require(environment.initializationSeed == transcript.gameSeed) {
                "Replay seed mismatch: config initialized ${environment.initializationSeed}, transcript requires ${transcript.gameSeed}"
            }
            require(digest(environment) == transcript.initialStateDigest) { "Replay initial state digest differs" }

            transcript.entries.forEach { entry ->
                require(environment.stepCount + 1 == entry.stepIndex) {
                    "Replay step index mismatch at transcript step ${entry.stepIndex}"
                }
                require(digest(environment) == entry.preDigest) {
                    "Replay pre-state digest differs at transcript step ${entry.stepIndex}"
                }
                val replayAction = validateAndRebindReplayInput(environment, entry)
                val result = environment.step(replayAction)
                require(digest(environment) == entry.postDigest) {
                    "Replay post-state digest differs at transcript step ${entry.stepIndex}"
                }
                require(result.events.map { it::class.qualifiedName ?: it::class.simpleName.orEmpty() } == entry.eventTypes) {
                    "Replay event sequence differs at transcript step ${entry.stepIndex}"
                }
                require(result.info.turnNumber == entry.turnNumber) {
                    "Replay turn number differs at transcript step ${entry.stepIndex}"
                }
                require(result.pendingDecision?.let { it::class.simpleName } == entry.pendingDecisionKind) {
                    "Replay pending decision differs at transcript step ${entry.stepIndex}"
                }
            }
            transcript.invariantFailure?.let { failure ->
                require(transcript.outcome == SimulationOutcome.INVARIANT_FAILURE) {
                    "Invariant failure evidence requires INVARIANT_FAILURE outcome"
                }
                require(environment.stepCount + 1 == failure.attemptedStepIndex) {
                    "Invariant failure step index differs"
                }
                require(digest(environment) == failure.preDigest) {
                    "Invariant failure pre-state digest differs"
                }
                val action = validateAndRebindReplayInput(
                    environment,
                    SimulationTraceEntry(
                        stepIndex = failure.attemptedStepIndex,
                        action = failure.attemptedAction,
                        inputKind = failure.inputKind,
                        preDigest = failure.preDigest,
                        postDigest = "",
                        eventTypes = emptyList(),
                        turnNumber = environment.turnNumber,
                        pendingDecisionKind = environment.pendingDecision?.let { it::class.simpleName },
                        triggerOrderItems = failure.triggerOrderItems,
                    )
                )
                val observed = try {
                    environment.step(action)
                    error("Replay expected an invariant failure")
                } catch (exception: InvariantViolationException) {
                    SimulationInvariantFailure.from(
                        attemptedStepIndex = failure.attemptedStepIndex,
                        attemptedAction = failure.attemptedAction,
                        inputKind = failure.inputKind,
                        preDigest = failure.preDigest,
                        triggerOrderItems = failure.triggerOrderItems,
                        exception = exception,
                    )
                }
                require(observed.exceptionType == failure.exceptionType &&
                    observed.message == failure.message &&
                    observed.canonicalViolations == failure.canonicalViolations
                ) { "Invariant failure details differ during replay" }
                return SimulationReplayResult(valid = true, replayedEntries = transcript.entries.size)
            }
            val observedOutcome = when {
                environment.isTerminal -> SimulationOutcome.TERMINAL
                environment.stepCount >= transcript.maxSteps -> SimulationOutcome.STEP_LIMIT
                else -> SimulationOutcome.NO_ACTION
            }
            require(observedOutcome == transcript.outcome) {
                "Replay outcome differs: observed $observedOutcome, transcript ${transcript.outcome}"
            }
            SimulationReplayResult(valid = true, replayedEntries = transcript.entries.size)
        } catch (failure: Exception) {
            SimulationReplayResult(
                valid = false,
                replayedEntries = environment.stepCount,
                failure = failure.message ?: failure::class.qualifiedName,
            )
        }
    }

    private fun validateAndRebindReplayInput(environment: GameEnvironment, entry: SimulationTraceEntry): GameAction =
        when (entry.inputKind) {
            SimulationInputKind.LEGAL_ACTION -> {
                require(environment.pendingDecision == null) { "Transcript expected priority action while a decision is pending" }
                val legal = environment.legalActions().firstOrNull { it.action == entry.action }
                require(legal?.affordable == true) {
                    "Transcript action at step ${entry.stepIndex} is no longer a legal affordable action"
                }
                entry.action
            }
            SimulationInputKind.PENDING_DECISION -> {
                val decision = requireNotNull(environment.pendingDecision) {
                    "Transcript expected a decision at step ${entry.stepIndex}"
                }
                val action = entry.action as? SubmitDecision
                    ?: error("Transcript decision input at step ${entry.stepIndex} is not SubmitDecision")
                require(action.playerId == decision.playerId) { "Transcript decision player differs at step ${entry.stepIndex}" }
                val reboundResponse = rebindDecisionResponse(action.response, decision, entry.triggerOrderItems)
                require(DecisionValidators.validate(decision, reboundResponse, environment.state) == null) {
                    "Transcript decision response is no longer legal at step ${entry.stepIndex}"
                }
                // Decision IDs are fresh routing nonces, not deterministic game state. Validate
                // the recorded choice payload, then bind it to this run's pending decision.
                action.copy(response = reboundResponse)
            }
        }

    private fun rebindDecisionResponse(
        response: com.wingedsheep.engine.core.DecisionResponse,
        decision: com.wingedsheep.engine.core.PendingDecision,
        recordedTriggerItems: List<TriggerOrderItem>?,
    ): com.wingedsheep.engine.core.DecisionResponse {
        if (response !is TriggeredAbilitiesOrderedResponse) return response.withDecisionId(decision.id)
        val fresh = decision as? OrderTriggeredAbilitiesDecision
            ?: error("Transcript expected triggered-ability ordering decision")
        val recorded = requireNotNull(recordedTriggerItems) {
            "Transcript lacks offered trigger instances for an ordering response"
        }
        require(recorded.size == fresh.abilities.size) { "Triggered ability offer count differs" }

        // Instance IDs are fresh on replay, but the offered-list position identifies the actual
        // pending trigger instance in this deterministic state. First prove the freshly offered
        // instances retain the recorded display semantics at each position, then translate each
        // old opaque ID through its recorded position to that position's fresh opaque ID. This
        // deliberately does not collapse same-text instances into an arbitrary equivalence class.
        require(recorded.indices.all { index ->
            triggerSignature(recorded[index]) == triggerSignature(fresh.abilities[index])
        }) { "Fresh trigger offer semantics differ from recorded offer" }
        val oldIndexById = recorded.mapIndexed { index, item -> item.id to index }.toMap()
        require(response.orderedAbilityIds.toSet() == oldIndexById.keys &&
            response.orderedAbilityIds.size == oldIndexById.size
        ) { "Recorded trigger order does not contain every offered instance exactly once" }
        val rebound = response.orderedAbilityIds.map { oldId ->
            fresh.abilities[requireNotNull(oldIndexById[oldId])].id
        }
        return TriggeredAbilitiesOrderedResponse(decision.id, rebound)
    }

    private fun triggerSignature(item: TriggerOrderItem): String =
        "${item.sourceName.length}:${item.sourceName}|${item.description.length}:${item.description}"

    private fun digest(environment: GameEnvironment): String {
        val perspective = environment.playerIds.first()
        return (ObservationBuilder().build(environment.state, perspective, emptyList(), revealAll = true)
            .observation as com.wingedsheep.gym.contract.TrainingObservation).stateDigest
    }
}

/** JSON-safe evidence for a deterministic simulation. */
@Serializable
data class SimulationTranscript(
    val gameSeed: Long,
    val policySeed: Long,
    val maxSteps: Int,
    val config: SimulationConfigEvidence,
    val initialStateDigest: String,
    val entries: List<SimulationTraceEntry>,
    val outcome: SimulationOutcome,
    /** Present only when the next submitted input crossed an invariant boundary. */
    val invariantFailure: SimulationInvariantFailure? = null,
) {
    fun toJson(): String = json.encodeToString(this)

    companion object {
        private val json = Json { prettyPrint = true; encodeDefaults = true }

        fun fromJson(value: String): SimulationTranscript = json.decodeFromString(value)
    }
}

/** Serializable evidence for the action that exposed an invariant failure. */
@Serializable
data class SimulationInvariantFailure(
    val attemptedStepIndex: Int,
    val attemptedAction: GameAction,
    val inputKind: SimulationInputKind,
    val preDigest: String,
    /** Original offered trigger instances when the rejected input ordered a trigger wave. */
    val triggerOrderItems: List<TriggerOrderItem>? = null,
    val exceptionType: String,
    val message: String,
    val canonicalViolations: List<String>,
) {
    companion object {
        fun from(
            attemptedStepIndex: Int,
            attemptedAction: GameAction,
            inputKind: SimulationInputKind,
            preDigest: String,
            triggerOrderItems: List<TriggerOrderItem>?,
            exception: InvariantViolationException,
        ) = SimulationInvariantFailure(
            attemptedStepIndex = attemptedStepIndex,
            attemptedAction = attemptedAction,
            inputKind = inputKind,
            preDigest = preDigest,
            triggerOrderItems = triggerOrderItems,
            exceptionType = exception::class.qualifiedName ?: exception::class.simpleName.orEmpty(),
            message = exception.message.orEmpty(),
            canonicalViolations = exception.violations.map(::canonicalViolation),
        )

        private fun canonicalViolation(violation: InvariantViolation): String = when (violation) {
            is InvariantViolation.DanglingZoneEntity -> "DanglingZoneEntity|zone=${violation.zone}|entity=${violation.entityId}"
            is InvariantViolation.DanglingStackEntity -> "DanglingStackEntity|entity=${violation.entityId}"
            is InvariantViolation.EntityInMultipleLocations ->
                "EntityInMultipleLocations|entity=${violation.entityId}|first=${violation.first}|second=${violation.second}"
            is InvariantViolation.StackEntityWithoutStackComponent -> "StackEntityWithoutStackComponent|entity=${violation.entityId}"
            InvariantViolation.DuplicateTurnOrderPlayer -> "DuplicateTurnOrderPlayer"
            is InvariantViolation.TurnOrderEntityIsNotPlayer -> "TurnOrderEntityIsNotPlayer|entity=${violation.entityId}"
            is InvariantViolation.UnknownActivePlayer -> "UnknownActivePlayer|entity=${violation.entityId}"
            is InvariantViolation.UnknownPriorityPlayer -> "UnknownPriorityPlayer|entity=${violation.entityId}"
            is InvariantViolation.UnknownPriorityPasser -> "UnknownPriorityPasser|entity=${violation.entityId}"
            is InvariantViolation.UnknownDecisionPlayer -> "UnknownDecisionPlayer|entity=${violation.entityId}"
            InvariantViolation.RejectedActionChangedState -> "RejectedActionChangedState"
            InvariantViolation.RejectedActionEmittedEvents -> "RejectedActionEmittedEvents"
        }
    }
}

@Serializable
data class SimulationTraceEntry(
    val stepIndex: Int,
    val action: GameAction,
    /** Whether the recorded input was an enumerated priority action or a validated decision response. */
    val inputKind: SimulationInputKind,
    val preDigest: String,
    val postDigest: String,
    val eventTypes: List<String>,
    val turnNumber: Int,
    val pendingDecisionKind: String?,
    /** Original offered trigger instances; required to rebind their fresh replay IDs semantically. */
    val triggerOrderItems: List<TriggerOrderItem>? = null,
)

@Serializable
enum class SimulationInputKind { LEGAL_ACTION, PENDING_DECISION }

/** Result of validating a JSON-safe transcript by replaying it from its recorded seed. */
data class SimulationReplayResult(
    val valid: Boolean,
    val replayedEntries: Int,
    val failure: String? = null,
)

@Serializable
enum class SimulationOutcome { TERMINAL, STEP_LIMIT, NO_ACTION, INVARIANT_FAILURE }

@Serializable
data class SimulationConfigEvidence(
    val players: List<SimulationPlayerEvidence>,
    val startingHandSize: Int,
    val skipMulligans: Boolean,
    val useHandSmoother: Boolean,
    val handSmootherCandidates: Int,
    val startingPlayerIndex: Int?,
    /** Runtime formats are a hierarchy, so portable evidence stores their explicit string form. */
    val format: String,
    val attackMode: String,
    val teams: List<List<Int>>?,
) {
    companion object {
        fun from(config: GameConfig) = SimulationConfigEvidence(
            players = config.players.map { player ->
                SimulationPlayerEvidence(
                    name = player.name,
                    deck = player.deck,
                    startingLife = player.startingLife,
                    playerId = player.playerId,
                    commanderCardName = player.commanderCardName,
                )
            },
            startingHandSize = config.startingHandSize,
            skipMulligans = config.skipMulligans,
            useHandSmoother = config.useHandSmoother,
            handSmootherCandidates = config.handSmootherCandidates,
            startingPlayerIndex = config.startingPlayerIndex,
            format = config.format.toString(),
            attackMode = config.attackMode.toString(),
            teams = config.teams,
        )
    }
}

@Serializable
data class SimulationPlayerEvidence(
    val name: String,
    val deck: Deck,
    val startingLife: Int,
    /** Explicit IDs are initialization inputs, not observer-only labels. */
    val playerId: com.wingedsheep.sdk.model.EntityId? = null,
    val commanderCardName: String?,
)
