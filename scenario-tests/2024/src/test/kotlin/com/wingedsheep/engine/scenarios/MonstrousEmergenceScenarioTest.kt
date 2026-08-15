package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Monstrous Emergence (DSK #191) — "As an additional cost to cast this spell, choose a creature you
 * control or reveal a creature card from your hand. Monstrous Emergence deals damage equal to the
 * power of the creature you chose or the card you revealed to target creature."
 *
 * Exercises the `AdditionalCost.ChooseEntity` (battlefield creature / hand creature card) feeding a
 * power snapshot into `DealDamage(EntityReference.FromCostStorage)` (the Close Encounter pattern).
 */
class MonstrousEmergenceScenarioTest : ScenarioTestBase() {

    init {
        context("Monstrous Emergence — power-of-chosen-creature damage") {

            test("deals damage equal to the chosen battlefield creature's power to target creature") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Monstrous Emergence")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    // A 2/2 you control whose power (2) is the damage amount.
                    .withCardOnBattlefield(1, "Gray Ogre")
                    // The opposing creature being burned — a 2/2 that dies to 2 damage.
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val spellId = game.state.getHand(game.player1Id).first { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == "Monstrous Emergence"
                }
                val ogre = game.state.getBattlefield(game.player1Id).first { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == "Gray Ogre"
                }
                val bears = game.state.getBattlefield(game.player2Id).first { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == "Grizzly Bears"
                }

                val cast = game.execute(
                    CastSpell(
                        game.player1Id,
                        spellId,
                        listOf(ChosenTarget.Permanent(bears)),
                        additionalCostPayment = AdditionalCostPayment(beheldCards = listOf(ogre))
                    )
                )
                withClue("Cast should succeed: ${cast.error}") { cast.error shouldBe null }
                game.resolveStack()

                withClue("Grizzly Bears (2/2) died to 2 damage equal to Gray Ogre's power") {
                    game.state.getBattlefield(game.player2Id).contains(bears) shouldBe false
                    game.isInGraveyard(2, "Grizzly Bears") shouldBe true
                }
            }

            test("cannot be cast without choosing a creature for the additional cost") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Monstrous Emergence")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val spellId = game.state.getHand(game.player1Id).first { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == "Monstrous Emergence"
                }
                val bears = game.state.getBattlefield(game.player2Id).first { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == "Grizzly Bears"
                }

                // No creature chosen → the mandatory additional cost is unpaid, cast rejected.
                val cast = game.execute(
                    CastSpell(
                        game.player1Id,
                        spellId,
                        listOf(ChosenTarget.Permanent(bears)),
                        additionalCostPayment = AdditionalCostPayment(beheldCards = emptyList())
                    )
                )
                withClue("Cast should fail when no creature is chosen for the additional cost") {
                    (cast.error != null) shouldBe true
                }
            }
        }
    }
}
