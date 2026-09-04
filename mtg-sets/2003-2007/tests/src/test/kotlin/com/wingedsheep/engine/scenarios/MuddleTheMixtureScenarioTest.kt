package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.matchers.shouldBe
import io.kotest.matchers.nulls.shouldNotBeNull

class MuddleTheMixtureScenarioTest : ScenarioTestBase() {
    init {
        test("transmute searches by this card's mana value") {
            val game = scenario().withPlayers("P1", "P2")
                .withCardInHand(1, "Muddle the Mixture")
                .withCardInLibrary(1, "Grizzly Bears")
                .withCardInLibrary(1, "Island")
                .withLandsOnBattlefield(1, "Island", 3)
                .withLandsOnBattlefield(1, "Swamp", 3)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            val source = game.findCardsInHand(1, "Muddle the Mixture").single()
            val ability = cardRegistry.getCard("Muddle the Mixture")!!.activatedAbilities.single { it.activateFromZone == Zone.HAND }
            game.execute(ActivateAbility(game.player1Id, source, ability.id)).error shouldBe null
            game.isInGraveyard(1, "Muddle the Mixture") shouldBe true
            game.resolveStack()
            val match = game.findCardsInLibrary(1, "Grizzly Bears").single()
            (game.state.pendingDecision as SelectCardsDecision).options shouldBe listOf(match)
            game.selectCards(listOf(match)).error shouldBe null
            game.resolveStack()
            game.isInHand(1, "Grizzly Bears") shouldBe true
        }

        test("counters an instant spell") {
            val game = scenario().withPlayers("P1", "P2")
                .withCardInHand(1, "Muddle the Mixture")
                .withCardInHand(2, "Giant Growth")
                .withLandsOnBattlefield(1, "Island", 2)
                .withLandsOnBattlefield(2, "Forest", 1)
                .withCardOnBattlefield(2, "Grizzly Bears")
                .withActivePlayer(2).inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            val bear = game.findPermanent("Grizzly Bears")!!
            game.castSpell(2, "Giant Growth", bear).error shouldBe null
            val spell = game.state.stack.last()
            game.passPriority().error shouldBe null
            val muddle = game.findCardsInHand(1, "Muddle the Mixture").single()
            game.execute(CastSpell(game.player1Id, muddle, listOf(ChosenTarget.Spell(spell)))).error shouldBe null
            game.resolveStack()
            game.isInGraveyard(2, "Giant Growth") shouldBe true
            game.state.projectedState.getPower(bear) shouldBe 2
        }

        test("cannot target a creature spell") {
            val game = scenario().withPlayers("P1", "P2")
                .withCardInHand(1, "Muddle the Mixture")
                .withCardInHand(2, "Grizzly Bears")
                .withLandsOnBattlefield(1, "Island", 2)
                .withLandsOnBattlefield(2, "Forest", 2)
                .withActivePlayer(2).inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            game.castSpell(2, "Grizzly Bears").error shouldBe null
            val spell = game.state.stack.last()
            game.passPriority().error shouldBe null
            val muddle = game.findCardsInHand(1, "Muddle the Mixture").single()
            game.execute(CastSpell(game.player1Id, muddle, listOf(ChosenTarget.Spell(spell)))).error.shouldNotBeNull()
            game.isInHand(1, "Muddle the Mixture") shouldBe true
        }
    }
}
