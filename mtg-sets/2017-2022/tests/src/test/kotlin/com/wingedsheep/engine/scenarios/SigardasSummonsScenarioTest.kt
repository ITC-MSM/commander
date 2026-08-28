package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Sigarda's Summons (VOW #36) — {4}{W}{W} Enchantment.
 *
 *   Creatures you control with +1/+1 counters on them have base power and toughness 4/4, have
 *   flying, and are Angels in addition to their other types.
 *
 * The point of interest is that one printed ability spans three layers, so it is authored as a
 * single `CompositeStaticAbility`. These tests pin all three (Layer 4 subtype, Layer 6 flying,
 * Layer 7b base P/T), the fact that the counters still apply on top of the base set (Layer 7d),
 * and the two ways the filter can fail: no counter, or someone else's creature.
 */
class SigardasSummonsScenarioTest : ScenarioTestBase() {

    private fun power(game: TestGame, id: EntityId): Int? = game.state.projectedState.getPower(id)
    private fun toughness(game: TestGame, id: EntityId): Int? = game.state.projectedState.getToughness(id)
    private fun hasFlying(game: TestGame, id: EntityId): Boolean =
        game.state.projectedState.hasKeyword(id, Keyword.FLYING)
    private fun isAngel(game: TestGame, id: EntityId): Boolean =
        game.state.projectedState.hasSubtype(id, "Angel")

    private fun addPlusOneCounters(game: TestGame, id: EntityId, count: Int) {
        game.state = game.state.updateEntity(id) { c ->
            c.with(
                (c.get<CountersComponent>() ?: CountersComponent())
                    .withAdded(CounterType.PLUS_ONE_PLUS_ONE, count)
            )
        }
    }

    init {
        context("Sigarda's Summons — creatures with +1/+1 counters become 4/4 flying Angels") {

            test("a countered creature you control gets all three layers") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Sigarda's Summons")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()
                addPlusOneCounters(game, bears, 1)

                withClue("Layer 7b sets base 4/4, then the single counter applies on top in 7d") {
                    power(game, bears) shouldBe 5
                    toughness(game, bears) shouldBe 5
                }
                withClue("Layer 6 grants flying") {
                    hasFlying(game, bears) shouldBe true
                }
                withClue("Layer 4 adds Angel in addition to its other types") {
                    isAngel(game, bears) shouldBe true
                    game.state.projectedState.hasSubtype(bears, "Bear") shouldBe true
                }
            }

            test("a creature with no +1/+1 counter is untouched") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Sigarda's Summons")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()

                withClue("the filter reads counters, so an uncountered creature keeps its printing") {
                    power(game, bears) shouldBe 2
                    toughness(game, bears) shouldBe 2
                    hasFlying(game, bears) shouldBe false
                    isAngel(game, bears) shouldBe false
                }
            }

            test("an opponent's countered creature is not affected") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Sigarda's Summons")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()
                addPlusOneCounters(game, bears, 2)

                withClue("'creatures you control' scopes to the enchantment's controller") {
                    power(game, bears) shouldBe 4
                    toughness(game, bears) shouldBe 4
                    hasFlying(game, bears) shouldBe false
                    isAngel(game, bears) shouldBe false
                }
            }

            test("removing the last counter reverts the creature") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Sigarda's Summons")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()
                addPlusOneCounters(game, bears, 1)
                power(game, bears) shouldBe 5

                game.state = game.state.updateEntity(bears) { c -> c.with(CountersComponent()) }

                withClue("the printed ruling: it reverts back to what it was") {
                    power(game, bears) shouldBe 2
                    toughness(game, bears) shouldBe 2
                    hasFlying(game, bears) shouldBe false
                    isAngel(game, bears) shouldBe false
                }
            }
        }
    }
}
