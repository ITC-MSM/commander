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
import com.wingedsheep.sdk.dsl.renew

/**
 * Scenario test for Kheru Goldkeeper (TDM #199) — {1}{B}{G}{U} Dragon, 3/3, Flying.
 *
 * "Whenever one or more cards leave your graveyard during your turn, create a Treasure token."
 * "Renew — {2}{B}{G}{U}, Exile this card from your graveyard: Put two +1/+1 counters and a
 *  flying counter on target creature. Activate only as a sorcery."
 *
 * The leave-graveyard trigger is driven by activating *another* card's renew (Sagu Pummeler),
 * which exiles itself from the graveyard as part of its cost — a card leaving the controller's
 * graveyard during their turn — and Kheru's batching trigger then mints a Treasure. The second
 * test exercises Kheru's own renew payoff: two +1/+1 counters plus a flying counter (granting
 * Flying via projection), with Kheru exiled from the graveyard as part of the cost.
 */
class KheruGoldkeeperScenarioTest : ScenarioTestBase() {

    private val kheruRenewAbilityId =
        cardRegistry.getCard("Kheru Goldkeeper")!!.activatedAbilities.first().id
    private val saguRenewAbilityId =
        cardRegistry.getCard("Sagu Pummeler")!!.activatedAbilities.first().id

    init {
        context("Kheru Goldkeeper") {

            test("creating a Treasure when a card leaves your graveyard during your turn") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Kheru Goldkeeper")
                    .withCardInGraveyard(1, "Sagu Pummeler")
                    .withCardOnBattlefield(1, "Glory Seeker") // renew target, 2/2
                    .withLandsOnBattlefield(1, "Forest", 6)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                withClue("No Treasure exists before any card leaves the graveyard") {
                    game.findPermanent("Treasure") shouldBe null
                }

                val sagu = game.findCardsInGraveyard(1, "Sagu Pummeler").first()
                val target = game.findPermanent("Glory Seeker")!!

                // Activating Sagu's renew exiles it from the graveyard as part of the cost,
                // so a card leaves Player 1's graveyard during their turn.
                val activation = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = sagu,
                        abilityId = saguRenewAbilityId,
                        targets = listOf(ChosenTarget.Permanent(target)),
                    )
                )
                withClue("Activating Sagu Pummeler renew should succeed: ${activation.error}") {
                    activation.error shouldBe null
                }
                game.resolveStack()

                withClue("Kheru Goldkeeper's leave-graveyard trigger mints exactly one Treasure token") {
                    game.findAllPermanents("Treasure").size shouldBe 1
                }
            }

            test("renew puts two +1/+1 counters and a flying counter (granting Flying) on a target creature") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInGraveyard(1, "Kheru Goldkeeper")
                    .withCardOnBattlefield(1, "Glory Seeker") // 2/2, no flying
                    .withLandsOnBattlefield(1, "Swamp", 2)   // renew cost {2}{B}{G}{U}
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withLandsOnBattlefield(1, "Island", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val kheru = game.findCardsInGraveyard(1, "Kheru Goldkeeper").first()
                val creature = game.findPermanent("Glory Seeker")!!

                withClue("Glory Seeker has no Flying before the counter is placed") {
                    game.state.projectedState.hasKeyword(creature, Keyword.FLYING) shouldBe false
                }

                val activation = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = kheru,
                        abilityId = kheruRenewAbilityId,
                        targets = listOf(ChosenTarget.Permanent(creature)),
                    )
                )
                withClue("Activating Kheru renew should succeed: ${activation.error}") {
                    activation.error shouldBe null
                }
                game.resolveStack()

                val counters = game.state.getEntity(creature)?.get<CountersComponent>()
                withClue("Glory Seeker gets two +1/+1 counters") {
                    (counters?.counters?.get(CounterType.PLUS_ONE_PLUS_ONE) ?: 0) shouldBe 2
                }
                withClue("Glory Seeker gets one flying counter") {
                    (counters?.counters?.get(CounterType.FLYING) ?: 0) shouldBe 1
                }
                withClue("The flying counter grants the Flying keyword via projection") {
                    game.state.projectedState.hasKeyword(creature, Keyword.FLYING) shouldBe true
                }
                withClue("Kheru Goldkeeper is exiled from the graveyard as part of the cost") {
                    game.findCardsInGraveyard(1, "Kheru Goldkeeper").size shouldBe 0
                    game.state.getExile(game.player1Id).contains(kheru) shouldBe true
                }
            }
        }
    }
}
