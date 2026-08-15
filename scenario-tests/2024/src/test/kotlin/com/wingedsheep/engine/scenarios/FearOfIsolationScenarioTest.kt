package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Fear of Isolation (DSK #58) — "As an additional cost to cast this spell, return a permanent you
 * control to its owner's hand. Flying."
 *
 * Exercises the `AdditionalCost.Atom(CostAtom.ReturnToHand)` spell additional cost: the chosen
 * permanent is returned to its owner's hand as the spell is cast (CR 601.2f), and the spell can't
 * be cast without a permanent to return.
 */
class FearOfIsolationScenarioTest : ScenarioTestBase() {

    init {
        context("Fear of Isolation — return-a-permanent additional cost") {

            test("returns the chosen permanent to hand and resolves onto the battlefield") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Fear of Isolation")
                    // A creature the caster can return to pay the additional cost.
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Island", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val spellId = game.state.getHand(game.player1Id).first { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == "Fear of Isolation"
                }
                val bears = game.state.getBattlefield(game.player1Id).first { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == "Grizzly Bears"
                }

                val cast = game.execute(
                    CastSpell(
                        game.player1Id, spellId, emptyList(),
                        additionalCostPayment = AdditionalCostPayment(bouncedPermanents = listOf(bears))
                    )
                )
                withClue("Cast should succeed: ${cast.error}") { cast.error shouldBe null }
                game.resolveStack()

                withClue("Grizzly Bears was returned to hand as the additional cost") {
                    game.state.getBattlefield(game.player1Id).contains(bears) shouldBe false
                    game.isInHand(1, "Grizzly Bears") shouldBe true
                }
                withClue("Fear of Isolation resolved onto the battlefield") {
                    game.isOnBattlefield("Fear of Isolation") shouldBe true
                }
            }

            test("cannot be cast without paying the return-a-permanent additional cost") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Fear of Isolation")
                    // Mana is available (the lands are also valid return targets), but the cast
                    // supplies no bounce, so the mandatory additional cost is unpaid.
                    .withLandsOnBattlefield(1, "Island", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val spellId = game.state.getHand(game.player1Id).first { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == "Fear of Isolation"
                }

                // No permanent returned → the mandatory additional cost is unpaid, cast rejected.
                val cast = game.execute(
                    CastSpell(
                        game.player1Id, spellId, emptyList(),
                        additionalCostPayment = AdditionalCostPayment(bouncedPermanents = emptyList())
                    )
                )
                withClue("Cast should fail when the additional cost is not paid") {
                    (cast.error != null) shouldBe true
                }
                game.isOnBattlefield("Fear of Isolation") shouldBe false
            }
        }
    }
}
