package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Kona, Rescue Beastie (DSK #187) — {3}{G} 4/3 Legendary Creature — Beast Survivor.
 *
 * "Survival — At the beginning of your second main phase, if Kona is tapped, you may put a
 *  permanent card from your hand onto the battlefield."
 *
 * Modeled as a [Triggers.YourPostcombatMain] intervening-if (SourceIsTapped) over
 * [Patterns.Hand.putFromHand] with the Permanent filter; the "you may" is the up-to-one selection.
 */
class KonaRescueBeastieScenarioTest : ScenarioTestBase() {

    private val p1 = EntityId.of("player-1")

    init {
        context("Survival — second main, if tapped, may put a permanent from hand onto battlefield") {

            test("a tapped Kona puts a chosen permanent card from hand onto the battlefield") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Kona, Rescue Beastie", tapped = true)
                    .withCardInHand(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)

                // Resolve the trigger; it pauses on a select-up-to-one decision. Choose the Bears.
                var guard = 0
                while (!game.isOnBattlefield("Grizzly Bears") && guard < 30) {
                    val decision = game.state.pendingDecision
                    if (decision is SelectCardsDecision) {
                        val bears = game.state.getHand(p1).first { id ->
                            game.state.getEntity(id)?.get<CardComponent>()?.name == "Grizzly Bears"
                        }
                        game.selectCards(listOf(bears))
                    } else {
                        game.resolveStack()
                    }
                    guard++
                }

                withClue("the chosen permanent entered the battlefield from hand") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                }
                withClue("it is no longer in hand") {
                    game.isInHand(1, "Grizzly Bears") shouldBe false
                }
            }

            test("the controller may decline (selecting nothing leaves the card in hand)") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Kona, Rescue Beastie", tapped = true)
                    .withCardInHand(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)

                var guard = 0
                while (game.hasPendingDecision() && guard < 30) {
                    val decision = game.state.pendingDecision
                    if (decision is SelectCardsDecision) {
                        game.skipSelection()
                    } else {
                        game.resolveStack()
                    }
                    guard++
                }

                withClue("declining the may keeps the permanent in hand") {
                    game.isInHand(1, "Grizzly Bears") shouldBe true
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                }
            }

            test("an untapped Kona does NOT fire Survival") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Kona, Rescue Beastie", tapped = false)
                    .withCardInHand(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
                repeat(5) { if (!game.hasPendingDecision()) game.resolveStack() }

                withClue("no Survival decision — Kona is untapped, the intervening-if is false") {
                    game.hasPendingDecision() shouldBe false
                    game.isInHand(1, "Grizzly Bears") shouldBe true
                }
            }
        }
    }
}
