package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.PlayLand
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Blanchwood Armor — {2}{G} Aura.
 * "Enchanted creature gets +1/+1 for each Forest you control."
 *
 * Proves the buff scales dynamically with the number of Forests controlled: a 2/2
 * enchanted creature with 3 Forests becomes 5/5, and playing a 4th Forest makes it 6/6.
 */
class BlanchwoodArmorScenarioTest : ScenarioTestBase() {

    private val stateProjector = StateProjector()

    init {
        test("enchanted creature gets +1/+1 per Forest and scales when a Forest enters") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardInHand(1, "Blanchwood Armor")
                .withCardInHand(1, "Forest")
                .withLandsOnBattlefield(1, "Forest", 3)
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val bears = game.findPermanent("Grizzly Bears")!!

            // Cast Blanchwood Armor on Grizzly Bears (2/2). 3 Forests provide {2}{G}.
            val castResult = game.castSpell(1, "Blanchwood Armor", bears)
            withClue("Blanchwood Armor should cast successfully: ${castResult.error}") {
                castResult.error shouldBe null
            }
            game.resolveStack()

            withClue("Blanchwood Armor should be on the battlefield (attached)") {
                game.findPermanent("Blanchwood Armor").shouldNotBeNull()
            }

            // 2/2 base + (3 Forests) = 5/5
            withClue("Grizzly Bears power with 3 Forests (2 + 3)") {
                stateProjector.getProjectedPower(game.state, bears) shouldBe 5
            }
            withClue("Grizzly Bears toughness with 3 Forests (2 + 3)") {
                stateProjector.getProjectedToughness(game.state, bears) shouldBe 5
            }

            // Play a 4th Forest from hand.
            val forestInHand = game.findCardsInHand(1, "Forest").first()
            val playResult = game.execute(PlayLand(game.player1Id, forestInHand))
            withClue("Playing a 4th Forest should succeed: ${playResult.error}") {
                playResult.error shouldBe null
            }

            // 2/2 base + (4 Forests) = 6/6 — the buff scales dynamically.
            withClue("Grizzly Bears power with 4 Forests (2 + 4)") {
                stateProjector.getProjectedPower(game.state, bears) shouldBe 6
            }
            withClue("Grizzly Bears toughness with 4 Forests (2 + 4)") {
                stateProjector.getProjectedToughness(game.state, bears) shouldBe 6
            }
        }
    }
}
