package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Astelli Reclaimer ({3}{W}{W}, Angel Warrior 5/4, Flying, Warp {2}{W}):
 * "When this creature enters, return target noncreature, nonland permanent card with mana value X
 * or less from your graveyard to the battlefield, where X is the amount of mana spent to cast this
 * creature."
 *
 * Cast normally for {3}{W}{W}, X = 5.
 */
class AstelliReclaimerScenarioTest : ScenarioTestBase() {

    private fun ScenarioTestBase.TestGame.graveyardCard(player: Int, name: String): EntityId {
        val playerId = if (player == 1) player1Id else player2Id
        return state.getGraveyard(playerId).first {
            state.getEntity(it)?.get<CardComponent>()?.name == name
        }
    }

    init {
        test("enters trigger returns a noncreature, nonland permanent and excludes creatures") {
            val game = scenario()
                .withPlayers("Player", "Opponent")
                .withCardInHand(1, "Astelli Reclaimer")
                .withLandsOnBattlefield(1, "Plains", 5)
                .withCardInGraveyard(1, "Sol Ring")       // Artifact, MV 1 — eligible
                .withCardInGraveyard(1, "Grizzly Bears")   // Creature, MV 2 — NOT eligible
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            game.castSpell(1, "Astelli Reclaimer")
            game.resolveStack()

            val decision = game.state.pendingDecision as? ChooseTargetsDecision
            withClue("Enters trigger should pause to choose a target in the graveyard") {
                decision shouldNotBe null
            }
            val legalTargets = decision!!.legalTargets[0] ?: emptyList()

            withClue("Sol Ring (noncreature, nonland, MV 1 <= 5) is a legal target") {
                legalTargets shouldContain game.graveyardCard(1, "Sol Ring")
            }
            withClue("Grizzly Bears (a creature) is not a legal target") {
                legalTargets shouldNotContain game.graveyardCard(1, "Grizzly Bears")
            }

            game.selectTargets(listOf(game.graveyardCard(1, "Sol Ring")))
            game.resolveStack()

            withClue("Sol Ring returns to the battlefield") {
                game.findPermanent("Sol Ring") shouldNotBe null
            }
        }

        test("returning an Aura lets the controller choose what it enchants (CR 303.4g)") {
            // Regression: an Aura returned by a generic put-onto-battlefield effect used to enter
            // unattached and die instantly to a state-based action. It must instead let its
            // controller pick a legal host as it enters.
            val game = scenario()
                .withPlayers("Player", "Opponent")
                .withCardInHand(1, "Astelli Reclaimer")
                .withLandsOnBattlefield(1, "Plains", 5)
                .withCardInGraveyard(1, "Pacifism")        // Aura — enchant creature, MV 2
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            game.castSpell(1, "Astelli Reclaimer")
            game.resolveStack()

            // Choose Pacifism as the return target.
            (game.state.pendingDecision as? ChooseTargetsDecision) shouldNotBe null
            game.selectTargets(listOf(game.graveyardCard(1, "Pacifism")))
            game.resolveStack()

            // The Aura now pauses for the controller to choose a host. The only creature on the
            // battlefield is Astelli Reclaimer itself.
            val hostDecision = game.state.pendingDecision as? ChooseTargetsDecision
            withClue("Returning the Aura should prompt for the host it enchants") {
                hostDecision shouldNotBe null
            }
            val reclaimer = game.findPermanent("Astelli Reclaimer")!!
            withClue("Astelli Reclaimer is a legal host for Pacifism") {
                (hostDecision!!.legalTargets[0] ?: emptyList()) shouldContain reclaimer
            }

            game.selectTargets(listOf(reclaimer))
            game.resolveStack()

            val pacifism = game.findPermanent("Pacifism")
            withClue("Pacifism enters the battlefield attached to a creature, not bounced by SBA") {
                pacifism shouldNotBe null
                game.state.getEntity(pacifism!!)?.get<AttachedToComponent>()?.targetId shouldBe reclaimer
            }
        }
    }
}
