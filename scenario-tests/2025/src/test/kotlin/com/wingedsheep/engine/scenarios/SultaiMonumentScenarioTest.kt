package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Scenario tests for Sultai Monument (TDM #247).
 *
 * "{2} Artifact — When this artifact enters, search your library for a basic Swamp, Forest, or
 *  Island card, reveal it, put it into your hand, then shuffle.
 *  {2}{B}{G}{U}, {T}, Sacrifice this artifact: Create two 2/2 black Zombie Druid creature tokens.
 *  Activate only as a sorcery."
 */
class SultaiMonumentScenarioTest : ScenarioTestBase() {

    private val stateProjector = StateProjector()

    init {
        context("Sultai Monument") {

            test("ETB searches for a basic Swamp/Forest/Island and puts it into hand") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Sultai Monument")
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(1, "Mountain")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpell(1, "Sultai Monument")
                withClue("Casting Sultai Monument should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                val decision = game.getPendingDecision()
                decision.shouldBeInstanceOf<SelectCardsDecision>()
                withClue("Only the basic Forest should be a legal choice (Mountain is excluded)") {
                    decision.options.mapNotNull {
                        game.state.getEntity(it)?.get<CardComponent>()?.name
                    }.toSet() shouldBe setOf("Forest")
                }
                game.selectCards(listOf(decision.options.first()))
                game.resolveStack()

                withClue("The searched Forest should be in hand") {
                    game.state.getHand(game.player1Id).count { id ->
                        game.state.getEntity(id)?.get<CardComponent>()?.name == "Forest"
                    } shouldBe 1
                }
            }

            test("sacrifice ability makes two 2/2 black Zombie Druid tokens") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Sultai Monument")
                    .withLandsOnBattlefield(1, "Swamp", 1)
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withLandsOnBattlefield(1, "Island", 1)
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val monumentId = game.findPermanent("Sultai Monument")!!
                val cardDef = cardRegistry.getCard("Sultai Monument")!!
                val tokenAbility = cardDef.script.activatedAbilities[0]

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = monumentId,
                        abilityId = tokenAbility.id
                    )
                )
                withClue("Activating the token ability should succeed: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()

                withClue("Sultai Monument should be sacrificed") {
                    game.isOnBattlefield("Sultai Monument") shouldBe false
                }

                val tokens = game.findPermanents("Zombie Druid Token")
                withClue("Two 2/2 black Zombie Druid tokens should exist") {
                    tokens.size shouldBe 2
                }

                val projected = stateProjector.project(game.state)
                tokens.forEach { id ->
                    withClue("Each token should be 2/2") {
                        projected.getPower(id) shouldBe 2
                        projected.getToughness(id) shouldBe 2
                    }
                }
            }
        }
    }
}
