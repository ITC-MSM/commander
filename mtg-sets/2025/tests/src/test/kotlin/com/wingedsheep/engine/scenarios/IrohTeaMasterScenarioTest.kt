package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.TargetsResponse
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Iroh, Tea Master (TLA #228, {1}{R}{W}, 2/2).
 *
 *   When Iroh enters, create a Food token.
 *   At the beginning of combat on your turn, you may have target opponent gain control of target
 *   permanent you control. When you do, create a 1/1 white Ally creature token. Put a +1/+1 counter
 *   on that token for each permanent you own that your opponents control.
 *
 * Verifies:
 *  - the ETB Food token;
 *  - the begin-combat give-control → reflexive 1/1 Ally token whose +1/+1 counters equal the number
 *    of permanents you own that your opponents control. After donating a permanent, that permanent
 *    (still owned by you, now controlled by the opponent) is exactly one such permanent, so the
 *    token enters as a 2/2 — pinning the owner ≠ controller dynamic-amount composition
 *    (AggregateBattlefield over opponents' permanents filtered to OwnedByYou);
 *  - declining the optional "you may" leaves control unchanged and creates no token.
 */
class IrohTeaMasterScenarioTest : ScenarioTestBase() {

    init {
        test("ETB creates a Food token") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardInHand(1, "Iroh, Tea Master")
                .withLandsOnBattlefield(1, "Mountain", 2)
                .withLandsOnBattlefield(1, "Plains", 1)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            game.castSpell(1, "Iroh, Tea Master").error shouldBe null
            game.resolveStack()

            withClue("Iroh's ETB creates one Food token") {
                game.findPermanents("Food").size shouldBe 1
            }
        }

        test("begin combat: donate a permanent, then make a 1/1 Ally with +1/+1 counters per permanent you own an opponent controls") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Iroh, Tea Master", summoningSickness = false)
                .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val bears = game.findPermanent("Grizzly Bears")!!

            // Advance to begin-of-combat. The optional "you may" is asked first (the trigger is
            // optional), then — only if accepted — the two targets are chosen (slot 0 = target
            // opponent, slot 1 = target permanent you control).
            game.passUntilPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
            (game.getPendingDecision() is YesNoDecision) shouldBe true
            game.answerYesNo(true).error shouldBe null

            val td = game.getPendingDecision()!!
            game.submitDecision(
                TargetsResponse(td.id, mapOf(0 to listOf(game.player2Id), 1 to listOf(bears)))
            ).error shouldBe null
            game.resolveStack()

            withClue("the opponent now controls the donated Grizzly Bears") {
                game.state.projectedState.getController(bears) shouldBe game.player2Id
            }

            val ally = game.findPermanent("Ally Token")!!
            withClue("the token has one +1/+1 counter (one permanent you own that an opponent controls)") {
                game.state.getEntity(ally)?.get<CountersComponent>()
                    ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
            }
            withClue("so the 1/1 white Ally token is a 2/2") {
                game.state.projectedState.getPower(ally) shouldBe 2
                game.state.projectedState.getToughness(ally) shouldBe 2
            }
        }

        test("declining the optional give-control leaves control unchanged and creates no token") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Iroh, Tea Master", summoningSickness = false)
                .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val bears = game.findPermanent("Grizzly Bears")!!

            // Declining the optional "you may" at begin-of-combat: no targets are requested and the
            // payoff never fires.
            game.passUntilPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
            (game.getPendingDecision() is YesNoDecision) shouldBe true
            game.answerYesNo(false).error shouldBe null
            game.resolveStack()

            withClue("control of Grizzly Bears stays with you") {
                game.state.projectedState.getController(bears) shouldBe game.player1Id
            }
            withClue("no Ally token is created when you decline") {
                game.findPermanents("Ally Token").size shouldBe 0
            }
        }
    }
}
