package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Living History {1}{R} Enchantment (Secrets of Strixhaven #121).
 *
 * "When this enchantment enters, create a 2/2 red and white Spirit creature token.
 *  Whenever you attack, if a card left your graveyard this turn, target attacking creature
 *  gets +2/+0 until end of turn."
 *
 * Covers the ETB token (auto-tested via the snapshot) and, in particular, the intervening-"if"
 * attack trigger: the +2/+0 only fires/resolves when a card left the controller's graveyard
 * earlier in the turn. We force that condition with Raise Dead (returns a creature card from
 * the graveyard to hand), then attack and confirm the buff; the negative case skips the buff.
 */
class LivingHistoryScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    init {
        context("Living History — attack trigger gated on a card leaving the graveyard") {

            test("ETB creates a 2/2 red-white Spirit token") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Living History")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Living History").error shouldBe null
                game.resolveStack()

                withClue("ETB makes one 2/2 Spirit token") {
                    val spirit = game.findPermanent("Spirit Token")
                    (spirit != null) shouldBe true
                    projector.getProjectedPower(game.state, spirit!!) shouldBe 2
                    projector.getProjectedToughness(game.state, spirit) shouldBe 2
                }
            }

            test("after a card leaves the graveyard, attacking grants +2/+0 to a target attacker") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Living History")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInGraveyard(1, "Hill Giant")
                    .withCardInHand(1, "Raise Dead")
                    .withLandsOnBattlefield(1, "Swamp", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                // Raise Dead makes a card leave the graveyard this turn.
                game.castSpellTargetingGraveyardCard(1, "Raise Dead", graveyardOwnerNumber = 1, targetCardName = "Hill Giant")
                    .error shouldBe null
                game.resolveStack()
                game.isInGraveyard(1, "Hill Giant") shouldBe false

                val bears = game.findPermanent("Grizzly Bears")!!

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Grizzly Bears" to 2))

                withClue("The attack trigger should pause for a target attacking creature") {
                    game.hasPendingDecision() shouldBe true
                }
                game.selectTargets(listOf(bears))
                game.resolveStack()

                withClue("Grizzly Bears gets +2/+0 (2/2 -> 4/2)") {
                    projector.getProjectedPower(game.state, bears) shouldBe 4
                    projector.getProjectedToughness(game.state, bears) shouldBe 2
                }
            }

            test("attacking does nothing when no card left the graveyard this turn") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Living History")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Grizzly Bears" to 2))
                game.resolveStack()

                withClue("No card left the graveyard, so the intervening-if trigger does nothing") {
                    game.hasPendingDecision() shouldBe false
                    projector.getProjectedPower(game.state, bears) shouldBe 2
                }
            }
        }
    }
}
