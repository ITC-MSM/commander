package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.TargetsResponse
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Goatnapper (LRW #172) — "When this creature enters, untap target Goat and gain control of it
 * until end of turn. It gains haste until end of turn."
 *
 * Three things are worth proving from the outside: the target filter really is a Goat filter (a
 * non-Goat creature must be rejected, a changeling must be accepted), all three riders land
 * together (untapped, controlled, hasty), and the control grab expires at end of turn while the
 * untap does not.
 */
class GoatnapperScenarioTest : ScenarioTestBase() {

    init {
        context("Goatnapper") {

            /** Resolve the enters trigger, aiming it at [goat] if the engine asks. */
            fun TestGame.stealWith(goat: com.wingedsheep.sdk.model.EntityId?) {
                resolveStack()
                val decision = state.pendingDecision
                if (decision != null && goat != null) {
                    submitDecision(TargetsResponse(decision.id, mapOf(0 to listOf(goat))))
                }
                resolveStack()
            }

            test("steals a tapped Goat: untapped, under your control, and hasty") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Goatnapper")
                    .withLandsOnBattlefield(1, "Mountain", 3)
                    .withCardOnBattlefield(2, "Mountain Goat", tapped = true)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val goat = game.findPermanent("Mountain Goat")!!

                game.castSpell(1, "Goatnapper").error shouldBe null
                game.stealWith(goat)

                withClue("The Goat is untapped") {
                    game.state.getEntity(goat)?.get<TappedComponent>() shouldBe null
                }
                withClue("Control changed to Goatnapper's controller") {
                    game.state.projectedState.getController(goat) shouldBe game.player1Id
                }
                withClue("The stolen Goat has haste, so it can attack the turn it is stolen") {
                    game.state.projectedState.hasKeyword(goat, Keyword.HASTE) shouldBe true
                }
            }

            test("a changeling is a legal target — changeling makes it a Goat") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Goatnapper")
                    .withLandsOnBattlefield(1, "Mountain", 3)
                    // Avian Changeling is "every creature type", so it is a Goat (CR 702.73a).
                    .withCardOnBattlefield(2, "Avian Changeling")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val changeling = game.findPermanent("Avian Changeling")!!

                game.castSpell(1, "Goatnapper").error shouldBe null
                game.stealWith(changeling)

                withClue("The changeling counted as a Goat and was stolen") {
                    game.state.projectedState.getController(changeling) shouldBe game.player1Id
                }
            }

            test("a non-Goat creature is not a legal target, so the trigger finds nothing to steal") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Goatnapper")
                    .withLandsOnBattlefield(1, "Mountain", 3)
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                game.castSpell(1, "Goatnapper").error shouldBe null
                game.stealWith(null)

                withClue("Grizzly Bears is no Goat — it stays with its controller") {
                    game.state.projectedState.getController(bears) shouldBe game.player2Id
                }
                withClue("Goatnapper itself still resolved onto the battlefield") {
                    game.findPermanent("Goatnapper") shouldNotBe null
                }
            }

            test("control reverts at end of turn but the Goat stays untapped") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Goatnapper")
                    .withLandsOnBattlefield(1, "Mountain", 3)
                    .withCardOnBattlefield(2, "Mountain Goat", tapped = true)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val goat = game.findPermanent("Mountain Goat")!!

                game.castSpell(1, "Goatnapper").error shouldBe null
                game.stealWith(goat)

                game.state.projectedState.getController(goat) shouldBe game.player1Id

                game.passUntilPhase(Phase.ENDING, Step.CLEANUP)

                withClue("\"Until end of turn\" hands the Goat back in the cleanup step") {
                    game.state.projectedState.getController(goat) shouldBe game.player2Id
                }
                withClue("The untap is a one-shot, not a duration — the Goat is still untapped") {
                    game.state.getEntity(goat)?.get<TappedComponent>() shouldBe null
                }
                withClue("The Goat is still the Goat it always was") {
                    game.state.getEntity(goat)?.get<CardComponent>()?.name shouldBe "Mountain Goat"
                }
            }
        }
    }
}
