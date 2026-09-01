package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Jaspera Sentinel (KHM #178) — {G} Creature — Elf Rogue, 1/2.
 *
 *   Reach
 *   {T}, Tap an untapped creature you control: Add one mana of any color.
 *
 * The cost has two tap halves paid together, and the Sentinel cannot fill both: the {T} symbol
 * already taps it, so it is not available as the "untapped creature you control" (CR 601.2h — the
 * costs are paid simultaneously, and one permanent cannot be tapped twice for the same payment).
 * The cost atom carries that as `excludeSelf`, which is what makes a lone Sentinel unable to
 * activate at all. The "you control" scope is the other half these tests pin: an opponent's
 * untapped creature is not a legal partner.
 */
class JasperaSentinelScenarioTest : ScenarioTestBase() {

    init {
        context("the mana ability's second tap cost") {

            test("a lone Sentinel cannot pay — it cannot tap itself twice") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Jaspera Sentinel")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val sentinel = game.findPermanent("Jaspera Sentinel")!!
                val abilityId = cardRegistry.getCard("Jaspera Sentinel")!!
                    .activatedAbilities.first().id

                val result = game.execute(
                    ActivateAbility(playerId = game.player1Id, sourceId = sentinel, abilityId = abilityId)
                )
                withClue("With no other creature to tap, the cost is unpayable") {
                    result.error shouldNotBe null
                }
            }

            test("another creature you control pays the second half and both end up tapped") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Jaspera Sentinel")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val sentinel = game.findPermanent("Jaspera Sentinel")!!
                val abilityId = cardRegistry.getCard("Jaspera Sentinel")!!
                    .activatedAbilities.first().id

                val bears = game.findPermanent("Grizzly Bears")!!

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = sentinel,
                        abilityId = abilityId,
                        costPayment = AdditionalCostPayment(tappedPermanents = listOf(bears))
                    )
                )
                withClue("The Bears can pay the second tap: ${result.error}") {
                    result.error shouldBe null
                }
                withClue("Both the Sentinel and its partner are tapped by the cost") {
                    game.state.getEntity(sentinel)?.has<TappedComponent>() shouldBe true
                    game.state.getEntity(bears)?.has<TappedComponent>() shouldBe true
                }
            }

            test("an opponent's untapped creature cannot pay the second half") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Jaspera Sentinel")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val sentinel = game.findPermanent("Jaspera Sentinel")!!
                val abilityId = cardRegistry.getCard("Jaspera Sentinel")!!
                    .activatedAbilities.first().id

                val bears = game.findPermanent("Grizzly Bears")!!

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = sentinel,
                        abilityId = abilityId,
                        costPayment = AdditionalCostPayment(tappedPermanents = listOf(bears))
                    )
                )
                withClue("\"a creature you control\" excludes the opponent's Bears") {
                    result.error shouldNotBe null
                }
            }
        }
    }
}
