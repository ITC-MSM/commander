package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Lightning, Security Sergeant (FIN).
 *
 * Whenever Lightning deals combat damage to a player, the top card of the controller's library is
 * exiled and may be played for as long as they control Lightning. Exercises the impulse-exile +
 * conditional play permission (Possibility Technician shape): combat damage → exile → a CastSpell
 * play action for the exiled card is enumerated while Lightning is on the battlefield.
 */
class LightningSecuritySergeantScenarioTest : ScenarioTestBase() {

    init {
        context("Lightning, Security Sergeant") {

            test("combat damage exiles the top card and lets you play it while you control Lightning") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Lightning, Security Sergeant", summoningSickness = false)
                    .withCardInLibrary(1, "Mons's Goblin Raiders") // {R} 1/1 — a known top card
                    .withLandsOnBattlefield(1, "Mountain", 1)       // so the exiled card is affordable
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                game.declareAttackers(mapOf("Lightning, Security Sergeant" to 2)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.COMBAT_DAMAGE)
                game.resolveStack()
                if (game.state.pendingDecision != null) {
                    game.submitDefaultCombatDamage()
                    game.resolveStack()
                }

                withClue("Combat damage to a player should exile the top card of your library") {
                    game.isInExile(1, "Mons's Goblin Raiders") shouldBe true
                }

                // Advance to your postcombat main phase (sorcery-speed window) and confirm the
                // exiled creature is now playable — the granted may-play permission is honored
                // because you still control Lightning.
                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
                game.resolveStack()

                val canPlayExiledCard = game.getLegalActions(1).any { info ->
                    val action = info.action
                    action is CastSpell &&
                        game.state.getEntity(action.cardId)?.get<CardComponent>()?.name == "Mons's Goblin Raiders"
                }
                withClue("While controlling Lightning, the exiled card must be playable from exile") {
                    canPlayExiledCard shouldBe true
                }
            }
        }
    }
}
