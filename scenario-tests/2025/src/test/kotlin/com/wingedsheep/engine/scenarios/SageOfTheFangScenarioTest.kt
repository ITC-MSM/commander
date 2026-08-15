package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Sage of the Fang (Tarkir: Dragonstorm #155).
 *
 * Sage of the Fang ({2}{G}, 2/2):
 *   ETB: put a +1/+1 counter on target creature.
 *   Renew — {3}{G}, Exile from graveyard: put a +1/+1 counter on target creature, then double
 *   the number of +1/+1 counters on that creature. Sorcery speed.
 */
class SageOfTheFangScenarioTest : ScenarioTestBase() {

    private fun plusOneCounters(game: TestGame, id: EntityId): Int =
        game.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    init {
        context("Sage of the Fang") {

            test("ETB puts a +1/+1 counter on the targeted creature") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Sage of the Fang")
                    .withLandsOnBattlefield(1, "Forest", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                val cast = game.castSpell(1, "Sage of the Fang")
                withClue("Casting Sage of the Fang should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()
                game.selectTargets(listOf(bears))
                game.resolveStack()

                withClue("Grizzly Bears should have one +1/+1 counter") {
                    plusOneCounters(game, bears) shouldBe 1
                }
            }

            test("Renew from graveyard adds a counter then doubles the total") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    // Bears already carries two +1/+1 counters; Renew adds one (→3) then doubles (→6).
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInGraveyard(1, "Sage of the Fang")
                    .withLandsOnBattlefield(1, "Forest", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                // Seed two existing counters directly.
                game.state = game.state.updateEntity(bears) { c ->
                    c.with(CountersComponent().withAdded(CounterType.PLUS_ONE_PLUS_ONE, 2))
                }

                val sage = game.state.getGraveyard(game.player1Id).first { id ->
                    game.state.getEntity(id)?.get<com.wingedsheep.engine.state.components.identity.CardComponent>()?.name == "Sage of the Fang"
                }
                val renewAbility = cardRegistry.getCard("Sage of the Fang")!!
                    .script.activatedAbilities.first().id

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = sage,
                        abilityId = renewAbility,
                        targets = listOf(ChosenTarget.Permanent(bears))
                    )
                )
                withClue("Activating Renew should succeed: ${result.error}") {
                    result.error shouldBe null
                }
                // Renew targets a creature.
                if (game.hasPendingDecision()) {
                    game.selectTargets(listOf(bears))
                }
                game.resolveStack()

                withClue("Bears should have 6 +1/+1 counters: (2 existing + 1) doubled") {
                    plusOneCounters(game, bears) shouldBe 6
                }
            }
        }
    }
}
