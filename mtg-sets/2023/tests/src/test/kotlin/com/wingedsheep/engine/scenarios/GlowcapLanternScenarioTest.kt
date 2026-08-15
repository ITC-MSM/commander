package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

/**
 * Glowcap Lantern (LCI #187) — {G} Artifact — Equipment.
 *
 * "Equipped creature has 'You may look at the top card of your library any time' and 'Whenever
 *  this creature attacks, it explores.'"
 *
 * Focus: the private "look at the top card of your library" permission exists only while the
 * Lantern is attached (it's part of "Equipped creature has ..."). The permission is a
 * conditional static gated on the source being attached to a creature, so an unattached Lantern
 * reveals nothing.
 */
class GlowcapLanternScenarioTest : ScenarioTestBase() {

    init {
        context("Glowcap Lantern") {

            test("attached Lantern reveals the top card of the controller's library to them") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardOnBattlefield(1, "Glowcap Lantern")
                    .withCardAttachedTo(1, "Glowcap Lantern", "Grizzly Bears")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val topCard = game.state.getLibrary(game.player1Id).first()
                game.getClientState(1).cards.containsKey(topCard) shouldBe true
            }

            test("unattached Lantern reveals nothing (peek is gated on attachment)") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Glowcap Lantern") // on the battlefield but not attached
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val topCard = game.state.getLibrary(game.player1Id).first()
                game.getClientState(1).cards.containsKey(topCard) shouldBe false
            }
        }
    }
}
