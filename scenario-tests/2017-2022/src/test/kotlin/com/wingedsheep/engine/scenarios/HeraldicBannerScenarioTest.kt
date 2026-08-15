package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseColorDecision
import com.wingedsheep.engine.core.ColorChosenResponse
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CastChoicesComponent
import com.wingedsheep.engine.state.components.battlefield.ChoiceValue
import com.wingedsheep.engine.state.components.battlefield.chosenColor
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.ChoiceSlot
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Heraldic Banner (ELD #222) — "As this artifact enters, choose a color. Creatures you control of
 * the chosen color get +1/+0. {T}: Add one mana of the chosen color."
 *
 * Covers the chosen-color anthem (`ModifyStats` × `withChosenColor()`) and the entry color choice.
 * The `AddManaOfChosenColor` mana ability reuses the primitive proven by Uncharted Haven.
 */
class HeraldicBannerScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    init {
        context("Heraldic Banner — chosen-color anthem") {

            test("buffs your creatures of the chosen color, leaves off-color creatures alone") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Heraldic Banner")
                    .withCardOnBattlefield(1, "Savannah Lions")   // white 1/1
                    .withCardOnBattlefield(1, "Centaur Courser")  // green 3/3 (off-color)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val banner = game.findPermanent("Heraldic Banner")!!
                game.state = game.state.updateEntity(banner) { c ->
                    c.with(
                        CastChoicesComponent(
                            chosen = mapOf(ChoiceSlot.COLOR to ChoiceValue.ColorChoice(Color.WHITE))
                        )
                    )
                }

                val lion = game.findPermanent("Savannah Lions")!!
                val centaur = game.findPermanent("Centaur Courser")!!

                withClue("Savannah Lions (white 1/1) should be +1/+0") {
                    projector.getProjectedPower(game.state, lion) shouldBe 2
                    projector.getProjectedToughness(game.state, lion) shouldBe 1
                }
                withClue("Centaur Courser (green) is off-color and unaffected") {
                    projector.getProjectedPower(game.state, centaur) shouldBe 3
                    projector.getProjectedToughness(game.state, centaur) shouldBe 3
                }
            }

            test("casting Heraldic Banner records the chosen color and the anthem then applies") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Heraldic Banner")
                    .withLandsOnBattlefield(1, "Plains", 3)        // pay {3}
                    .withCardOnBattlefield(1, "Savannah Lions")    // white 1/1 to observe the anthem
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val lion = game.findPermanent("Savannah Lions")!!

                val cast = game.castSpell(1, "Heraldic Banner")
                withClue("Casting Heraldic Banner ({3}) should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }

                // As-it-enters color choice (CR 614.12a).
                game.resolveStack()
                val decision = game.state.pendingDecision
                withClue("Heraldic Banner should pause for the color choice") {
                    (decision is ChooseColorDecision) shouldBe true
                }
                game.submitDecision(ColorChosenResponse(decision!!.id, Color.WHITE))
                game.resolveStack()

                val banner = game.findPermanent("Heraldic Banner")!!
                withClue("The chosen-color slot should hold WHITE") {
                    game.state.getEntity(banner)!!.chosenColor() shouldBe Color.WHITE
                }
                withClue("Savannah Lions should now be +1/+0 from the recorded chosen color") {
                    projector.getProjectedPower(game.state, lion) shouldBe 2
                    projector.getProjectedToughness(game.state, lion) shouldBe 1
                }
            }
        }
    }
}
