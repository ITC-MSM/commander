package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Nishoba Brawler (DMU #174) — {1}{G} Cat Warrior, power * / toughness 3, Trample.
 *
 * "Domain — Nishoba Brawler's power is equal to the number of basic land types among lands
 * you control."
 *
 * Domain is the first characteristic-defining ability (CR 604.3) in the corpus to read
 * `DynamicAmounts.domain()`, so this test pins the three things that make it different from a
 * plain count: it counts *types* rather than lands, a dual land contributes both of its types,
 * and only lands their controller controls are looked at.
 */
class NishobaBrawlerScenarioTest : ScenarioTestBase() {

    private fun brawlerId(game: TestGame): EntityId =
        game.state.getBattlefield().first { id ->
            game.state.getEntity(id)?.get<CardComponent>()?.name == "Nishoba Brawler"
        }

    init {
        context("Nishoba Brawler — power = basic land types among lands you control") {

            test("counts distinct types, not lands; toughness stays 3") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Nishoba Brawler")
                    .withLandsOnBattlefield(1, "Forest", 3)
                    .withLandsOnBattlefield(1, "Plains", 1)
                    .withActivePlayer(1)
                    .build()

                val id = brawlerId(game)

                withClue("four lands but only two basic land types → power 2") {
                    game.state.projectedState.getPower(id) shouldBe 2
                }
                withClue("toughness is fixed at 3") {
                    game.state.projectedState.getToughness(id) shouldBe 3
                }
            }

            test("a dual land contributes both of its basic land types") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Nishoba Brawler")
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withCardOnBattlefield(1, "Contaminated Aquifer") // Land — Island Swamp
                    .withActivePlayer(1)
                    .build()

                val id = brawlerId(game)

                withClue("Forest + Island + Swamp → power 3") {
                    game.state.projectedState.getPower(id) shouldBe 3
                }
            }

            test("lands an opponent controls do not count") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Nishoba Brawler")
                    .withLandsOnBattlefield(1, "Swamp", 1)
                    .withLandsOnBattlefield(2, "Island", 1)
                    .withLandsOnBattlefield(2, "Mountain", 1)
                    .withActivePlayer(1)
                    .build()

                val id = brawlerId(game)

                withClue("only the controller's Swamp counts → power 1") {
                    game.state.projectedState.getPower(id) shouldBe 1
                }
            }

            test("power is 0 with no lands") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Nishoba Brawler")
                    .withActivePlayer(1)
                    .build()

                val id = brawlerId(game)

                game.state.projectedState.getPower(id) shouldBe 0
                game.state.projectedState.getToughness(id) shouldBe 3
            }
        }
    }
}
