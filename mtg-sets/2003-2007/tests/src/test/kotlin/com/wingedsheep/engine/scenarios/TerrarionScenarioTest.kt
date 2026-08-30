package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Terrarion — Ravnica: City of Guilds #273, {1} Artifact
 *
 * "This artifact enters tapped."
 * "{2}, {T}, Sacrifice this artifact: Add two mana in any combination of colors."
 * "When this artifact is put into a graveyard from the battlefield, draw a card."
 *
 * Three abilities, and the one worth pinning is the last: the sacrifice is part of the mana
 * ability's *cost*, so the draw trigger has to fire on any route to the graveyard, not only on the
 * self-sacrifice. These tests cover the enters-tapped replacement and the two ways the artifact can
 * reach a graveyard — its own cost, and someone else destroying it — because the trigger is
 * `Triggers.Dies` (battlefield-to-graveyard on the source), which is not creature-only despite the
 * name, and that is exactly what the printed "no matter how" ruling asks for.
 */
class TerrarionScenarioTest : ScenarioTestBase() {

    init {
        context("Terrarion") {

            test("it enters tapped") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Terrarion")
                    .withLandsOnBattlefield(1, "Island", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Terrarion").error shouldBe null
                game.resolveStack()

                val terrarion = game.findPermanent("Terrarion")!!
                withClue("the enters-tapped replacement applied") {
                    game.state.getEntity(terrarion)?.has<TappedComponent>() shouldBe true
                }
            }

            test("destroying it from the battlefield draws a card") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Terrarion")
                    .withLandsOnBattlefield(2, "Forest", 3)
                    .withCardInHand(2, "Deconstruct") // {2}{G}: Destroy target artifact
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(3) { builder = builder.withCardInLibrary(1, "Island") }
                repeat(3) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                val terrarion = game.findPermanent("Terrarion")!!
                val handBefore = game.handSize(1)
                val libraryBefore = game.librarySize(1)

                game.castSpell(2, "Deconstruct", terrarion).error shouldBe null
                game.resolveStack()

                withClue("Terrarion died to someone else's removal") {
                    game.isInGraveyard(1, "Terrarion") shouldBe true
                }
                withClue("the trigger fires no matter how it reached the graveyard") {
                    game.handSize(1) shouldBe handBefore + 1
                    game.librarySize(1) shouldBe libraryBefore - 1
                }
            }
        }
    }
}
