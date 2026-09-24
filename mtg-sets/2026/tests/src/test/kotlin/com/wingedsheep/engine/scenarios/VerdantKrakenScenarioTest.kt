package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.mana.IntrinsicManaAbilities
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

/**
 * Verdant Kraken (FRA #118) — {4}{G}{G}{G} Creature — Plant Kraken 6/6:
 *   At the beginning of each player's upkeep, you create a 3/3 green Forest Tentacle land creature
 *   token.
 *
 * Pins that the trigger fires on *each* player's upkeep, that the Kraken's controller — not the
 * active player — gets the token, and the token's shape: a green 3/3 land creature with the Forest
 * and Tentacle subtypes whose mana ability is the Forest's intrinsic "{T}: Add {G}."
 */
class VerdantKrakenScenarioTest : ScenarioTestBase() {

    init {
        test("each upkeep — the opponent's and then yours — makes you a Forest Tentacle") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Verdant Kraken")
                .withCardInLibrary(1, "Forest")
                .withCardInLibrary(1, "Forest")
                .withCardInLibrary(2, "Forest")
                .withCardInLibrary(2, "Forest")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
            game.state.activePlayerId shouldBe game.player2Id
            game.resolveStack()

            val tentacles = game.findAllPermanents("Forest Tentacle")
            tentacles shouldHaveSize 1
            val tentacle = tentacles.single()
            val projected = game.state.projectedState
            withClue("the Kraken's controller gets it on the opponent's upkeep") {
                projected.getController(tentacle) shouldBe game.player1Id
            }
            withClue("a green 3/3 Forest Tentacle land creature") {
                projected.isCreature(tentacle) shouldBe true
                projected.hasType(tentacle, "LAND") shouldBe true
                projected.hasSubtype(tentacle, "Forest") shouldBe true
                projected.hasSubtype(tentacle, "Tentacle") shouldBe true
                projected.getColors(tentacle) shouldBe setOf("GREEN")
                projected.getPower(tentacle) shouldBe 3
                projected.getToughness(tentacle) shouldBe 3
            }
            withClue("its {T}: Add {G} is the Forest's intrinsic mana ability") {
                IntrinsicManaAbilities.forEntity(game.state, projected, tentacle) shouldHaveSize 1
            }

            game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
            game.state.activePlayerId shouldBe game.player1Id
            game.resolveStack()

            game.findAllPermanents("Forest Tentacle") shouldHaveSize 2
        }
    }
}
