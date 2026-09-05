package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.matchers.shouldBe

class EtherealUsherScenarioTest : ScenarioTestBase() {
    init {
        test("transmute searches by this card's mana value") {
            val game = scenario().withPlayers("P1", "P2")
                .withCardInHand(1, "Ethereal Usher")
                .withCardInLibrary(1, "Craw Wurm")
                .withCardInLibrary(1, "Island")
                .withLandsOnBattlefield(1, "Island", 3)
                .withLandsOnBattlefield(1, "Swamp", 3)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            val source = game.findCardsInHand(1, "Ethereal Usher").single()
            val ability = cardRegistry.getCard("Ethereal Usher")!!.activatedAbilities.single { it.activateFromZone == Zone.HAND }
            game.execute(ActivateAbility(game.player1Id, source, ability.id)).error shouldBe null
            game.isInGraveyard(1, "Ethereal Usher") shouldBe true
            game.resolveStack()
            val match = game.findCardsInLibrary(1, "Craw Wurm").single()
            (game.state.pendingDecision as SelectCardsDecision).options shouldBe listOf(match)
            game.selectCards(listOf(match)).error shouldBe null
            game.resolveStack()
            game.isInHand(1, "Craw Wurm") shouldBe true
        }

        test("the battlefield ability makes the target unblockable") {
            val game = scenario().withPlayers("P1", "P2")
                .withCardOnBattlefield(1, "Ethereal Usher")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withLandsOnBattlefield(1, "Island", 1)
                .withActivePlayer(1).inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            val source = game.findPermanent("Ethereal Usher")!!
            val bear = game.findPermanent("Grizzly Bears")!!
            val ability = cardRegistry.getCard("Ethereal Usher")!!.activatedAbilities.single { it.activateFromZone == Zone.BATTLEFIELD }
            game.execute(ActivateAbility(game.player1Id, source, ability.id,
                targets = listOf(ChosenTarget.Permanent(bear)))).error shouldBe null
            game.resolveStack()
            game.state.projectedState.hasKeyword(bear, AbilityFlag.CANT_BE_BLOCKED) shouldBe true
            game.state.projectedState.hasKeyword(source, AbilityFlag.CANT_BE_BLOCKED) shouldBe false
        }
    }
}
