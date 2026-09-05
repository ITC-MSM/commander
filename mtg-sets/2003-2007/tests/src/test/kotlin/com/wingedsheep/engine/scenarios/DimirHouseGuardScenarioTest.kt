package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.matchers.shouldBe

class DimirHouseGuardScenarioTest : ScenarioTestBase() {
    init {
        test("transmute searches by this card's mana value") {
            val game = scenario().withPlayers("P1", "P2")
                .withCardInHand(1, "Dimir House Guard")
                .withCardInLibrary(1, "Hill Giant")
                .withCardInLibrary(1, "Island")
                .withLandsOnBattlefield(1, "Island", 3)
                .withLandsOnBattlefield(1, "Swamp", 3)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            val source = game.findCardsInHand(1, "Dimir House Guard").single()
            val ability = cardRegistry.getCard("Dimir House Guard")!!.activatedAbilities.single { it.activateFromZone == Zone.HAND }
            game.execute(ActivateAbility(game.player1Id, source, ability.id)).error shouldBe null
            game.isInGraveyard(1, "Dimir House Guard") shouldBe true
            game.resolveStack()
            val match = game.findCardsInLibrary(1, "Hill Giant").single()
            (game.state.pendingDecision as SelectCardsDecision).options shouldBe listOf(match)
            game.selectCards(listOf(match)).error shouldBe null
            game.resolveStack()
            game.isInHand(1, "Hill Giant") shouldBe true
        }

        test("sacrificing a creature regenerates the guard through destruction") {
            val game = scenario().withPlayers("P1", "P2")
                .withCardOnBattlefield(1, "Dimir House Guard")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withCardInHand(2, "Murder")
                .withLandsOnBattlefield(2, "Swamp", 3)
                .withActivePlayer(1).inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            val source = game.findPermanent("Dimir House Guard")!!
            val bear = game.findPermanent("Grizzly Bears")!!
            val ability = cardRegistry.getCard("Dimir House Guard")!!.activatedAbilities.single { it.activateFromZone == Zone.BATTLEFIELD }
            game.execute(ActivateAbility(game.player1Id, source, ability.id,
                costPayment = AdditionalCostPayment(sacrificedPermanents = listOf(bear)))).error shouldBe null
            game.isInGraveyard(1, "Grizzly Bears") shouldBe true
            game.resolveStack()
            if (game.state.priorityPlayerId != game.player2Id) game.passPriority().error shouldBe null
            game.castSpell(2, "Murder", source).error shouldBe null
            game.resolveStack()
            game.isOnBattlefield("Dimir House Guard") shouldBe true
        }
    }
}
