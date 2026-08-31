package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.combat.AttackingComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Faithbound Judge // Sinner's Judgment (VOW #12).
 *
 *   Front — Faithbound Judge (4/4) — Defender, flying, vigilance. At the beginning of your upkeep,
 *           if it has two or fewer judgment counters on it, put a judgment counter on it. As long
 *           as it has three or more judgment counters on it, it can attack as though it didn't have
 *           defender. Disturb {5}{W}{W}.
 *   Back  — Sinner's Judgment — Enchant player. At the beginning of your upkeep, put a judgment
 *           counter on this Aura. Then if there are three or more judgment counters on it,
 *           enchanted player loses the game.
 *
 * The two faces differ in exactly the way their printed text does, and that is what these tests
 * pin down: the creature's clause is an intervening "if" that **caps** the tally at three, while
 * the Aura's is a resolution-time "Then if" with no cap — and, being on the *Aura*, it kills the
 * player it is attached to rather than its own controller.
 */
class FaithboundJudgeScenarioTest : ScenarioTestBase() {

    private fun TestGame.judgmentCounters(id: EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.JUDGMENT) ?: 0

    /** Advance from the controller's upkeep to their *next* upkeep, resolving each one. */
    private fun TestGame.toNextOwnUpkeep() {
        passUntilPhase(Phase.ENDING, Step.END)
        passUntilPhase(Phase.BEGINNING, Step.UPKEEP) // opponent's upkeep — no accrual
        passUntilPhase(Phase.ENDING, Step.END)
        passUntilPhase(Phase.BEGINNING, Step.UPKEEP) // ours again
        resolveStack()
    }

    init {
        context("Faithbound Judge") {

            test("your upkeep adds a judgment counter, and the intervening if caps the tally at three") {
                var builder = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Faithbound Judge", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.BEGINNING, Step.UNTAP)
                repeat(20) { builder = builder.withCardInLibrary(1, "Plains") }
                repeat(20) { builder = builder.withCardInLibrary(2, "Plains") }
                val game = builder.build()

                val judge = game.findPermanent("Faithbound Judge")!!

                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.resolveStack()
                withClue("first upkeep") { game.judgmentCounters(judge) shouldBe 1 }

                game.toNextOwnUpkeep()
                withClue("second upkeep") { game.judgmentCounters(judge) shouldBe 2 }

                game.toNextOwnUpkeep()
                withClue("third upkeep") { game.judgmentCounters(judge) shouldBe 3 }

                game.toNextOwnUpkeep()
                withClue("the intervening 'if' fails at three, so a fourth counter never lands") {
                    game.judgmentCounters(judge) shouldBe 3
                }
            }

            test("three judgment counters let it attack despite defender; two do not") {
                fun judgeWith(counters: Int): Pair<TestGame, EntityId> {
                    val game = scenario()
                        .withPlayers("Player1", "Player2")
                        .withCardOnBattlefield(1, "Faithbound Judge", summoningSickness = false)
                        .withActivePlayer(1)
                        .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                        .build()
                    val judge = game.findPermanent("Faithbound Judge")!!
                    game.state = game.state.updateEntity(judge) {
                        it.with(CountersComponent(mapOf(CounterType.JUDGMENT to counters)))
                    }
                    return game to judge
                }

                val (blocked, _) = judgeWith(2)
                blocked.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                withClue("two counters — defender still applies") {
                    (blocked.declareAttackers(mapOf("Faithbound Judge" to 2)).error != null) shouldBe true
                }

                val (freed, judge) = judgeWith(3)
                freed.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                withClue("three counters — it can attack as though it didn't have defender") {
                    freed.declareAttackers(mapOf("Faithbound Judge" to 2)).error shouldBe null
                }
                withClue("and it really is attacking") {
                    freed.state.getEntity(judge)?.has<AttackingComponent>() shouldBe true
                }
            }
        }

        context("Sinner's Judgment") {

            test("the third judgment counter makes the enchanted player — not the controller — lose the game") {
                var builder = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Sinner's Judgment")
                    .withActivePlayer(1)
                    .inPhase(Phase.BEGINNING, Step.UNTAP)
                repeat(20) { builder = builder.withCardInLibrary(1, "Plains") }
                repeat(20) { builder = builder.withCardInLibrary(2, "Plains") }
                val game = builder.build()

                // "Enchant player" needs an AttachedToComponent pointing at a *player* id, which the
                // builder's permanent-host `withCardAttachedTo` can't produce.
                val curse = game.findPermanent("Sinner's Judgment")!!
                game.state = game.state.updateEntity(curse) {
                    it.with(AttachedToComponent(game.player2Id))
                }

                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.resolveStack()
                withClue("one counter — nobody has lost") {
                    game.judgmentCounters(curse) shouldBe 1
                    game.state.gameOver shouldBe false
                }

                game.toNextOwnUpkeep()
                withClue("two counters — still nobody") {
                    game.judgmentCounters(curse) shouldBe 2
                    game.state.gameOver shouldBe false
                }

                game.toNextOwnUpkeep()
                game.checkStateBasedActions()
                withClue("the third counter landed") { game.judgmentCounters(curse) shouldBe 3 }
                withClue("the third counter kills the enchanted player, leaving its controller the winner") {
                    game.state.gameOver shouldBe true
                    game.state.winnerId shouldBe game.player1Id
                }
            }
        }
    }
}
