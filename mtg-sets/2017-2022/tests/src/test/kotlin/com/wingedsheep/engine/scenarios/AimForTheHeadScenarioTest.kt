package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Aim for the Head (VOW #92) — {2}{B} Sorcery.
 *
 *   Choose one —
 *   • Exile target Zombie.
 *   • Target opponent exiles two cards from their hand.
 *
 * Mode 2 is the interesting one: the cards leave the *targeted opponent's* hand and that opponent
 * picks them, so the decision has to be posed to them and not to the caster. The printed ruling —
 * an opponent holding a single card exiles that card rather than the spell stalling — is asserted
 * as its own case.
 */
class AimForTheHeadScenarioTest : ScenarioTestBase() {

    init {
        context("Aim for the Head") {

            test("mode 1 exiles a target Zombie") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Aim for the Head")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withCardOnBattlefield(2, "Black Creature") // a 2/2 Zombie
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val zombie = game.findPermanent("Black Creature")!!

                game.castSpellWithMode(1, "Aim for the Head", 0, zombie).error shouldBe null
                game.resolveStack()

                withClue("the Zombie is exiled, not merely destroyed") {
                    game.findPermanents("Black Creature").size shouldBe 0
                    game.isInExile(2, "Black Creature") shouldBe true
                }
            }

            test("mode 2 has the targeted opponent exile two cards from their own hand") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Aim for the Head")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withCardInHand(2, "Grizzly Bears")
                    .withCardInHand(2, "Hill Giant")
                    .withCardInHand(2, "Lightning Bolt")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val spell = game.findCardsInHand(1, "Aim for the Head").first()
                game.execute(
                    CastSpell(
                        game.player1Id,
                        spell,
                        listOf(ChosenTarget.Player(game.player2Id)),
                        chosenModes = listOf(1),
                        modeTargetsOrdered = listOf(listOf(ChosenTarget.Player(game.player2Id)))
                    )
                ).error shouldBe null
                game.resolveStack()

                val decision = game.getPendingDecision() as? SelectCardsDecision
                decision.shouldNotBeNull()
                withClue("the opponent, not the caster, chooses which of their cards to exile") {
                    decision.playerId shouldBe game.player2Id
                    decision.minSelections shouldBe 2
                }

                game.selectCards(decision.options.take(2))
                game.resolveStack()

                withClue("two cards left the opponent's hand of three") {
                    game.handSize(2) shouldBe 1
                }
            }

            test("an opponent holding one card exiles that card instead of stalling") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Aim for the Head")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withCardInHand(2, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val spell = game.findCardsInHand(1, "Aim for the Head").first()
                game.execute(
                    CastSpell(
                        game.player1Id,
                        spell,
                        listOf(ChosenTarget.Player(game.player2Id)),
                        chosenModes = listOf(1),
                        modeTargetsOrdered = listOf(listOf(ChosenTarget.Player(game.player2Id)))
                    )
                ).error shouldBe null
                game.resolveStack()

                withClue("the opponent's only card is exiled — the printed ruling") {
                    game.handSize(2) shouldBe 0
                    game.isInExile(2, "Grizzly Bears") shouldBe true
                }
            }
        }
    }
}
