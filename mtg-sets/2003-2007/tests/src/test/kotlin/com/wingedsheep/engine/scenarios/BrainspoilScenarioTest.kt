package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.matchers.shouldBe
import io.kotest.matchers.nulls.shouldNotBeNull

class BrainspoilScenarioTest : ScenarioTestBase() {
    init {
        test("transmute searches by this card's mana value") {
            val game = scenario().withPlayers("P1", "P2")
                .withCardInHand(1, "Brainspoil")
                .withCardInLibrary(1, "Air Elemental")
                .withCardInLibrary(1, "Island")
                .withLandsOnBattlefield(1, "Island", 3)
                .withLandsOnBattlefield(1, "Swamp", 3)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            val source = game.findCardsInHand(1, "Brainspoil").single()
            val ability = cardRegistry.getCard("Brainspoil")!!.activatedAbilities.single { it.activateFromZone == Zone.HAND }
            game.execute(ActivateAbility(game.player1Id, source, ability.id)).error shouldBe null
            game.isInGraveyard(1, "Brainspoil") shouldBe true
            game.resolveStack()
            val match = game.findCardsInLibrary(1, "Air Elemental").single()
            (game.state.pendingDecision as SelectCardsDecision).options shouldBe listOf(match)
            game.selectCards(listOf(match)).error shouldBe null
            game.resolveStack()
            game.isInHand(1, "Air Elemental") shouldBe true
        }

        test("destroys an unenchanted creature") {
            val game = scenario().withPlayers("P1", "P2")
                .withCardInHand(1, "Brainspoil")
                .withLandsOnBattlefield(1, "Swamp", 5)
                .withCardOnBattlefield(2, "Grizzly Bears")
                .withActivePlayer(1).inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            game.castSpell(1, "Brainspoil", game.findPermanent("Grizzly Bears")!!).error shouldBe null
            game.resolveStack()
            game.isInGraveyard(2, "Grizzly Bears") shouldBe true
        }

        test("an enchanted creature is not a legal target") {
            val game = scenario().withPlayers("P1", "P2")
                .withCardInHand(1, "Brainspoil")
                .withLandsOnBattlefield(1, "Swamp", 5)
                .withCardOnBattlefield(2, "Grizzly Bears")
                .withCardAttachedTo(2, "Holy Strength", "Grizzly Bears")
                .withActivePlayer(1).inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            game.castSpell(1, "Brainspoil", game.findPermanent("Grizzly Bears")!!).error.shouldNotBeNull()
            game.isInHand(1, "Brainspoil") shouldBe true
        }
    }
}
