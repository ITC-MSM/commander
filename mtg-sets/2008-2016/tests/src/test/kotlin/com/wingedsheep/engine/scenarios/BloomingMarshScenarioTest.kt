package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.PlayLand
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Blooming Marsh (KLD #243) — Land.
 *
 *   This land enters tapped unless you control two or fewer other lands.
 *   {T}: Add {B} or {G}.
 *
 * One of the "fast lands", and the `YouControlOtherAtMost` half of the conditional tapped entry —
 * the tally excludes the entering land itself, so the boundary is at *two* other lands and not at
 * a total of three.
 */
class BloomingMarshScenarioTest : ScenarioTestBase() {

    init {
        context("Blooming Marsh enters-tapped condition") {

            test("enters untapped with two other lands — the boundary case") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Blooming Marsh")
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withLandsOnBattlefield(1, "Swamp", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.execute(
                    PlayLand(
                        game.player1Id,
                        game.findCardsInHand(1, "Blooming Marsh").single()
                    )
                ).error shouldBe null

                val marsh = game.findPermanent("Blooming Marsh")!!
                withClue("Two other lands is 'two or fewer', so it enters untapped") {
                    game.state.getEntity(marsh)?.has<TappedComponent>() shouldBe false
                }
            }

            test("enters tapped with three other lands") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Blooming Marsh")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withLandsOnBattlefield(1, "Swamp", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.execute(
                    PlayLand(
                        game.player1Id,
                        game.findCardsInHand(1, "Blooming Marsh").single()
                    )
                ).error shouldBe null

                val marsh = game.findPermanent("Blooming Marsh")!!
                withClue("Three other lands is more than 'two or fewer', so it enters tapped") {
                    game.state.getEntity(marsh)?.has<TappedComponent>() shouldBe true
                }
            }

            test("the entering land is not counted against itself") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Blooming Marsh")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.execute(
                    PlayLand(
                        game.player1Id,
                        game.findCardsInHand(1, "Blooming Marsh").single()
                    )
                ).error shouldBe null

                val marsh = game.findPermanent("Blooming Marsh")!!
                withClue("No other lands at all — it enters untapped") {
                    game.state.getEntity(marsh)?.has<TappedComponent>() shouldBe false
                }
            }
        }
    }
}
