package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Anje, Maid of Dishonor (VOW #231) — {2}{B}{R} Legendary Creature — Vampire, 4/5.
 *
 *   Whenever Anje and/or one or more other Vampires you control enter, create a Blood token. This
 *   ability triggers only once each turn.
 *   {2}, Sacrifice another creature or a Blood token: Each opponent loses 2 life and you gain 2 life.
 *
 * Test 1 casts Anje herself — her batching "enters" trigger includes the source (`excludeSource =
 * false`), so her own entry fires it once and creates exactly one Blood token. (Casting Anje rather
 * than another Vampire keeps the count clean: a Vampire like Bloodtithe Harvester has its own
 * Blood-making ETB that would confound the assertion.)
 * Test 2 pays {2} + sacrifices a fodder creature to drain each opponent for 2 and gain 2.
 */
class AnjeMaidOfDishonorScenarioTest : ScenarioTestBase() {

    init {
        context("Anje, Maid of Dishonor") {

            test("Anje's own entry (source counts) creates exactly one Blood token") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Anje, Maid of Dishonor") // {2}{B}{R}
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bloodBefore = game.findPermanents("Blood").size

                game.castSpell(1, "Anje, Maid of Dishonor").error shouldBe null
                game.resolveStack()

                withClue("Anje resolved onto the battlefield") {
                    game.isOnBattlefield("Anje, Maid of Dishonor") shouldBe true
                }
                withClue("Anje's enters trigger (source counts) creates exactly one Blood token") {
                    (game.findPermanents("Blood").size - bloodBefore) shouldBe 1
                }
            }

            test("{2}, Sacrifice another creature: each opponent loses 2, you gain 2") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Anje, Maid of Dishonor", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false) // fodder
                    .withLandsOnBattlefield(1, "Swamp", 2) // pays {2}
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val anje = game.findPermanent("Anje, Maid of Dishonor")!!
                val fodder = game.findPermanent("Grizzly Bears")!!
                val drainAbility = cardRegistry.getCard("Anje, Maid of Dishonor")!!
                    .activatedAbilities.first().id

                val opponentLifeBefore = game.getLifeTotal(2)
                val selfLifeBefore = game.getLifeTotal(1)

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = anje,
                        abilityId = drainAbility,
                        costPayment = AdditionalCostPayment(sacrificedPermanents = listOf(fodder)),
                    )
                )
                withClue("Activating the drain ability should succeed: ${result.error}") {
                    result.error shouldBe null
                }
                if (game.getPendingDecision() is SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                game.resolveStack()

                withClue("the sacrificed fodder went to the graveyard") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                    game.isInGraveyard(1, "Grizzly Bears") shouldBe true
                }
                withClue("each opponent loses 2 life") {
                    game.getLifeTotal(2) shouldBe (opponentLifeBefore - 2)
                }
                withClue("you gain 2 life") {
                    game.getLifeTotal(1) shouldBe (selfLifeBefore + 2)
                }
            }
        }
    }
}
