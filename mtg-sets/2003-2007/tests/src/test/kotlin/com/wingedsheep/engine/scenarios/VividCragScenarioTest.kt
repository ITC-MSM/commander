package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.PlayLand
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Vivid Crag (LRW #275) — the vivid-land cycle.
 *
 * "This land enters tapped with two charge counters on it." is two self-scoped replacement effects
 * on one zone change; the other four vivid lands are the same script with a different colour, so
 * this file stands for the cycle. The counter-fed mana ability is the interesting half: its cost
 * eats a charge counter, so after two activations only the mono-coloured ability remains.
 */
class VividCragScenarioTest : ScenarioTestBase() {

    private val cragDef = cardRegistry.getCard("Vivid Crag")!!
    private val redAbilityId = cragDef.activatedAbilities[0].id
    private val anyColorAbilityId = cragDef.activatedAbilities[1].id

    private fun charge(game: TestGame, id: EntityId): Int =
        game.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.CHARGE) ?: 0

    private fun pool(game: TestGame): ManaPoolComponent =
        game.state.getEntity(game.player1Id)?.get<ManaPoolComponent>() ?: ManaPoolComponent()

    init {
        context("Vivid Crag") {

            test("enters tapped with two charge counters") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Vivid Crag")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val inHand = game.findCardsInHand(1, "Vivid Crag").single()
                game.execute(PlayLand(game.player1Id, inHand)).error shouldBe null

                val crag = game.findPermanent("Vivid Crag")
                crag shouldNotBe null
                withClue("enters tapped") {
                    (game.state.getEntity(crag!!)?.get<TappedComponent>() != null).shouldBeTrue()
                }
                withClue("with two charge counters — the land, not just creatures, takes the self entry") {
                    charge(game, crag!!) shouldBe 2
                }
            }

            test("the charge-counter ability adds a colour of choice and spends one counter") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Vivid Crag")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val crag = game.findPermanent("Vivid Crag")!!
                game.state = game.state.updateEntity(crag) { c ->
                    c.with((c.get<CountersComponent>() ?: CountersComponent()).withAdded(CounterType.CHARGE, 2))
                }

                val result = game.execute(
                    ActivateAbility(game.player1Id, crag, anyColorAbilityId, manaColorChoice = Color.GREEN)
                )
                withClue("activation should succeed: ${result.error}") { result.error shouldBe null }

                pool(game).green shouldBe 1
                charge(game, crag) shouldBe 1
                (game.state.getEntity(crag)?.get<TappedComponent>() != null).shouldBeTrue()
            }

            test("with no charge counters only the red ability is affordable") {
                // The mana-ability enumerator flags rather than drops an unpayable ability; the flag
                // is what the client greys out. Before the RemoveCounters gate it read affordable
                // and the activation only failed at payment time.
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Vivid Crag")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val crag = game.findPermanent("Vivid Crag")!!
                val offered = game.getLegalActions(1)
                    .filter { (it.action as? ActivateAbility)?.sourceId == crag }
                    .associate { (it.action as ActivateAbility).abilityId to it.isAffordable }
                withClue("offered abilities: $offered") {
                    offered shouldBe mapOf(redAbilityId to true, anyColorAbilityId to false)
                }

                withClue("and the payment path agrees") {
                    game.execute(
                        ActivateAbility(game.player1Id, crag, anyColorAbilityId, manaColorChoice = Color.GREEN)
                    ).error shouldNotBe null
                }

                game.execute(ActivateAbility(game.player1Id, crag, redAbilityId)).error shouldBe null
                pool(game).red shouldBe 1
            }
        }
    }
}
