package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.state.components.identity.FaceDownComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Vindictive Triumph (FRA #162) — {W}{B}{B} Instant.
 *
 *   Exile target creature or planeswalker. If that permanent's mana value was 3 or less, return it
 *   to the battlefield tapped under your control. Exile it at the beginning of the next end step.
 *
 * The mana-value test is last-known information about the permanent, so a face-down creature
 * (mana value 0 on the battlefield) comes back even when the card underneath costs more.
 */
class VindictiveTriumphScenarioTest : ScenarioTestBase() {

    private fun ScenarioBuilder.triumphBoard(): ScenarioBuilder = this
        .withPlayers("Player1", "Player2")
        .withLandsOnBattlefield(1, "Plains", 1)
        .withLandsOnBattlefield(1, "Swamp", 2)
        .withCardInHand(1, "Vindictive Triumph")
        .withActivePlayer(1)
        .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

    init {
        context("Vindictive Triumph") {

            test("a mana value 3 or less creature returns tapped under your control, then is exiled at the next end step") {
                val game = scenario().triumphBoard()
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                game.castSpell(1, "Vindictive Triumph", bears).error shouldBe null
                game.resolveStack()

                val returned = game.findPermanent("Grizzly Bears")
                withClue("the Bears came back onto the battlefield") { (returned != null) shouldBe true }
                withClue("under the caster's control") {
                    game.state.getEntity(returned!!)?.get<ControllerComponent>()?.playerId shouldBe game.player1Id
                }
                withClue("tapped") {
                    game.state.getEntity(returned!!)?.has<TappedComponent>() shouldBe true
                }

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("exiled at the beginning of the next end step, into its owner's exile") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                    game.isInExile(2, "Grizzly Bears") shouldBe true
                }
            }

            test("a mana value 4 creature is simply exiled") {
                val game = scenario().triumphBoard()
                    .withCardOnBattlefield(2, "Hill Giant")
                    .build()

                val giant = game.findPermanent("Hill Giant")!!
                game.castSpell(1, "Vindictive Triumph", giant).error shouldBe null
                game.resolveStack()

                withClue("the Giant stays in exile") {
                    game.isOnBattlefield("Hill Giant") shouldBe false
                    game.isInExile(2, "Hill Giant") shouldBe true
                }
            }

            test("a face-down creature had mana value 0, so it comes back even though its card costs more") {
                val game = scenario().triumphBoard()
                    .withCardOnBattlefield(2, "Hill Giant")
                    .build()

                val giant = game.findPermanent("Hill Giant")!!
                game.state = game.state.updateEntity(giant) { it.with(FaceDownComponent) }

                game.castSpell(1, "Vindictive Triumph", giant).error shouldBe null
                game.resolveStack()

                val returned = game.findPermanent("Hill Giant")
                withClue("the face-down permanent's last-known mana value (0) passed the check") {
                    (returned != null) shouldBe true
                }
                withClue("it came back face up, under the caster's control") {
                    game.state.getEntity(returned!!)?.has<FaceDownComponent>() shouldBe false
                    game.state.getEntity(returned)?.get<ControllerComponent>()?.playerId shouldBe game.player1Id
                }
            }

            test("a token is exiled and never returns") {
                val game = scenario().triumphBoard()
                    .withCardOnBattlefield(2, "Grizzly Bears", isToken = true)
                    .build()

                val token = game.findPermanent("Grizzly Bears")!!
                game.castSpell(1, "Vindictive Triumph", token).error shouldBe null
                game.resolveStack()

                withClue("a token that has left the battlefield can't come back") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                }
            }
        }
    }
}
