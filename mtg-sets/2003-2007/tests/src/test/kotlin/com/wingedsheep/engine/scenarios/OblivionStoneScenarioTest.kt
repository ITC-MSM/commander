package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.mrd.cards.OblivionStone
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/** Scenario coverage for both activated abilities of Oblivion Stone (MRD #222). */
class OblivionStoneScenarioTest : ScenarioTestBase() {
    private val fate = CounterType.FATE

    private fun setCounters(game: TestGame, id: EntityId, counters: Map<CounterType, Int>) {
        game.state = game.state.updateEntity(id) { it.with(CountersComponent(counters)) }
    }

    private fun count(game: TestGame, id: EntityId, type: CounterType): Int =
        game.state.getEntity(id)?.get<CountersComponent>()?.getCount(type) ?: 0

    init {
        context("Oblivion Stone") {
            test("first ability puts a fate counter on target permanent") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Oblivion Stone")
                    .withCardOnBattlefield(2, "Grizzly Bears", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Plains", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val stone = game.findPermanent("Oblivion Stone")!!
                val bears = game.findPermanent("Grizzly Bears")!!
                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = stone,
                        abilityId = OblivionStone.activatedAbilities[0].id,
                        targets = listOf(ChosenTarget.Permanent(bears))
                    )
                )
                withClue("the targeted ability should activate: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()

                count(game, bears, fate) shouldBe 1
            }

            test("wipe spares fate-marked permanents, preserves lands, and removes only fate counters") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Oblivion Stone")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardOnBattlefield(2, "Bonesplitter")
                    .withLandsOnBattlefield(1, "Plains", 5)
                    .withLandsOnBattlefield(2, "Forest", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val stone = game.findPermanent("Oblivion Stone")!!
                val bears = game.findPermanent("Grizzly Bears")!!
                setCounters(
                    game,
                    bears,
                    mapOf(fate to 1, CounterType.PLUS_ONE_PLUS_ONE to 2)
                )

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = stone,
                        abilityId = OblivionStone.activatedAbilities[1].id
                    )
                )
                withClue("the sacrifice ability should activate: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()

                withClue("the Stone is sacrificed as a cost and unmarked nonland permanents die") {
                    game.isInGraveyard(1, "Oblivion Stone") shouldBe true
                    game.isInGraveyard(2, "Bonesplitter") shouldBe true
                }
                withClue("a fate counter protects the permanent, then is removed") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                    count(game, bears, fate) shouldBe 0
                }
                withClue("counter cleanup is type-specific") {
                    count(game, bears, CounterType.PLUS_ONE_PLUS_ONE) shouldBe 2
                }
                withClue("lands are outside the destruction filter") {
                    game.isOnBattlefield("Plains") shouldBe true
                    game.isOnBattlefield("Forest") shouldBe true
                }
            }
        }
    }
}
