package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Scenario tests for Abzan Monument (TDM #238).
 *
 * "{2} Artifact — When this artifact enters, search your library for a basic Plains, Swamp,
 *  or Forest card, reveal it, put it into your hand, then shuffle.
 *  {1}{W}{B}{G}, {T}, Sacrifice this artifact: Create an X/X white Spirit creature token,
 *  where X is the greatest toughness among creatures you control. Activate only as a sorcery."
 */
class AbzanMonumentScenarioTest : ScenarioTestBase() {

    // A vanilla 0/5 wall whose toughness drives X for the token ability.
    private val sturdyWall = card("Sturdy Test Wall") {
        manaCost = "{2}"
        typeLine = "Creature — Wall"
        power = 0
        toughness = 5
    }

    private val stateProjector = StateProjector()

    init {
        cardRegistry.register(sturdyWall)

        context("Abzan Monument") {

            test("ETB searches for a basic Plains/Swamp/Forest and puts it into hand") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Abzan Monument")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(1, "Island")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpell(1, "Abzan Monument")
                withClue("Casting Abzan Monument should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                // ETB search surfaces a selection; the only eligible basic is the Forest.
                val decision = game.getPendingDecision()
                decision.shouldBeInstanceOf<SelectCardsDecision>()
                game.selectCards(listOf(decision.options.first()))
                game.resolveStack()

                withClue("The searched Forest should be in hand") {
                    game.state.getHand(game.player1Id).count { id ->
                        game.state.getEntity(id)?.get<CardComponent>()?.name == "Forest"
                    } shouldBe 1
                }
            }

            test("sacrifice ability makes an X/X white Spirit where X is greatest toughness") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Abzan Monument")
                    // A 0/5 wall → greatest toughness among creatures you control is 5.
                    .withCardOnBattlefield(1, "Sturdy Test Wall", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Plains", 1)
                    .withLandsOnBattlefield(1, "Swamp", 1)
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val monumentId = game.findPermanent("Abzan Monument")!!
                val cardDef = cardRegistry.getCard("Abzan Monument")!!
                val tokenAbility = cardDef.script.activatedAbilities[0]

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = monumentId,
                        abilityId = tokenAbility.id
                    )
                )
                withClue("Activating the Spirit ability should succeed: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()

                withClue("Abzan Monument should be sacrificed") {
                    game.isOnBattlefield("Abzan Monument") shouldBe false
                }

                val spirit = game.findPermanent("Spirit Token")
                withClue("A 5/5 white Spirit token should exist (X = greatest toughness 5)") {
                    val id = spirit!!
                    val projected = stateProjector.project(game.state)
                    projected.getPower(id) shouldBe 5
                    projected.getToughness(id) shouldBe 5
                }
            }
        }
    }
}
