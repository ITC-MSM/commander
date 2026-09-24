package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Fblthp, Impossibly Lost (FRA #213) — {1}{U} Legendary Creature — Homunculus 1/1.
 *   When one or more of your opponents are dealt combat damage during your turn, draw two cards.
 *   If your library has no cards in it, you win the game. Fblthp's owner shuffles him into their
 *   library. (If you draw from an empty library this way, you still win the game.)
 *
 * Pins the opponent-keyed batch trigger (once for two connecting attackers), the win on an emptied
 * library, the reminder-text case — the second draw hits an empty library and the player still
 * wins, because the deck-out loss is a state-based action (CR 704.5b) that only applies after the
 * ability has resolved — and the shuffle of Fblthp into his owner's library.
 */
class FblthpImpossiblyLostScenarioTest : ScenarioTestBase() {

    private fun builder(libraryCards: Int, withBears: Boolean = false): ScenarioBuilder {
        var b = scenario().withPlayers()
            .withCardOnBattlefield(1, "Fblthp, Impossibly Lost")
            .withActivePlayer(1)
            .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
        if (withBears) b = b.withCardOnBattlefield(1, "Grizzly Bears")
        repeat(libraryCards) { b = b.withCardInLibrary(1, "Island") }
        repeat(5) { b = b.withCardInLibrary(2, "Island") }
        return b
    }

    private fun TestGame.connect(vararg attackers: String) {
        declareAttackers(attackers.associateWith { 2 }).error shouldBe null
        passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
        declareNoBlockers().error shouldBe null
        passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
    }

    /** As [connect], but for a trigger that ends the game: pass until it is over. */
    private fun TestGame.connectAndWin(attacker: String) {
        declareAttackers(mapOf(attacker to 2)).error shouldBe null
        passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
        declareNoBlockers().error shouldBe null
        var guard = 0
        while (!state.gameOver && state.priorityPlayerId != null && guard++ < 20) passPriority()
    }

    init {
        test("dealing combat damage to an opponent draws two and shuffles Fblthp into the library") {
            val game = builder(libraryCards = 5).build()
            val hand = game.handSize(1)

            game.connect("Fblthp, Impossibly Lost")

            game.getLifeTotal(2) shouldBe 19
            game.handSize(1) shouldBe hand + 2
            game.isOnBattlefield("Fblthp, Impossibly Lost") shouldBe false
            game.findCardsInLibrary(1, "Fblthp, Impossibly Lost").size shouldBe 1
            // 5 - 2 drawn + Fblthp
            game.librarySize(1) shouldBe 4
            game.state.gameOver shouldBe false
        }

        test("two attackers connecting trigger it only once") {
            val game = builder(libraryCards = 6, withBears = true).build()
            val hand = game.handSize(1)

            game.connect("Fblthp, Impossibly Lost", "Grizzly Bears")

            game.getLifeTotal(2) shouldBe 17
            game.handSize(1) shouldBe hand + 2
        }

        test("drawing the last two cards wins the game") {
            val game = builder(libraryCards = 2).build()

            game.connectAndWin("Fblthp, Impossibly Lost")

            game.state.gameOver shouldBe true
            game.state.winnerId shouldBe game.player1Id
        }

        test("drawing from an empty library this way still wins the game") {
            val game = builder(libraryCards = 1).build()
            val hand = game.handSize(1)

            game.connectAndWin("Fblthp, Impossibly Lost")

            withClue("one card drawn, the second draw found an empty library") {
                game.handSize(1) shouldBe hand + 1
            }
            game.state.gameOver shouldBe true
            game.state.winnerId shouldBe game.player1Id
        }

        test("combat damage dealt to you on an opponent's turn doesn't trigger it") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Fblthp, Impossibly Lost")
                .withCardOnBattlefield(2, "Grizzly Bears")
                .withActivePlayer(2)
                .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(2, "Island")
                .build()
            val hand = game.handSize(1)

            game.declareAttackers(mapOf("Grizzly Bears" to 1)).error shouldBe null
            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
            game.execute(
                com.wingedsheep.engine.core.DeclareBlockers(game.player1Id, emptyMap())
            ).error shouldBe null
            game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)

            game.getLifeTotal(1) shouldBe 18
            game.handSize(1) shouldBe hand
            game.isOnBattlefield("Fblthp, Impossibly Lost") shouldBe true
        }
    }
}
