package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Enigma Drake (AKH #198, reprinted in FDN) — {1}{U}{R} Drake, power * / toughness 4, Flying.
 *
 * "Enigma Drake's power is equal to the number of instant and sorcery cards in your graveyard."
 *
 * Characteristic-defining ability (CR 604.3): the power counts instants and sorceries in the
 * controller's graveyard while toughness stays a fixed 4. Creature and other cards in the
 * graveyard must not count.
 */
class EnigmaDrakeScenarioTest : ScenarioTestBase() {

    private fun drakeId(game: TestGame): EntityId =
        game.state.getBattlefield().first { id ->
            game.state.getEntity(id)?.get<CardComponent>()?.name == "Enigma Drake"
        }

    init {
        context("Enigma Drake — power = instant/sorcery cards in your graveyard") {

            test("power counts only instants and sorceries in your graveyard; toughness stays 4") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Enigma Drake")
                    .withCardInGraveyard(1, "Lightning Bolt") // instant
                    .withCardInGraveyard(1, "Divination")     // sorcery
                    .withCardInGraveyard(1, "Grizzly Bears")  // creature — excluded
                    .withActivePlayer(1)
                    .build()

                val id = drakeId(game)

                withClue("2 instant/sorcery cards → power 2 (Grizzly Bears excluded)") {
                    game.state.projectedState.getPower(id) shouldBe 2
                }
                withClue("toughness is fixed at 4") {
                    game.state.projectedState.getToughness(id) shouldBe 4
                }
            }

            test("power is 0 with an empty graveyard") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Enigma Drake")
                    .withActivePlayer(1)
                    .build()

                val id = drakeId(game)

                withClue("no instants/sorceries → power 0") {
                    game.state.projectedState.getPower(id) shouldBe 0
                }
                game.state.projectedState.getToughness(id) shouldBe 4
            }

            test("an opponent's instants and sorceries do not count") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Enigma Drake")
                    .withCardInGraveyard(1, "Lightning Bolt")
                    .withCardInGraveyard(2, "Divination") // opponent's graveyard
                    .withActivePlayer(1)
                    .build()

                val id = drakeId(game)

                withClue("only the controller's graveyard counts → power 1") {
                    game.state.projectedState.getPower(id) shouldBe 1
                }
            }
        }
    }
}
