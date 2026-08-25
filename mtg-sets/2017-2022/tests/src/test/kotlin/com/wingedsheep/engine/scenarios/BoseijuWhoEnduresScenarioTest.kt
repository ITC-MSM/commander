package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.engine.support.ScenarioTestBase.TestGame
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Boseiju, Who Endures (NEO #266) — Legendary Land.
 *
 *   {T}: Add {G}.
 *   Channel — {1}{G}, Discard this card: Destroy target artifact, enchantment, or nonbasic land
 *   an opponent controls. That player may search their library for a land card with a basic land
 *   type, put it onto the battlefield, then shuffle. This ability costs {1} less to activate for
 *   each legendary creature you control.
 *
 * See OtawaraSoaringCityScenarioTest for the shared channel shape. Boseiju's own points are the
 * "an opponent controls" restriction — unlike Otawara, it can't be pointed at your own board —
 * and that a *basic* land is not a legal target while a nonbasic one is.
 */
class BoseijuWhoEnduresScenarioTest : ScenarioTestBase() {

    private fun channelAbilityId() = cardRegistry.getCard("Boseiju, Who Endures")!!
        .activatedAbilities.first { it.activateFromZone == Zone.HAND }.id

    private fun activate(game: TestGame, target: EntityId) =
        game.execute(
            ActivateAbility(
                playerId = game.player1Id,
                sourceId = game.findCardsInHand(1, "Boseiju, Who Endures").first(),
                abilityId = channelAbilityId(),
                targets = listOf(ChosenTarget.Permanent(target))
            )
        )


    /**
     * Clear whatever prompts an activation raises, without assuming their shape or count.
     * Bounded, because an unbounded `while (hasPendingDecision())` spins forever the moment a
     * decision arrives that the chosen responder can't answer — `skipSelection` is a no-op
     * against a yes/no prompt, which is exactly what the consolation search raises.
     */
    private fun declineAllPrompts(game: TestGame, max: Int = 8) {
        var guard = 0
        while (game.hasPendingDecision() && guard++ < max) {
            when (game.getPendingDecision()) {
                is YesNoDecision -> game.answerYesNo(false)
                else -> game.skipSelection()
            }
        }
        withClue("prompts should settle well inside the guard") {
            game.hasPendingDecision() shouldBe false
        }
    }

    init {
        context("Boseiju, Who Endures") {

            test("destroys an opponent's nonbasic land") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Boseiju, Who Endures")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withCardOnBattlefield(2, "Secluded Courtyard")   // a nonbasic land
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val courtyard = game.findPermanent("Secluded Courtyard")!!

                withClue("{1}{G} from two Forests") {
                    activate(game, courtyard).error shouldBe null
                }
                game.resolveStack()
                // The consolation search belongs to the opponent and is a "may" — decline it.
                declineAllPrompts(game)

                withClue("The nonbasic land is destroyed") {
                    game.isOnBattlefield("Secluded Courtyard") shouldBe false
                    game.isInGraveyard(2, "Secluded Courtyard") shouldBe true
                }
                withClue("Boseiju discarded itself to pay") {
                    game.isInGraveyard(1, "Boseiju, Who Endures") shouldBe true
                }
            }

            test("destroys an opponent's artifact") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Boseiju, Who Endures")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withCardOnBattlefield(2, "Sol Ring")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val solRing = game.findPermanent("Sol Ring")!!
                activate(game, solRing).error shouldBe null
                game.resolveStack()
                declineAllPrompts(game)

                game.isInGraveyard(2, "Sol Ring") shouldBe true
            }

            test("a basic land is not a legal target") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Boseiju, Who Endures")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withLandsOnBattlefield(2, "Island", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val island = game.findPermanent("Island")!!

                withClue("'nonbasic land' excludes an Island") {
                    activate(game, island).error shouldNotBe null
                }
            }

            test("your own nonbasic land is not a legal target") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Boseiju, Who Endures")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withCardOnBattlefield(1, "Secluded Courtyard")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val ownCourtyard = game.findPermanent("Secluded Courtyard")!!

                withClue("'an opponent controls' — Boseiju can't eat your own land") {
                    activate(game, ownCourtyard).error shouldNotBe null
                }
            }
        }
    }
}
