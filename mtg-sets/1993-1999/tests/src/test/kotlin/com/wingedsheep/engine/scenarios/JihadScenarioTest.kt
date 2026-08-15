package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseColorDecision
import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.ColorChosenResponse
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CastChoicesComponent
import com.wingedsheep.engine.state.components.battlefield.ChoiceValue
import com.wingedsheep.engine.state.components.battlefield.chosenColor
import com.wingedsheep.engine.state.components.battlefield.chosenOpponent
import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.ChoiceSlot
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Jihad (ARN) — exercises two pieces that didn't exist before this card:
 *  - the [ChoiceSlot.OPPONENT] cast-choice slot + [com.wingedsheep.sdk.scripting.references.Player.ChosenOpponent]
 *    reference (read by the static anthem's condition and the state-trigger condition);
 *  - the `nontoken()` × `sharingChosenColorWithSource()` filter composition pinning the buff to
 *    *nontoken* permanents of the *chosen* color on the *chosen* player.
 *
 * The ETB cast-choice flow itself (color + opponent prompt → resumer writes the bag) is covered
 * by Riptide Replicator / Callous Oppressor style tests for [ChoiceSlot.COLOR]; here we set the
 * bag directly to isolate the *reading* paths and avoid duplicating decision-loop plumbing.
 */
class JihadScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    init {
        context("Jihad — static anthem + state-trigger sacrifice gated on chosen player + color") {

            test("anthem applies while the chosen opponent controls a nontoken permanent of the chosen color") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Jihad")
                    .withCardOnBattlefield(1, "Glory Seeker")          // white creature on owner's side
                    .withCardOnBattlefield(2, "Mons's Goblin Raiders")            // chosen player controls a red permanent
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val jihad = game.findPermanent("Jihad")!!
                val whiteCreature = game.findPermanent("Glory Seeker")!!
                game.state = game.state.updateEntity(jihad) { c ->
                    c.with(
                        CastChoicesComponent(
                            chosen = mapOf(
                                ChoiceSlot.COLOR to ChoiceValue.ColorChoice(Color.RED),
                                ChoiceSlot.OPPONENT to ChoiceValue.EntityChoice(game.player2Id)
                            )
                        )
                    )
                }

                game.resolveStack()

                withClue("Glory Seeker (2/2) should be +2/+1 while chosen opponent controls a red nontoken permanent") {
                    projector.getProjectedPower(game.state, whiteCreature) shouldBe (2 + 2)
                    projector.getProjectedToughness(game.state, whiteCreature) shouldBe (2 + 1)
                }
                withClue("Jihad should remain on the battlefield while the condition holds") {
                    game.isOnBattlefield("Jihad") shouldBe true
                }
            }

            test("tokens do not count — chosen opponent only has a red token, so Jihad is sacrificed") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Jihad")
                    .withCardOnBattlefield(1, "Glory Seeker")
                    // Only thing the chosen opponent controls of the chosen color is a TOKEN —
                    // `nontoken()` excludes it, so the condition is false from the start.
                    .withCardOnBattlefield(2, "Mons's Goblin Raiders", isToken = true)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val jihad = game.findPermanent("Jihad")!!
                val whiteCreature = game.findPermanent("Glory Seeker")!!
                game.state = game.state.updateEntity(jihad) { c ->
                    c.with(
                        CastChoicesComponent(
                            chosen = mapOf(
                                ChoiceSlot.COLOR to ChoiceValue.ColorChoice(Color.RED),
                                ChoiceSlot.OPPONENT to ChoiceValue.EntityChoice(game.player2Id)
                            )
                        )
                    )
                }

                // Sanity check: the token *exists* on the battlefield as a permanent...
                val tokenId = game.findPermanent("Mons's Goblin Raiders")!!
                game.state.getEntity(tokenId)?.has<TokenComponent>() shouldBe true

                // ...but the anthem is OFF (filter rejects tokens).
                projector.getProjectedPower(game.state, whiteCreature) shouldBe 2
                projector.getProjectedToughness(game.state, whiteCreature) shouldBe 2

                // Run the state-trigger poller: with no *nontoken* red permanent on the chosen
                // opponent's side, Jihad's state trigger fires and sacrifices it.
                game.passUntilPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
                game.resolveStack()

                withClue("Jihad's state-triggered ability should sacrifice it") {
                    game.isOnBattlefield("Jihad") shouldBe false
                }
            }

            test("untargeted opponent's permanents are ignored — Jihad is sacrificed even if a non-chosen player controls the color") {
                // 3 players' worth of state isn't supported by the 2-player scenario harness;
                // we model the "ignored player" angle by having the controller themselves hold
                // the chosen color while the chosen opponent holds nothing of it.
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Jihad")
                    .withCardOnBattlefield(1, "Mountain")    // controller owns the red permanent, NOT the chosen opponent
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val jihad = game.findPermanent("Jihad")!!
                game.state = game.state.updateEntity(jihad) { c ->
                    c.with(
                        CastChoicesComponent(
                            chosen = mapOf(
                                ChoiceSlot.COLOR to ChoiceValue.ColorChoice(Color.RED),
                                ChoiceSlot.OPPONENT to ChoiceValue.EntityChoice(game.player2Id)
                            )
                        )
                    )
                }

                game.passUntilPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
                game.resolveStack()

                withClue("Only the chosen opponent's permanents satisfy the condition") {
                    game.isOnBattlefield("Jihad") shouldBe false
                }
            }

            test("casting Jihad walks the color → opponent choice chain and records both on the cast-choices bag") {
                // Unlike the cases above (which seed the bag directly to isolate the reading
                // paths), this one casts Jihad from hand and answers the prompts, exercising the
                // OPPONENT pause/resume plumbing: StackResolver.pauseForEntersWithChoice +
                // ModalAndCloneContinuationResumer writing ChoiceSlot.OPPONENT from opponentIds.
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Jihad")
                    .withLandsOnBattlefield(1, "Plains", 3)             // pay {W}{W}{W}
                    .withCardOnBattlefield(1, "Glory Seeker")           // white creature to observe the anthem
                    .withCardOnBattlefield(2, "Mons's Goblin Raiders")  // chosen opponent's red nontoken permanent
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val whiteCreature = game.findPermanent("Glory Seeker")!!

                val cast = game.castSpell(1, "Jihad")
                withClue("Casting Jihad ({W}{W}{W}) should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }

                // CR 614.12a — the as-it-enters choices are made before Jihad enters. The engine
                // surfaces them in ChoiceType ordinal order: COLOR first, then OPPONENT.
                game.resolveStack()
                val colorDecision = game.state.pendingDecision
                withClue("Jihad should pause for the color choice first") {
                    (colorDecision is ChooseColorDecision) shouldBe true
                }
                game.submitDecision(ColorChosenResponse(colorDecision!!.id, Color.RED))

                // Resolving the color choice chains straight into the opponent choice.
                val opponentDecision = game.state.pendingDecision
                withClue("Jihad should then pause for the opponent choice") {
                    (opponentDecision is ChooseOptionDecision) shouldBe true
                }
                val options = (opponentDecision as ChooseOptionDecision).options
                val player2Index = options.indexOf("Player2")
                withClue("The lone opponent should be offered as a choice, options were $options") {
                    (player2Index >= 0) shouldBe true
                }
                game.submitDecision(OptionChosenResponse(opponentDecision.id, player2Index))

                game.resolveStack()

                val jihadEntity = game.state.getEntity(game.findPermanent("Jihad")!!)!!
                withClue("Jihad should have resolved onto the battlefield") {
                    game.isOnBattlefield("Jihad") shouldBe true
                }
                withClue("The chosen-color slot should hold the picked color (RED)") {
                    jihadEntity.chosenColor() shouldBe Color.RED
                }
                withClue("The chosen-opponent slot should hold the picked player's entity id") {
                    jihadEntity.chosenOpponent() shouldBe game.player2Id
                }

                // End-to-end: the recorded color + opponent actually drive the anthem.
                withClue("Glory Seeker (2/2) should be +2/+1 from Jihad's anthem") {
                    projector.getProjectedPower(game.state, whiteCreature) shouldBe (2 + 2)
                    projector.getProjectedToughness(game.state, whiteCreature) shouldBe (2 + 1)
                }
            }
        }
    }
}
