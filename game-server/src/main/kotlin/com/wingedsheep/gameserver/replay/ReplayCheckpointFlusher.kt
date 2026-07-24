package com.wingedsheep.gameserver.replay

import com.wingedsheep.gameserver.repository.GameRepository
import com.wingedsheep.gameserver.session.GameSession
import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

/**
 * Keeps in-flight recordings in the [ReplayStore] so a restart doesn't lose a game in progress.
 *
 * That guarantee used to come free: the Redis session blob carried the recording along with the live
 * state, and Redis was written on every state change. Moving replays to a single store meant taking
 * the guarantee back explicitly — but *not* by writing on every action. The record is a whole
 * re-encoded blob, so per-action writes are quadratic in the length of the game for a property
 * (surviving a crash) that a few seconds of granularity satisfies just as well.
 *
 * So we sweep instead: every few seconds, flush the sessions whose action count moved. The cost of
 * coarse flushing is that a crash loses the last few actions, which
 * [GameSession.restoreReplayRecording] detects on the way back up (via the fingerprint written with
 * each flush) and handles by keeping the honest shorter replay rather than splicing the rest of the
 * game onto a stale prefix.
 */
@Component
class ReplayCheckpointFlusher(
    private val gameRepository: GameRepository,
    private val replayService: ReplayService,
    private val engineVersion: EngineVersion,
) {
    private val logger = LoggerFactory.getLogger(ReplayCheckpointFlusher::class.java)

    /** sessionId -> action count at last flush, so an idle game isn't rewritten every sweep. */
    private val flushed = ConcurrentHashMap<String, Int>()

    @Scheduled(fixedDelay = FLUSH_INTERVAL_MS, initialDelay = FLUSH_INTERVAL_MS)
    fun flush() {
        val live = gameRepository.findAll()
        for (session in live) {
            runCatching { flushSession(session) }
                .onFailure { logger.warn("Failed to flush replay for ${session.sessionId}: ${it.message}") }
        }

        // A game we were flushing that no longer has a session was abandoned (conceded away,
        // disconnected, swept as a zombie) — a game over would have called [forget] first. Close its
        // record out so the partial recording is watchable instead of stuck mid-write forever.
        val liveIds = live.mapTo(HashSet()) { it.sessionId }
        for (sessionId in flushed.keys - liveIds) {
            flushed.remove(sessionId)
            runCatching { replayService.finalizePartial(sessionId) }
                .onFailure { logger.warn("Failed to finalize abandoned replay $sessionId: ${it.message}") }
        }
    }

    /** Flush everything one last time on shutdown, which is the graceful half of "a restart". */
    @PreDestroy
    fun flushOnShutdown() {
        flush()
    }

    /** Drop bookkeeping for a game that has finished (its final record is already stored). */
    fun forget(sessionId: String) {
        flushed.remove(sessionId)
    }

    private fun flushSession(session: GameSession) {
        val setup = session.getReplaySetup() ?: return
        val actions = session.getRecordedActions()
        if (flushed[session.sessionId] == actions.size) return

        replayService.saveInProgress(
            replay = CompactReplay(
                gameId = session.sessionId,
                players = session.getPlayers().map { ReplayPlayerInfo(it.playerId.value, it.playerName) },
                startedAt = (session.replayStartedAt ?: Instant.now()).toString(),
                endedAt = "",
                winnerName = null,
                setup = setup,
                actions = actions,
                yields = session.getReplayYields(),
                engineVersion = engineVersion.value,
                pinnedCards = session.getPinnedCards(),
                checkpoints = session.getReplayCheckpoints(),
            ),
            resumeFingerprint = session.getReplayResumeFingerprint(),
        )
        flushed[session.sessionId] = actions.size
    }

    private companion object {
        const val FLUSH_INTERVAL_MS = 5_000L
    }
}
