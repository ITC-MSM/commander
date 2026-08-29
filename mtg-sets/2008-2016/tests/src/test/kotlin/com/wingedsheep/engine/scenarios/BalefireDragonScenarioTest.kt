package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Balefire Dragon — "Whenever this creature deals combat damage to a player, it deals that much
 * damage to each creature that player controls."
 *
 * The sweep is scoped by `ControllerPredicate.ControlledByReferencedPlayer(Player.TriggeringPlayer)`,
 * and the damaged player rides on the trigger's `triggeringEntityId` (a self-bound damage trigger
 * sets no distinct `triggeringPlayerId`) — so the filter has to resolve that reference the same way
 * the effect-side resolver does, or the board sweep silently hits nothing.
 */
class BalefireDragonScenarioTest : FunSpec({

    test("connecting with a player sweeps that player's creatures for the damage dealt") {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        d.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)

        val attacker = d.activePlayer!!
        val defender = d.getOpponent(attacker)

        val dragon = d.putCreatureOnBattlefield(attacker, "Balefire Dragon")
        d.removeSummoningSickness(dragon)

        // Two creatures for the defender (swept) and one for the attacker (untouched).
        d.putCreatureOnBattlefield(defender, "Grizzly Bears")
        d.putCreatureOnBattlefield(defender, "Runeclaw Bear")
        d.putCreatureOnBattlefield(attacker, "Grizzly Bears")

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(attacker, listOf(dragon), defender)
        d.declareNoBlockers(defender)
        d.passPriorityUntil(Step.POSTCOMBAT_MAIN) // combat damage, then the trigger resolves

        d.getLifeTotal(defender) shouldBe 14
        d.getGraveyardCardNames(defender).sorted() shouldBe listOf("Grizzly Bears", "Runeclaw Bear")
        d.getCreatures(attacker).size shouldBe 2 // dragon + its own Bears survive
    }
})
