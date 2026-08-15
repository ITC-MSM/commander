package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Champion of Dusan (TDM #137) — {2}{G} Human Warrior, 4/2, Trample.
 *
 * "Renew — {1}{G}, Exile this card from your graveyard: Put a +1/+1 counter and a trample
 *  counter on target creature. Activate only as a sorcery."
 *
 * Exercises the renew payoff: a +1/+1 counter plus a trample counter (granting Trample via
 * projection, CR 122.1c), with Champion of Dusan exiled from the graveyard as part of the cost.
 */
class ChampionOfDusanScenarioTest : ScenarioTestBase() {

    private val renewAbilityId =
        cardRegistry.getCard("Champion of Dusan")!!.activatedAbilities.first().id

    init {
        context("Champion of Dusan") {

            test("renew puts a +1/+1 counter and a trample counter (granting Trample) on a target creature") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInGraveyard(1, "Champion of Dusan")
                    .withCardOnBattlefield(1, "Glory Seeker") // 2/2, no trample
                    .withLandsOnBattlefield(1, "Forest", 2) // renew cost {1}{G}
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val champion = game.findCardsInGraveyard(1, "Champion of Dusan").first()
                val creature = game.findPermanent("Glory Seeker")!!

                withClue("Glory Seeker has no Trample before the counter is placed") {
                    game.state.projectedState.hasKeyword(creature, Keyword.TRAMPLE) shouldBe false
                }

                val activation = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = champion,
                        abilityId = renewAbilityId,
                        targets = listOf(ChosenTarget.Permanent(creature)),
                    )
                )
                withClue("Activating Champion of Dusan renew should succeed: ${activation.error}") {
                    activation.error shouldBe null
                }
                game.resolveStack()

                val counters = game.state.getEntity(creature)?.get<CountersComponent>()
                withClue("Glory Seeker gets one +1/+1 counter") {
                    (counters?.counters?.get(CounterType.PLUS_ONE_PLUS_ONE) ?: 0) shouldBe 1
                }
                withClue("Glory Seeker gets one trample counter") {
                    (counters?.counters?.get(CounterType.TRAMPLE) ?: 0) shouldBe 1
                }
                withClue("The trample counter grants the Trample keyword via projection") {
                    game.state.projectedState.hasKeyword(creature, Keyword.TRAMPLE) shouldBe true
                }
                withClue("Champion of Dusan is exiled from the graveyard as part of the cost") {
                    game.findCardsInGraveyard(1, "Champion of Dusan").size shouldBe 0
                    game.state.getExile(game.player1Id).contains(champion) shouldBe true
                }
            }
        }
    }
}
