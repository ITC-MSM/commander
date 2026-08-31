package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Radiant Grace // Radiant Restraints (VOW #31).
 *
 *   Front — Radiant Grace — Enchant creature. Enchanted creature gets +1/+0 and has vigilance.
 *           When enchanted creature dies, return this card to the battlefield transformed under
 *           your control attached to target opponent.
 *   Back  — Radiant Restraints — Enchant player. Creatures enchanted player controls enter tapped.
 *
 * Two things are new here and both are *scopes*, so both tests that matter are negative ones:
 *
 *  - The return is transformed **and** attached to a player, and lands **under the trigger's
 *    controller** rather than under the player it curses — the Dragon-aura cycle's version of this
 *    effect handed control to the host's controller, which for a Curse gives the card away.
 *  - "Creatures enchanted player controls" resolves against the *Aura's attachment*, not against
 *    the ability's controller. The controller's own creatures entering untapped is what separates
 *    the two readings; a controller-scoped filter would tap the wrong side of the board.
 */
class RadiantGraceScenarioTest : ScenarioTestBase() {

    private fun TestGame.attachedTo(id: EntityId): EntityId? =
        state.getEntity(id)?.get<AttachedToComponent>()?.targetId

    private fun TestGame.nameOf(id: EntityId): String? =
        state.getEntity(id)?.get<CardComponent>()?.name

    private fun TestGame.tapped(id: EntityId): Boolean =
        state.getEntity(id)?.has<TappedComponent>() == true

    init {
        context("Radiant Grace") {

            test("enchanted creature gets +1/+0 and vigilance") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardAttachedTo(1, "Radiant Grace", "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                game.state.projectedState.getPower(bears) shouldBe 3
                game.state.projectedState.getToughness(bears) shouldBe 2
                game.state.projectedState.hasKeyword(bears, Keyword.VIGILANCE) shouldBe true
            }

            test("when the enchanted creature dies it returns transformed, cursing the chosen opponent") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardAttachedTo(1, "Radiant Grace", "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withCardInHand(1, "Lightning Bolt")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val aura = game.findPermanent("Radiant Grace")!!

                // 3 damage kills the 3/2; the Aura falls off into the graveyard (CR 704.5m) and its
                // "when enchanted creature dies" trigger goes on the stack from the battlefield.
                game.castSpell(1, "Lightning Bolt", targetId = bears).error shouldBe null
                if (game.getPendingDecision() is SelectManaSourcesDecision) game.submitManaSourcesAutoPay()
                game.resolveStack()
                if (game.hasPendingDecision()) game.selectTargets(listOf(game.player2Id))
                game.resolveStack()

                withClue("the same card is back, showing its other face") {
                    game.nameOf(aura) shouldBe "Radiant Restraints"
                    game.isOnBattlefield("Radiant Restraints") shouldBe true
                }
                withClue("attached to the targeted opponent, not to a permanent") {
                    game.attachedTo(aura) shouldBe game.player2Id
                }
                withClue("'under your control' — the curse is player 1's, on player 2") {
                    game.state.getEntity(aura)?.get<ControllerComponent>()?.playerId shouldBe game.player1Id
                }
            }
        }

        context("Radiant Restraints") {

            /**
             * Radiant Restraints on the battlefield under player 1, cursing [cursedPlayer]. Attached
             * directly: "enchant player" needs an [AttachedToComponent] pointing at a *player* id,
             * which the builder's permanent-host `withCardAttachedTo` can't produce.
             */
            fun cursing(cursedPlayer: Int, activePlayer: Int = 1): TestGame {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Radiant Restraints")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withLandsOnBattlefield(2, "Forest", 2)
                    .withCardInHand(1, "Grizzly Bears")
                    .withCardInHand(2, "Grizzly Bears")
                    .withActivePlayer(activePlayer)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val curse = game.findPermanent("Radiant Restraints")!!
                val victimId = if (cursedPlayer == 1) game.player1Id else game.player2Id
                game.state = game.state.updateEntity(curse) { it.with(AttachedToComponent(victimId)) }
                return game
            }

            /** Cast Grizzly Bears from [playerNumber]'s hand and return the resulting permanent. */
            fun TestGame.playBears(playerNumber: Int): EntityId {
                castSpell(playerNumber, "Grizzly Bears").error shouldBe null
                if (getPendingDecision() is SelectManaSourcesDecision) submitManaSourcesAutoPay()
                resolveStack()
                return findPermanent("Grizzly Bears")!!
            }

            test("a creature the cursed player controls enters tapped") {
                val game = cursing(cursedPlayer = 2, activePlayer = 2)
                val bears = game.playBears(2)
                game.tapped(bears) shouldBe true
            }

            test("a creature the curse's own controller controls enters untapped") {
                val game = cursing(cursedPlayer = 2)
                val bears = game.playBears(1)
                withClue("the scope is the Aura's attachment, not the ability's controller") {
                    game.tapped(bears) shouldBe false
                }
            }

            test("a curse on its own controller taps that controller's creatures") {
                // The reading a controller-scoped filter gets exactly backwards.
                val game = cursing(cursedPlayer = 1)
                val bears = game.playBears(1)
                game.tapped(bears) shouldBe true
            }
        }
    }
}
