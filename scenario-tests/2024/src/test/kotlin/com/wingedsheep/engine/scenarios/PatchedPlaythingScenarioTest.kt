package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Patched Plaything (DSK #24) — {2}{W} Artifact Creature — Toy, 4/3, double strike.
 *
 * "This creature enters with two -1/-1 counters on it if you cast it from your hand."
 *
 * The conditional enter-with-counters is a replacement effect gated on
 * [com.wingedsheep.sdk.dsl.Conditions.WasCastFromHand]: a hand-cast enters with two -1/-1 counters
 * (a 2/1), while a permanent put onto the battlefield without being cast from hand enters at full
 * 4/3.
 */
class PatchedPlaythingScenarioTest : ScenarioTestBase() {

    private fun minusCounters(game: TestGame, id: EntityId): Int =
        game.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.MINUS_ONE_MINUS_ONE) ?: 0

    init {
        context("Patched Plaything") {
            test("cast from hand: enters with two -1/-1 counters (a 2/1) and has double strike") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Patched Plaything")
                    .withLandsOnBattlefield(1, "Plains", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpell(1, "Patched Plaything")
                withClue("Casting Patched Plaything should succeed: ${cast.error}") { cast.error shouldBe null }
                game.resolveStack()

                val id = game.findPermanent("Patched Plaything")!!
                withClue("Two -1/-1 counters from the hand-cast condition") {
                    minusCounters(game, id) shouldBe 2
                }
                withClue("4/3 base minus two -1/-1 counters = 2/1") {
                    game.state.projectedState.getPower(id) shouldBe 2
                    game.state.projectedState.getToughness(id) shouldBe 1
                }
                withClue("Double strike is present") {
                    game.state.projectedState.hasKeyword(id, Keyword.DOUBLE_STRIKE) shouldBe true
                }
            }

            test("put onto the battlefield without being cast from hand: no counters (full 4/3)") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Patched Plaything")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val id = game.findPermanent("Patched Plaything")!!
                withClue("Not cast from hand → no -1/-1 counters") {
                    minusCounters(game, id) shouldBe 0
                }
                withClue("Full base stats 4/3") {
                    game.state.projectedState.getPower(id) shouldBe 4
                    game.state.projectedState.getToughness(id) shouldBe 3
                }
            }
        }
    }
}
