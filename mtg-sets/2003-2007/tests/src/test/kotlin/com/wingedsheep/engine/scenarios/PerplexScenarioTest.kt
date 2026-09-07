package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.matchers.shouldBe

/**
 * Perplex — "Counter target spell unless its controller discards their hand."
 *
 * The card is a [com.wingedsheep.sdk.scripting.effects.PayOrSufferEffect] paying
 * `Costs.pay.DiscardHand`, the cost atom this batch adds. The cases that matter are the three the
 * atom's contract turns on: the payer is the *spell's* controller and not Perplex's, paying empties
 * the whole hand rather than a counted slice, and an empty hand pays the cost for free (CR 118.3)
 * so the spell survives.
 */
class PerplexScenarioTest : ScenarioTestBase() {
    init {
        test("the spell's controller discards their whole hand to save it") {
            val game = scenario().withPlayers("P1", "P2")
                .withCardInHand(1, "Perplex")
                .withCardInHand(2, "Giant Growth")
                .withCardInHand(2, "Lightning Bolt")
                .withCardInHand(2, "Island")
                .withLandsOnBattlefield(1, "Island", 2)
                .withLandsOnBattlefield(1, "Swamp", 1)
                .withLandsOnBattlefield(2, "Forest", 1)
                .withCardOnBattlefield(2, "Grizzly Bears")
                .withActivePlayer(2)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            val bear = game.findPermanent("Grizzly Bears")!!
            game.castSpell(2, "Giant Growth", bear).error shouldBe null
            val spell = game.state.stack.last()
            game.passPriority().error shouldBe null

            val perplex = game.findCardsInHand(1, "Perplex").single()
            game.execute(CastSpell(game.player1Id, perplex, listOf(ChosenTarget.Spell(spell)))).error shouldBe null
            game.resolveStack()

            // The opponent, not Perplex's controller, is asked.
            val decision = game.state.pendingDecision
            (decision as YesNoDecision).playerId shouldBe game.player2Id

            game.answerYesNo(true).error shouldBe null
            game.resolveStack()

            // Every remaining card went — Giant Growth is on the stack, so the hand held two.
            game.handSize(2) shouldBe 0
            game.isInGraveyard(2, "Lightning Bolt") shouldBe true
            game.isInGraveyard(2, "Island") shouldBe true
            // The spell was not countered: the pump resolved.
            game.state.projectedState.getPower(bear) shouldBe 5
            game.handSize(1) shouldBe 0
        }

        test("declining counters the spell and leaves the hand alone") {
            val game = scenario().withPlayers("P1", "P2")
                .withCardInHand(1, "Perplex")
                .withCardInHand(2, "Giant Growth")
                .withCardInHand(2, "Lightning Bolt")
                .withLandsOnBattlefield(1, "Island", 2)
                .withLandsOnBattlefield(1, "Swamp", 1)
                .withLandsOnBattlefield(2, "Forest", 1)
                .withCardOnBattlefield(2, "Grizzly Bears")
                .withActivePlayer(2)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            val bear = game.findPermanent("Grizzly Bears")!!
            game.castSpell(2, "Giant Growth", bear).error shouldBe null
            val spell = game.state.stack.last()
            game.passPriority().error shouldBe null

            val perplex = game.findCardsInHand(1, "Perplex").single()
            game.execute(CastSpell(game.player1Id, perplex, listOf(ChosenTarget.Spell(spell)))).error shouldBe null
            game.resolveStack()

            game.answerYesNo(false).error shouldBe null
            game.resolveStack()

            game.isInGraveyard(2, "Giant Growth") shouldBe true
            game.state.projectedState.getPower(bear) shouldBe 2
            game.handSize(2) shouldBe 1
            game.isInHand(2, "Lightning Bolt") shouldBe true
        }

        test("an empty hand pays the cost for free, so the spell resolves") {
            val game = scenario().withPlayers("P1", "P2")
                .withCardInHand(1, "Perplex")
                .withCardInHand(2, "Giant Growth")
                .withLandsOnBattlefield(1, "Island", 2)
                .withLandsOnBattlefield(1, "Swamp", 1)
                .withLandsOnBattlefield(2, "Forest", 1)
                .withCardOnBattlefield(2, "Grizzly Bears")
                .withActivePlayer(2)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            val bear = game.findPermanent("Grizzly Bears")!!
            game.castSpell(2, "Giant Growth", bear).error shouldBe null
            val spell = game.state.stack.last()
            game.handSize(2) shouldBe 0
            game.passPriority().error shouldBe null

            val perplex = game.findCardsInHand(1, "Perplex").single()
            game.execute(CastSpell(game.player1Id, perplex, listOf(ChosenTarget.Spell(spell)))).error shouldBe null
            game.resolveStack()

            // The choice is still offered — discarding nothing is a legal payment, not an
            // automatic one — and accepting it saves the spell.
            game.answerYesNo(true).error shouldBe null
            game.resolveStack()

            // Not countered: the pump resolved. (Giant Growth is in the graveyard either way —
            // a resolved instant goes there too — so the power is what tells the cases apart.)
            game.state.projectedState.getPower(bear) shouldBe 5
        }

        test("transmute searches by Perplex's own mana value") {
            val game = scenario().withPlayers("P1", "P2")
                .withCardInHand(1, "Perplex")
                .withCardInLibrary(1, "Centaur Courser")
                .withCardInLibrary(1, "Island")
                .withLandsOnBattlefield(1, "Island", 2)
                .withLandsOnBattlefield(1, "Swamp", 2)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            val source = game.findCardsInHand(1, "Perplex").single()
            val ability = cardRegistry.getCard("Perplex")!!
                .activatedAbilities.single { it.activateFromZone == Zone.HAND }
            game.execute(ActivateAbility(game.player1Id, source, ability.id)).error shouldBe null
            game.isInGraveyard(1, "Perplex") shouldBe true
            game.resolveStack()

            val match = game.findCardsInLibrary(1, "Centaur Courser").single()
            (game.state.pendingDecision as SelectCardsDecision).options shouldBe listOf(match)
            game.selectCards(listOf(match)).error shouldBe null
            game.resolveStack()
            game.isInHand(1, "Centaur Courser") shouldBe true
        }
    }
}
