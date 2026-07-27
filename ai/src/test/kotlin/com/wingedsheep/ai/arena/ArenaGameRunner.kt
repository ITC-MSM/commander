package com.wingedsheep.ai.arena

import com.wingedsheep.ai.engine.safeFallbackAction
import com.wingedsheep.engine.core.ActionProcessor
import com.wingedsheep.engine.core.GameConfig
import com.wingedsheep.engine.core.GameInitializer
import com.wingedsheep.engine.core.PlayerConfig
import com.wingedsheep.engine.core.SubmitDecision
import com.wingedsheep.engine.legalactions.LegalActionEnumerator
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.identity.LifeTotalComponent
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import java.security.MessageDigest
import kotlin.time.measureTime

/**
 * Result of one arena game, recorded **by seat**, never by agent.
 *
 * Seat-indexed is the whole point: a pair plays the same game twice with the agents swapped, and
 * anything that reads "did agent A win" before the pair is assembled is how a seat bias sneaks in.
 */
data class ArenaGameOutcome(
    val pairId: Int,
    /** 0 = the agent that sat in seat 0 (on the play) played first. */
    val gameIndex: Int,
    val seat0Agent: String,
    val seat1Agent: String,
    val seed: Long,
    /** 0, 1, or null for a draw / unfinished game. */
    val winnerSeat: Int?,
    val turns: Int,
    val actions: Int,
    val durationMs: Long,
    val seat0Life: Int,
    val seat1Life: Int,
    val completed: Boolean,
    /** Why the game ended without a winner. Empty when it ended normally. */
    val drawReason: String,
    /** A thrown engine exception, if any — the arena doubles as a crash finder at scale. */
    val exception: String?,
    /**
     * Actions the AI proposed that the processor rejected, recovered by [safeFallbackAction].
     * Keyed by `"<ActionType>: <error>"` so the report can histogram them — this is the arena
     * doubling as a free bug finder at scale, and a rejected action is always a real defect
     * somewhere (the enumerator offered it, or the AI mangled it).
     */
    val illegalActions: Map<String, Int>,
    /** Set only when the runner was asked to record one. See [ArenaGameRunner.play]. */
    val actionStreamHash: String?,
)

/**
 * Plays a single arena game: two [ArenaAgent]s, one deck each, one seed.
 *
 * Built from `AdvisorBenchmark.playAdvisorGame`, not `AIBenchmark.playGame` — the latter
 * round-trips every action through `ClientStateTransformer.transform` + `LegalActionEnricher.enrich`
 * to satisfy the server's DTO interface, which is pure overhead for engine-vs-engine. The
 * stuck-detector and draw taxonomy are `AIBenchmark`'s.
 */
object ArenaGameRunner {

    /** Matches `AIBenchmark`: this many actions on one turn with no turn change means wedged. */
    private const val STUCK_ACTION_THRESHOLD = 300

