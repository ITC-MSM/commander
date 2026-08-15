package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.sos.cards.EmilVastlandsRoamer
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Emil, Vastlands Roamer (Secrets of Strixhaven #146).
 *
 * Emil ({2}{G} Legendary Creature — Elf Druid, 3/3):
 *   Creatures you control with +1/+1 counters on them have trample.
 *   {4}{G}, {T}: Create a 0/0 green and blue Fractal creature token. Put X +1/+1 counters on it,
 *     where X is the number of differently named lands you control.
 *
 * Exercises the activated ability's dynamic-counter-on-the-created-token recipe (X = differently
 * named lands) and, on that same freshly counter-bearing Fractal, the lord granting trample.
 */
class EmilVastlandsRoamerScenarioTest : ScenarioTestBase() {

    private val abilityId = EmilVastlandsRoamer.activatedAbilities.first().id

    private fun plusOneCounters(game: TestGame, id: EntityId): Int =
        game.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    init {
        context("Emil, Vastlands Roamer") {

            test("activated ability makes a Fractal with X counters = differently named lands, then it has trample") {
                // Five Forests share a name → only one distinct name among the Forests; adding a
                // Mountain and a Plains makes three differently named lands → X = 3.
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Emil, Vastlands Roamer")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Forest", 3)
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withLandsOnBattlefield(1, "Plains", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val emil = game.findPermanent("Emil, Vastlands Roamer")!!
                val bears = game.findPermanent("Grizzly Bears")!!

                withClue("A counterless creature you control has no trample") {
                    game.state.projectedState.hasKeyword(bears, Keyword.TRAMPLE) shouldBe false
                }

                game.execute(
                    ActivateAbility(playerId = game.player1Id, sourceId = emil, abilityId = abilityId),
                ).error shouldBe null
                game.resolveStack()

                val fractal = game.findPermanent("Fractal Token")
                withClue("The activated ability creates a Fractal token") {
                    (fractal != null) shouldBe true
                }
                withClue("X = 3 differently named lands → three +1/+1 counters on the Fractal") {
                    plusOneCounters(game, fractal!!) shouldBe 3
                }
                withClue("The counter-bearing Fractal you control gains trample from Emil's lord") {
                    game.state.projectedState.hasKeyword(fractal!!, Keyword.TRAMPLE) shouldBe true
                }
            }
        }
    }
}
