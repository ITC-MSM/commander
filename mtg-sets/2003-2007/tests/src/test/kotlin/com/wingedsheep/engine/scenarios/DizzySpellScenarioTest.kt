package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.matchers.shouldBe

class DizzySpellScenarioTest : ScenarioTestBase() {
    init {
        test("transmute searches by this card's mana value") {
            val game = scenario().withPlayers("P1", "P2")
                .withCardInHand(1, "Dizzy Spell")
                .withCardInLibrary(1, "Llanowar Elves")
                .withCardInLibrary(1, "Island")
                .withLandsOnBattlefield(1, "Island", 3)
                .withLandsOnBattlefield(1, "Swamp", 3)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            val source = game.findCardsInHand(1, "Dizzy Spell").single()
            val ability = cardRegistry.getCard("Dizzy Spell")!!.activatedAbilities.single { it.activateFromZone == Zone.HAND }
            game.execute(ActivateAbility(game.player1Id, source, ability.id)).error shouldBe null
            game.isInGraveyard(1, "Dizzy Spell") shouldBe true
            game.resolveStack()
            val match = game.findCardsInLibrary(1, "Llanowar Elves").single()
            (game.state.pendingDecision as SelectCardsDecision).options shouldBe listOf(match)
            game.selectCards(listOf(match)).error shouldBe null
            game.resolveStack()
            game.isInHand(1, "Llanowar Elves") shouldBe true
        }

        test("the spell reduces power without reducing toughness") {
            val game = scenario().withPlayers("P1", "P2")
                .withCardInHand(1, "Dizzy Spell")
                .withLandsOnBattlefield(1, "Island", 1)
                .withCardOnBattlefield(2, "Hill Giant")
                .withActivePlayer(1).inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            val giant = game.findPermanent("Hill Giant")!!
            game.castSpell(1, "Dizzy Spell", giant).error shouldBe null
            game.resolveStack()
            game.state.projectedState.getPower(giant) shouldBe 0
            game.state.projectedState.getToughness(giant) shouldBe 3
        }
    }
}
