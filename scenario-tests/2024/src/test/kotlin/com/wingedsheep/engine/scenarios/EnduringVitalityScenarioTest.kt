package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ChooseColorDecision
import com.wingedsheep.engine.core.ColorChosenResponse
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Enduring Vitality.
 *
 * Enduring Vitality ({1}{G}{G}): Enchantment Creature — Elk Glimmer, 3/3
 * - Vigilance
 * - Creatures you control have "{T}: Add one mana of any color."
 * - Enduring: when it dies (as a creature) it returns as a (non-creature) enchantment.
 *
 * The Enduring death clause is covered by EnduringMechanicTest; here we prove the
 * granted mana ability: another creature you control gains it, it actually produces a
 * mana of the chosen color, and an opponent's creature is not granted it.
 */
class EnduringVitalityScenarioTest : ScenarioTestBase() {

    init {
        context("Enduring Vitality grants a tap-for-any-color mana ability to your creatures") {

            test("another creature you control gains the granted mana ability") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Enduring Vitality")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val manaAction = game.getLegalActions(1).find {
                    it.actionType == "ActivateAbility" &&
                        (it.action as? ActivateAbility)?.sourceId == bears
                }
                withClue("Grizzly Bears should gain '{T}: Add one mana of any color' from Vitality") {
                    manaAction shouldNotBe null
                }
            }

            test("activating the granted ability taps the creature and adds a mana of the chosen color") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Enduring Vitality", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val def = cardRegistry.getCard("Enduring Vitality")!!
                val abilityId = def.staticAbilities
                    .filterIsInstance<GrantActivatedAbility>().first().ability.id
                val bears = game.findPermanent("Grizzly Bears")!!

                // "{T}: Add one mana of any color" — a mana ability. The color is chosen at
                // activation time (manaColorChoice); if the engine instead pauses for a color
                // decision, answer it with green.
                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = bears,
                        abilityId = abilityId,
                        manaColorChoice = Color.GREEN
                    )
                )
                withClue("Activation should succeed: ${result.error}") { result.error shouldBe null }
                game.resolveStack()
                (game.getPendingDecision() as? ChooseColorDecision)?.let { decision ->
                    game.submitDecision(ColorChosenResponse(decision.id, Color.GREEN))
                    game.resolveStack()
                }

                withClue("Grizzly Bears should be tapped after paying the {T} cost") {
                    game.state.getEntity(bears)?.get<TappedComponent>() shouldNotBe null
                }
                val pool = game.state.getEntity(game.player1Id)?.get<ManaPoolComponent>()
                withClue("Exactly one mana of any color should have been produced") {
                    pool?.total shouldBe 1
                }
            }

            test("an opponent's creature is NOT granted the mana ability") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Enduring Vitality")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val manaAction = game.getLegalActions(2).find {
                    it.actionType == "ActivateAbility" &&
                        (it.action as? ActivateAbility)?.sourceId == bears
                }
                withClue("The grant is 'creatures YOU control'; opponent's creature gets nothing") {
                    manaAction shouldBe null
                }
            }
        }
    }
}
