package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.lci.cards.TinkersTote
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Tinker's Tote (LCI #40) — {2}{W} Artifact, Common.
 *
 * "When this artifact enters, create two 1/1 colorless Gnome artifact creature tokens.
 *  {W}, Sacrifice this artifact: You gain 3 life."
 *
 * Exercises:
 *  - ETB trigger: casting Tinker's Tote creates two 1/1 colorless Gnome artifact creature tokens.
 *  - Activated ability: {W}, Sacrifice this artifact → controller gains 3 life, the Tote leaves
 *    the battlefield.
 */
class TinkersToteScenarioTest : ScenarioTestBase() {

    init {
        // Tinker's Tote is auto-discovered from the LCI cards package (already in the shared
        // cardRegistry). No explicit registration needed.
        val activateAbilityId = TinkersTote.activatedAbilities.first().id
        val projector = StateProjector()

        /** All Gnome tokens on the battlefield. */
        fun gnomeTokens(game: TestGame): List<EntityId> =
            game.state.getBattlefield().filter { id ->
                val e = game.state.getEntity(id) ?: return@filter false
                e.has<TokenComponent>() && e.get<CardComponent>()?.name == "Gnome Token"
            }

        context("Tinker's Tote") {

            test("enters and creates two 1/1 colorless Gnome artifact creature tokens") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Tinker's Tote")
                    .withLandsOnBattlefield(1, "Plains", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Tinker's Tote").error shouldBe null
                game.resolveStack()

                withClue("Tinker's Tote is on the battlefield") {
                    (game.findPermanent("Tinker's Tote") != null) shouldBe true
                }
                withClue("ETB creates exactly two Gnome tokens") {
                    gnomeTokens(game).size shouldBe 2
                }

                // Each token is a 1/1 colorless Gnome artifact creature.
                val token = gnomeTokens(game).first()
                val card = game.state.getEntity(token)!!.get<CardComponent>()!!
                card.typeLine.isArtifact shouldBe true
                card.typeLine.isCreature shouldBe true
                card.typeLine.subtypes.map { it.value } shouldBe listOf("Gnome")
                card.colors shouldBe emptySet()
                projector.getProjectedPower(game.state, token) shouldBe 1
                projector.getProjectedToughness(game.state, token) shouldBe 1
            }

            test("{W}, Sacrifice this artifact: You gain 3 life") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Tinker's Tote")
                    .withLandsOnBattlefield(1, "Plains", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val tote = game.findPermanent("Tinker's Tote")!!
                withClue("starting life is 20") { game.getLifeTotal(1) shouldBe 20 }

                val result = game.execute(
                    ActivateAbility(playerId = game.player1Id, sourceId = tote, abilityId = activateAbilityId)
                )
                result.error shouldBe null
                if (game.getPendingDecision() is SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                game.resolveStack()

                withClue("controller gains 3 life (20 → 23)") {
                    game.getLifeTotal(1) shouldBe 23
                }
                withClue("Tinker's Tote was sacrificed") {
                    game.state.getBattlefield().contains(tote) shouldBe false
                }
            }
        }
    }
}
