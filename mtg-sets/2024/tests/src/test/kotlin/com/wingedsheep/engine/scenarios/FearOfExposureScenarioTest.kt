package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Fear of Exposure (DSK #177) — "As an additional cost to cast this spell, tap two untapped
 * creatures and/or lands you control. Trample."
 *
 * Exercises the `AdditionalCost.TapPermanents` spell additional cost over untapped creatures-or-lands
 * you control: the two chosen permanents are tapped as the spell is cast (CR 601.2f), and the spell
 * can't be cast without two untapped creatures/lands to tap.
 */
class FearOfExposureScenarioTest : ScenarioTestBase() {

    init {
        context("Fear of Exposure — tap-two additional cost") {

            test("taps the two chosen permanents and resolves onto the battlefield") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Fear of Exposure")
                    // Mana to pay {2}{G} (3 lands) plus extra untapped permanents to tap for the
                    // additional cost (the creature + one more Forest).
                    .withLandsOnBattlefield(1, "Forest", 4)
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val spellId = game.state.getHand(game.player1Id).first { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == "Fear of Exposure"
                }
                // Tap the creature and one untapped Forest (a Forest is also needed for mana, so
                // pick distinct permanents: the Grizzly Bears + one of the Forests).
                val bears = game.state.getBattlefield(game.player1Id).first { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == "Grizzly Bears"
                }
                val aForest = game.state.getBattlefield(game.player1Id).first { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == "Forest"
                }

                val cast = game.execute(
                    CastSpell(
                        game.player1Id, spellId, emptyList(),
                        additionalCostPayment = AdditionalCostPayment(tappedPermanents = listOf(bears, aForest))
                    )
                )
                withClue("Cast should succeed: ${cast.error}") { cast.error shouldBe null }
                game.resolveStack()

                withClue("Both chosen permanents are tapped by the additional cost") {
                    game.state.getEntity(bears)?.has<TappedComponent>() shouldBe true
                    game.state.getEntity(aForest)?.has<TappedComponent>() shouldBe true
                }
                withClue("Fear of Exposure resolved onto the battlefield") {
                    game.isOnBattlefield("Fear of Exposure") shouldBe true
                }
            }

            test("cannot be cast without paying the tap-two additional cost") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Fear of Exposure")
                    .withLandsOnBattlefield(1, "Forest", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val spellId = game.state.getHand(game.player1Id).first { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == "Fear of Exposure"
                }

                // No permanents tapped → the mandatory additional cost is unpaid, cast rejected.
                val cast = game.execute(
                    CastSpell(
                        game.player1Id, spellId, emptyList(),
                        additionalCostPayment = AdditionalCostPayment(tappedPermanents = emptyList())
                    )
                )
                withClue("Cast should fail when the additional cost is not paid") {
                    (cast.error != null) shouldBe true
                }
                game.isOnBattlefield("Fear of Exposure") shouldBe false
            }
        }
    }
}
