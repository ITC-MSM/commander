package com.wingedsheep.gameserver.tournament

import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlin.random.Random

/**
 * Deciding an AI-vs-AI bracket match without playing it.
 *
 * The saving is why the feature exists: a round-robin with one human and seven AI seats plays 28
 * matches, and only seven of them have the human in them. The other 21 are two AI controllers driving
 * a full engine game that nobody watches, and on a small box that is where the tournament's CPU goes.
 *
 * What has to survive the shortcut is the bracket's liveness. A simulated match is complete but never
 * had a `gameSessionId`, and [TournamentManager.hasIncompleteMatchBefore] — the guard that stops a
 * seat being handed two games at once — keys off completion, not off having been played. So a
 * simulated result must unblock the next round exactly as a played one does; if it didn't, the
 * shortcut would trade compute for the stall it was meant to avoid.
 */
class AiMatchSimulationTest : FunSpec({

    val human = EntityId("human")
    val ai = (1..7).map { EntityId("ai$it") }
    val everyone = (listOf(human) + ai).toSet()

    fun tournament(): TournamentManager = TournamentManager(
        lobbyId = "lobby-sim",
        players = (listOf(human to "Vincent") + ai.mapIndexed { i, id -> id to "AI ${i + 1}" }),
        gamesPerMatch = 1
    )

    fun TournamentRound.matchFor(playerId: EntityId): TournamentMatch =
        matches.first { it.player1Id == playerId || it.player2Id == playerId }

    fun TournamentMatch.opponentOf(playerId: EntityId): EntityId =
        (if (player1Id == playerId) player2Id else player1Id)!!

    test("only an AI pair in a bracket that also has a human is simulated") {
        val simulator = AiMatchSimulator(Random(1))
        val aiSeats = ai.toSet()

        // The case this exists for: two AI seats in a bracket the human is also playing in.
        simulator.shouldSimulate(everyone, aiSeats, ai[0], ai[1]) shouldBe true

        // A human seat is never decided for them, on either side of the pairing.
        simulator.shouldSimulate(everyone, aiSeats, human, ai[0]) shouldBe false
        simulator.shouldSimulate(everyone, aiSeats, ai[0], human) shouldBe false

        // An all-AI bracket is the AI Sandbox / a model-comparison run: it was built to be watched,
        // and simulating would resolve the whole thing in one sweep with nothing to open.
        simulator.shouldSimulate(aiSeats, aiSeats, ai[0], ai[1]) shouldBe false
    }

    test("the simulator picks one of the two seats, and both of them") {
        val simulator = AiMatchSimulator(Random(20260904))
        val picks = (1..200).map { simulator.pickWinner(ai[0], ai[1]) }.toSet()
        picks shouldBe setOf(ai[0], ai[1])
    }

    test("a simulated result moves the standings like a played one, and says it was simulated") {
        val manager = tournament()
        val round1 = manager.startNextRound()!!
        val match = round1.matchFor(ai[0])
        val winner = ai[0]
        val loser = match.opponentOf(ai[0])

        manager.reportSimulatedResult(match, winner)

        match.isComplete shouldBe true
        match.isSimulated shouldBe true
        match.winnerId shouldBe winner
        // No game ran, so there is nothing to spectate and nothing to open a replay on.
        match.gameSessionId shouldBe null

        val standings = manager.getStandingsForPersistence()
        standings.getValue(winner).wins shouldBe 1
        standings.getValue(winner).gamesWon shouldBe 1
        // No game was played, so no life total was invented for the displayed differential.
        standings.getValue(winner).lifeDifferential shouldBe 0
        standings.getValue(loser).losses shouldBe 1
        standings.getValue(loser).gamesLost shouldBe 1

        manager.getRoundResults(round1).single { it.player1Id == match.player1Id.value }
            .isSimulated shouldBe true
    }

    test("a simulated result unblocks the next round the same way a played result does") {
        val manager = tournament()
        val round1 = manager.startNextRound()!!

        // The human's game is the only one actually played; every other round-1 pair is AI vs AI.
        val humanMatch = round1.matchFor(human)
        humanMatch.gameSessionId = "r1-human"

        val simulated = round1.matches.filter { it !== humanMatch }
        simulated.size shouldBe 3
        simulated.forEach { manager.reportSimulatedResult(it, it.player1Id) }

        // The round isn't over — the human is still playing — but every AI whose round-1 match was
        // simulated is free to move on, which is the whole point: their round-2 pairs are startable
        // (and, in the handler, immediately simulated too) while the human is still in game one. Only
        // the two seats still owed a round-1 result are held back.
        manager.isRoundComplete() shouldBe false
        val stillOwed = setOf(human, humanMatch.opponentOf(human))
        val earlyStartable = manager.startableMatches(everyone)
        earlyStartable.map { it.first.roundNumber }.toSet() shouldBe setOf(2)
        earlyStartable.flatMap { (_, m) -> listOfNotNull(m.player1Id, m.player2Id) }
            .none { it in stillOwed } shouldBe true

        // The human finishes. Now round 2 opens in full: no seat is still owed a round-1 game,
        // because the simulated ones are complete.
        manager.reportMatchResult("r1-human", human)
        manager.isRoundComplete() shouldBe true

        val startable = manager.startableMatches(everyone)
        startable.map { it.first.roundNumber }.toSet() shouldBe setOf(2)
        startable.flatMap { (_, m) -> listOfNotNull(m.player1Id, m.player2Id) }.toSet() shouldBe everyone
    }

    test("a simulated result is refused for a match that already has one") {
        val manager = tournament()
        val round1 = manager.startNextRound()!!
        val match = round1.matchFor(ai[0])
        val first = match.player1Id
        val second = match.opponentOf(first)

        manager.reportSimulatedResult(match, first)
        manager.reportSimulatedResult(match, second)

        match.winnerId shouldBe first
        manager.getStandingsForPersistence().getValue(first).wins shouldBe 1
        manager.getStandingsForPersistence().getValue(second).wins shouldBe 0
    }

    test("simulated matches are excluded from the replayable game list") {
        val manager = tournament()
        val round1 = manager.startNextRound()!!
        val humanMatch = round1.matchFor(human)
        humanMatch.gameSessionId = "r1-human"
        manager.reportMatchResult("r1-human", human)
        round1.matches.filter { it !== humanMatch }.forEach { manager.reportSimulatedResult(it, it.player1Id) }

        manager.getCompletedGameSessionIds() shouldContainExactly listOf("r1-human")
    }
})
