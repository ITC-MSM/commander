package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.engine.state.CastSpellRecord
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Inventive Wingsmith's end-step flying-counter trigger.
 *
 * Oracle: "At the beginning of your end step, if you haven't cast a spell from your hand this turn
 * and this creature doesn't have a flying counter on it, put a flying counter on it."
 *
 * The trigger is an intervening-if (CR 603.4) gated on
 * All(Not(YouCastSpellsThisTurn(1, fromZone = HAND)), Not(SourceHasCounter("flying"))).
 */
class InventiveWingsmithScenarioTest : ScenarioTestBase() {

    private fun flyingCounters(game: TestGame): Int {
        val id = game.findPermanent("Inventive Wingsmith")!!
        return game.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.FLYING) ?: 0
    }

    init {
        context("Inventive Wingsmith end-step flying counter") {

            test("gains a flying counter when no spell was cast from hand and it has no flying counter") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Inventive Wingsmith")
                    .withActivePlayer(1)
                    .build()

                withClue("starts with no flying counter") {
                    flyingCounters(game) shouldBe 0
                }

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("end-step trigger puts a flying counter on it") {
                    flyingCounters(game) shouldBe 1
                }
            }

            test("does not trigger when a spell was cast from hand this turn") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Inventive Wingsmith")
                    .withActivePlayer(1)
                    .build()

                game.state = game.state.copy(
                    spellsCastThisTurnByPlayer = mapOf(
                        game.player1Id to listOf(
                            CastSpellRecord(
                                typeLine = TypeLine.parse("Instant"),
                                manaValue = 1,
                                colors = emptySet(),
                                isFaceDown = false,
                                castFromZone = Zone.HAND,
                            )
                        )
                    )
                )

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("no flying counter added — a spell was cast from hand") {
                    flyingCounters(game) shouldBe 0
                }
            }

            test("does not add a second flying counter when one is already present") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Inventive Wingsmith")
                    .withActivePlayer(1)
                    .build()

                // Pre-seed a flying counter so the "doesn't have a flying counter" clause is false.
                val id = game.findPermanent("Inventive Wingsmith")!!
                game.state = game.state.updateEntity(id) { container ->
                    container.with(CountersComponent(mapOf(CounterType.FLYING to 1)))
                }

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("still exactly one flying counter — the trigger didn't add another") {
                    flyingCounters(game) shouldBe 1
                }
            }
        }
    }
}
