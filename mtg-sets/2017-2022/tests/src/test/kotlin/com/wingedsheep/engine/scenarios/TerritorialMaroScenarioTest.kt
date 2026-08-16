package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Territorial Maro (DMU #184) — {4}{G} Elemental, power * / toughness *.
 *
 * "Domain — Territorial Maro's power and toughness are each equal to twice the number of basic
 * land types among lands you control."
 *
 * Both halves of the printed `*`/`*` are the *same* doubled domain count, so this test pins the
 * multiplier on each half independently — a card that doubled only power would still look right
 * on the board.
 */
class TerritorialMaroScenarioTest : ScenarioTestBase() {

    private fun maroId(game: TestGame): EntityId =
        game.state.getBattlefield().first { id ->
            game.state.getEntity(id)?.get<CardComponent>()?.name == "Territorial Maro"
        }

    init {
        context("Territorial Maro — power and toughness are each twice your domain") {

            test("one basic land type → 2/2") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Territorial Maro")
                    .withLandsOnBattlefield(1, "Forest", 4)
                    .withActivePlayer(1)
                    .build()

                val id = maroId(game)

                withClue("four Forests are still one basic land type → 2/2") {
                    game.state.projectedState.getPower(id) shouldBe 2
                    game.state.projectedState.getToughness(id) shouldBe 2
                }
            }

            test("all five basic land types → 10/10") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Territorial Maro")
                    .withLandsOnBattlefield(1, "Plains", 1)
                    .withLandsOnBattlefield(1, "Island", 1)
                    .withLandsOnBattlefield(1, "Swamp", 1)
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withActivePlayer(1)
                    .build()

                val id = maroId(game)

                withClue("domain caps at five types → 10/10") {
                    game.state.projectedState.getPower(id) shouldBe 10
                    game.state.projectedState.getToughness(id) shouldBe 10
                }
            }

            test("lands an opponent controls do not count") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Territorial Maro")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withLandsOnBattlefield(2, "Plains", 1)
                    .withLandsOnBattlefield(2, "Island", 1)
                    .withActivePlayer(1)
                    .build()

                val id = maroId(game)

                withClue("only the controller's Mountain counts → 2/2") {
                    game.state.projectedState.getPower(id) shouldBe 2
                    game.state.projectedState.getToughness(id) shouldBe 2
                }
            }
        }
    }
}
