package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Quina, Qu Gourmet (FIN #194, {2}{G}, 2/3 Legendary Qu).
 *
 *   If one or more tokens would be created under your control, those tokens plus a
 *   1/1 green Frog creature token are created instead.
 *     ([com.wingedsheep.sdk.scripting.CreateAdditionalToken] with additionalTokenType = "Frog")
 *   {2}, Sacrifice a Frog: Put a +1/+1 counter on Quina.
 *
 * These tests pin: (1) the replacement adds exactly one Frog per creation event regardless
 * of token count, (2) the added Frog is a 1/1 GREEN creature (its color comes from the
 * token definition's color indicator, since tokens have no mana cost), (3) the added Frog
 * does not itself loop (CR 614.5), and (4) the sacrifice-a-Frog ability puts a +1/+1
 * counter on Quina.
 */
class QuinaQuGourmetScenarioTest : ScenarioTestBase() {

    init {
        context("Quina, Qu Gourmet") {

            test("creating two tokens yields the two tokens plus exactly one green Frog") {
                // Rally at the Hornburg ({1}{R}) creates two 1/1 white Human Soldier tokens. With
                // Quina out, that single creation event is replaced to also create one Frog.
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Quina, Qu Gourmet")
                    .withCardInHand(1, "Rally at the Hornburg")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Rally at the Hornburg")
                game.resolveStack()

                withClue("Rally at the Hornburg makes two Human Soldiers") {
                    game.findPermanents("Human Soldier Token").size shouldBe 2
                }
                withClue("Quina adds exactly one Frog (fires once per event, not per token)") {
                    game.findPermanents("Frog").size shouldBe 1
                }

                val frogId = game.findPermanents("Frog").single()
                val frog = game.state.getEntity(frogId)?.get<CardComponent>()!!
                withClue("The Frog is green") { frog.colors shouldBe setOf(Color.GREEN) }
                withClue("The Frog is a 1/1") {
                    frog.baseStats?.basePower shouldBe 1
                    frog.baseStats?.baseToughness shouldBe 1
                }
            }

            test("a token-making source yields exactly one Frog — the added Frog does not loop (CR 614.5)") {
                // Raise the Alarm ({1}{W}) creates two 1/1 white Soldier tokens. Proves no runaway
                // loop: the source's tokens + exactly one Frog, and that added Frog (itself a token)
                // does NOT re-enter the replacement to make a second Frog.
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Quina, Qu Gourmet")
                    .withCardInHand(1, "Raise the Alarm")
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Raise the Alarm")
                game.resolveStack()

                withClue("Exactly one Frog — the added Frog does not itself re-enter the replacement") {
                    game.findPermanents("Frog").size shouldBe 1
                }
            }

            test("{2}, Sacrifice a Frog: Put a +1/+1 counter on Quina") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Quina, Qu Gourmet")
                    .withCardOnBattlefield(1, "Frog", isToken = true)
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val quina = game.findPermanent("Quina, Qu Gourmet")!!
                val frog = game.findPermanents("Frog").single()
                val ability = cardRegistry.getCard("Quina, Qu Gourmet")!!.script.activatedAbilities[0]

                val act = game.execute(
                    ActivateAbility(
                        game.player1Id,
                        quina,
                        ability.id,
                        costPayment = AdditionalCostPayment(sacrificedPermanents = listOf(frog))
                    )
                )
                withClue("Activating the ability should succeed: ${act.error}") { act.error shouldBe null }
                if (game.hasPendingDecision()) game.submitManaSourcesAutoPay()
                game.resolveStack()
                while (game.hasPendingDecision()) game.resolveStack()

                withClue("The Frog is sacrificed to pay the cost") {
                    game.findPermanents("Frog").size shouldBe 0
                }
                withClue("Quina gets a +1/+1 counter") {
                    game.state.getEntity(quina)?.get<CountersComponent>()
                        ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
                }
            }
        }
    }
}