    fun play(
        registry: CardRegistry,
        seat0: ArenaAgent,
        seat1: ArenaAgent,
        seat0Deck: Deck,
        seat1Deck: Deck,
        seed: Long,
        pairId: Int,
        gameIndex: Int,
        maxTurns: Int = 50,
        /** Hash every action and decision into [ArenaGameOutcome.actionStreamHash]. Off by
         *  default: it costs a string per action, and only `FrozenBaselineTest` needs it. */
        recordActionStream: Boolean = false,
    ): ArenaGameOutcome {
        val processor = ActionProcessor(registry)
        val enumerator = LegalActionEnumerator.create(registry)
        val initializer = GameInitializer(registry)

        val init = initializer.initializeGame(
            GameConfig(
                players = listOf(PlayerConfig("Seat0", seat0Deck), PlayerConfig("Seat1", seat1Deck)),
                // Mulligans are skipped so a rerun at the same seed is the same game. That puts
                // mulligan quality out of test — schedule a separate mulligan A/B rather than
                // pretending this measures it.
                skipMulligans = true,
                startingPlayerIndex = 0,
                seed = seed,
            )
        )

        val seat0Id = init.state.turnOrder[0]
        val seat1Id = init.state.turnOrder[1]
        val ai0 = seat0.createPlayer(registry, seat0Id)
        val ai1 = seat1.createPlayer(registry, seat1Id)
        fun aiFor(playerId: EntityId) = if (playerId == seat0Id) ai0 else ai1
        fun seatOf(playerId: EntityId) = if (playerId == seat0Id) 0 else 1

        var state: GameState = init.state
        var actionCount = 0
        val illegalActions = mutableMapOf<String, Int>()
        var lastProgressTurn = 0
        var lastProgressAction = 0
        var drawReason = ""
        var exception: String? = null
        val stream = if (recordActionStream) MessageDigest.getInstance("SHA-256") else null
        fun record(entry: String) = stream?.update(entry.toByteArray(Charsets.UTF_8))

        val duration = measureTime {
            try {
                while (!state.gameOver && state.turnNumber < maxTurns) {
                    if (actionCount - lastProgressAction > STUCK_ACTION_THRESHOLD &&
                        state.turnNumber == lastProgressTurn
                    ) {
                        drawReason = "stuck(turn=${state.turnNumber},step=${state.step.name})"
                        break
                    }
                    if (state.turnNumber > lastProgressTurn) {
                        lastProgressTurn = state.turnNumber
                        lastProgressAction = actionCount
                    }

                    val decision = state.pendingDecision
                    if (decision != null) {
                        actionCount++
                        val response = aiFor(decision.playerId).respondToDecision(state, decision)
                        record("D$actionCount|${seatOf(decision.playerId)}|${decision::class.simpleName}|$response\n")
                        val r = processor.process(state, SubmitDecision(decision.playerId, response)).result
                        if (r.error != null) {
                            drawReason = "decisionError(${r.error})"
                            break
                        }
                        state = r.state
                        continue
                    }

                    val priorityPlayer = state.priorityPlayerId
                    if (priorityPlayer == null) {
                        drawReason = "noPriority(turn=${state.turnNumber})"
                        break
                    }

                    actionCount++
                    val action = aiFor(priorityPlayer).chooseAction(state)
                    record("A$actionCount|${seatOf(priorityPlayer)}|${state.step.name}|$action\n")
                    val r = processor.process(state, action).result
                    val next = if (r.error != null) {
                        val key = "${action::class.simpleName}: ${r.error}"
                        illegalActions[key] = (illegalActions[key] ?: 0) + 1
                        val fallback = processor
                            .process(state, safeFallbackAction(state, priorityPlayer, enumerator))
                            .result
                        if (fallback.error != null) {
                            drawReason = "error(${r.error}; fallback: ${fallback.error})"
                            null
                        } else fallback.state
                    } else r.state

                    if (next == null) break
                    if (next === state) {
                        drawReason = "noProgress(turn=${state.turnNumber},step=${state.step.name})"
                        break
                    }
                    state = next
                }
                if (!state.gameOver && drawReason.isEmpty()) drawReason = "maxTurns($maxTurns)"
            } catch (e: Throwable) {
                exception = "${e::class.simpleName}: ${e.message}"
                if (drawReason.isEmpty()) drawReason = "exception"
            }
        }

        val seat0Life = state.getEntity(seat0Id)?.get<LifeTotalComponent>()?.life ?: 0
        val seat1Life = state.getEntity(seat1Id)?.get<LifeTotalComponent>()?.life ?: 0
        val winnerSeat = when {
            !state.gameOver -> null
            state.winnerId == seat0Id -> 0
            state.winnerId == seat1Id -> 1
            else -> null
        }
        record("END|turns=${state.turnNumber}|winner=$winnerSeat|life=$seat0Life/$seat1Life\n")

        return ArenaGameOutcome(
            pairId = pairId,
            gameIndex = gameIndex,
            seat0Agent = seat0.name,
            seat1Agent = seat1.name,
            seed = seed,
            winnerSeat = winnerSeat,
            turns = state.turnNumber,
            actions = actionCount,
            durationMs = duration.inWholeMilliseconds,
            seat0Life = seat0Life,
            seat1Life = seat1Life,
            completed = state.gameOver,
            drawReason = drawReason,
            exception = exception,
            illegalActions = illegalActions.toMap(),
            actionStreamHash = stream?.digest()?.joinToString("") { "%02x".format(it) }?.take(16),
        )
    }
}
