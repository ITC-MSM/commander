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
 * Scenario test for Sagu Pummeler (TDM #156) — {3}{G} Beast, 4/4, Reach.
 *
 * "Renew — {4}{G}, Exile this card from your graveyard: Put two +1/+1 counters and a reach
 *  counter on target creature. Activate only as a sorcery."
 *
 * Exercises the new [com.wingedsheep.sdk.core.CounterType.REACH] keyword counter: after the
 * renew payoff resolves, the target carries two +1/+1 counters and one reach counter, and the
 * reach counter must grant the Reach keyword via projected state (KEYWORD_COUNTER_MAP). The
 * card itself is exiled from the graveyard as part of the cost.
 */
class SaguPummelerScenarioTest : ScenarioTestBase() {

    private val renewAbilityId =
        cardRegistry.getCard("Sagu Pummeler")!!.activatedAbilities.first().id

    init {
        context("Sagu Pummeler renew") {

            test("puts two +1/+1 counters and a reach counter (granting Reach) on a target creature") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInGraveyard(1, "Sagu Pummeler")
                    .withCardOnBattlefield(1, "Glory Seeker") // 2/2, no reach
                    .withLandsOnBattlefield(1, "Forest", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val pummeler = game.findCardsInGraveyard(1, "Sagu Pummeler").first()
                val creature = game.findPermanent("Glory Seeker")!!

                withClue("Glory Seeker has no Reach before the counter is placed") {
                    game.state.projectedState.hasKeyword(creature, Keyword.REACH) shouldBe false
                }

                val activation = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = pummeler,
                        abilityId = renewAbilityId,
                        targets = listOf(ChosenTarget.Permanent(creature)),
                    )
                )
                withClue("Activating renew should succeed: ${activation.error}") {
                    activation.error shouldBe null
                }
                game.resolveStack()

                val counters = game.state.getEntity(creature)?.get<CountersComponent>()
                withClue("Glory Seeker gets two +1/+1 counters") {
                    (counters?.counters?.get(CounterType.PLUS_ONE_PLUS_ONE) ?: 0) shouldBe 2
                }
                withClue("Glory Seeker gets one reach counter") {
                    (counters?.counters?.get(CounterType.REACH) ?: 0) shouldBe 1
                }
                withClue("The reach counter grants the Reach keyword via projection") {
                    game.state.projectedState.hasKeyword(creature, Keyword.REACH) shouldBe true
                }
                withClue("Sagu Pummeler is exiled from the graveyard as part of the cost") {
                    game.findCardsInGraveyard(1, "Sagu Pummeler").size shouldBe 0
                    game.state.getExile(game.player1Id).contains(pummeler) shouldBe true
                }
            }
        }
    }
}
