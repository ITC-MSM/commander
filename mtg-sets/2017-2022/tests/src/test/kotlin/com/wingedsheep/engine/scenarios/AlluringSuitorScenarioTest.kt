package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.DeclareAttackers
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Alluring Suitor // Deadly Dancer (VOW #141).
 *
 *   Front — Alluring Suitor (2/3) — When you attack with exactly two creatures, transform this
 *           creature.
 *   Back  — Deadly Dancer (3/3) — Trample. When this transforms into Deadly Dancer, add {R}{R};
 *           until end of turn you don't lose that mana as steps and phases end.
 *
 * "Exactly two" is the whole point of the front face: the underlying event is a *two or more*
 * attackers-declared pattern, and the upper bound rides a CR 603.2 `triggerRestriction`. So the
 * tests bracket it on both sides — one attacker (no trigger), two (flip), three (no trigger) — and
 * then check that the flip's mana actually survives the step boundary out of combat.
 */
class AlluringSuitorScenarioTest : ScenarioTestBase() {

    init {
        context("Alluring Suitor") {

            /** Suitor plus [extraAttackers] vanilla creatures, ready to attack. */
            fun board(extraAttackers: Int): TestGame {
                var builder = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Alluring Suitor", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(extraAttackers) {
                    builder = builder.withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                }
                return builder.build()
            }

            fun attackWith(game: TestGame, names: List<String>) {
                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(names.associateWith { 2 }).error shouldBe null
                game.resolveStack()
            }

            fun manaTotal(game: TestGame): Int {
                val pool = game.state.getEntity(game.player1Id)?.get<ManaPoolComponent>()
                    ?: ManaPoolComponent()
                return pool.white + pool.blue + pool.black + pool.red + pool.green + pool.colorless
            }

            fun faceOf(game: TestGame): String =
                game.state.getEntity(game.findPermanent("Alluring Suitor") ?: game.findPermanent("Deadly Dancer")!!)!!
                    .get<CardComponent>()!!.name

            test("attacking with exactly two creatures transforms the Suitor") {
                val game = board(extraAttackers = 1)
                attackWith(game, listOf("Alluring Suitor", "Grizzly Bears"))
                withClue("two attackers — the trigger's restriction is met") {
                    faceOf(game) shouldBe "Deadly Dancer"
                }
            }

            test("attacking alone does not transform it") {
                val game = board(extraAttackers = 0)
                attackWith(game, listOf("Alluring Suitor"))
                withClue("one attacker is below the event's own two-attacker floor") {
                    faceOf(game) shouldBe "Alluring Suitor"
                }
            }

            test("attacking with three creatures does not transform it") {
                val game = board(extraAttackers = 2)
                // Two distinct Bears exist; declareAttackers resolves by name, so attack with the
                // Suitor and both of them by declaring each Bears entity directly.
                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                val suitor = game.findPermanent("Alluring Suitor")!!
                val bears = game.findAllPermanents("Grizzly Bears")
                bears.size shouldBe 2
                game.execute(
                    DeclareAttackers(
                        game.state.activePlayerId!!,
                        (listOf(suitor) + bears).associateWith { game.player2Id }
                    )
                ).error shouldBe null
                game.resolveStack()
                withClue("the event fires at two-or-more, but 'exactly two' rejects three") {
                    faceOf(game) shouldBe "Alluring Suitor"
                }
            }

            test("the transform trigger's {R}{R} survives the end of the combat phase") {
                val game = board(extraAttackers = 1)
                attackWith(game, listOf("Alluring Suitor", "Grizzly Bears"))
                faceOf(game) shouldBe "Deadly Dancer"

                withClue("the trigger added {R}{R} to its controller's pool") {
                    manaTotal(game) shouldBe 2
                }

                game.advanceToPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
                withClue("'you don't lose this mana as steps and phases end' — it is still there") {
                    manaTotal(game) shouldBe 2
                }
            }
        }
    }
}
