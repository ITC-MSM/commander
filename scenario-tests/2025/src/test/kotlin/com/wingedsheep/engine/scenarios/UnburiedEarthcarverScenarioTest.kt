package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Unburied Earthcarver (TDM #95) — {1}{B} Human Warrior, 2/2.
 *
 * "{2}, Sacrifice another creature: Put a +1/+1 counter on this creature."
 *
 * Activating the ability pays {2} plus sacrificing another creature; the source then gains a
 * +1/+1 counter. The fodder creature must be *another* creature (not the Earthcarver itself).
 */
class UnburiedEarthcarverScenarioTest : ScenarioTestBase() {

    private val earthcarverAbilityId =
        cardRegistry.getCard("Unburied Earthcarver")!!.activatedAbilities.first().id

    init {
        context("Unburied Earthcarver") {

            test("activated ability sacrifices another creature and adds a +1/+1 counter") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Unburied Earthcarver")
                    .withCardOnBattlefield(1, "Glory Seeker") // fodder, 2/2
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val earthcarver = game.findPermanent("Unburied Earthcarver")!!
                val fodder = game.findPermanent("Glory Seeker")!!

                withClue("Earthcarver has no +1/+1 counters before activation") {
                    val counters = game.state.getEntity(earthcarver)?.get<CountersComponent>()
                    (counters?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0) shouldBe 0
                }

                val activation = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = earthcarver,
                        abilityId = earthcarverAbilityId,
                        costPayment = AdditionalCostPayment(sacrificedPermanents = listOf(fodder)),
                    )
                )
                withClue("Activating Unburied Earthcarver should succeed: ${activation.error}") {
                    activation.error shouldBe null
                }
                game.resolveStack()

                withClue("Glory Seeker should be sacrificed to the graveyard") {
                    game.findPermanents("Glory Seeker").contains(fodder) shouldBe false
                    game.findCardsInGraveyard(1, "Glory Seeker").size shouldBe 1
                }
                withClue("Unburied Earthcarver gains a +1/+1 counter") {
                    val counters = game.state.getEntity(earthcarver)?.get<CountersComponent>()
                    (counters?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0) shouldBe 1
                }
                withClue("Earthcarver is now a 3/3") {
                    game.state.projectedState.getPower(earthcarver) shouldBe 3
                    game.state.projectedState.getToughness(earthcarver) shouldBe 3
                }
            }
        }
    }
}
