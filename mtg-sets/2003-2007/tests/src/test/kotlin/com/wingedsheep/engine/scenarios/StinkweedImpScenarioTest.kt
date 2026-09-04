package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class StinkweedImpScenarioTest : ScenarioTestBase() {
    init {
        fun base() = scenario().withPlayers("P1", "P2")
            .withActivePlayer(1).inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

        test("dredge returns this card and mills its printed number instead of one draw") {
            val game = base().withCardInGraveyard(1, "Stinkweed Imp")
                .withCardInHand(1, "Inspiration")
                .withLandsOnBattlefield(1, "Island", 4)
                .withCardInLibrary(1, "Forest").withCardInLibrary(1, "Forest")
                .withCardInLibrary(1, "Forest").withCardInLibrary(1, "Forest")
                .withCardInLibrary(1, "Forest").withCardInLibrary(1, "Forest").build()
            game.castSpellTargetingPlayer(1, "Inspiration", 1).error shouldBe null
            game.resolveStack()
            (game.state.pendingDecision as YesNoDecision).context.sourceName shouldBe "Stinkweed Imp"
            game.answerYesNo(true).error shouldBe null
            game.resolveStack()
            game.isInHand(1, "Stinkweed Imp") shouldBe true
            game.findCardsInGraveyard(1, "Forest").size shouldBe 5
            game.state.getLibrary(game.player1Id).size shouldBe 0
            game.state.getHand(game.player1Id).size shouldBe 2
        }

        test("combat damage trigger destroys the creature even when Imp dies") {
            val game = base().withCardOnBattlefield(1, "Stinkweed Imp")
                .withCardOnBattlefield(2, "Air Elemental")
                .withCardInLibrary(1, "Forest").withCardInLibrary(2, "Forest").build()
            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            game.declareAttackers(mapOf("Stinkweed Imp" to 2)).error shouldBe null
            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
            game.declareBlockers(mapOf("Air Elemental" to listOf("Stinkweed Imp"))).error shouldBe null
            game.passUntilPhase(Phase.COMBAT, Step.COMBAT_DAMAGE)
            game.isInGraveyard(1, "Stinkweed Imp") shouldBe true
            game.isOnBattlefield("Air Elemental") shouldBe true
            game.resolveStack()
            game.isInGraveyard(2, "Air Elemental") shouldBe true
        }

        test("noncombat damage from Imp does not trigger destruction") {
            val game = base().withCardOnBattlefield(1, "Stinkweed Imp")
                .withCardOnBattlefield(2, "Air Elemental")
                .withCardInHand(1, "Lavamancer's Skill").withLandsOnBattlefield(1, "Mountain", 2).build()
            val imp = game.findPermanent("Stinkweed Imp")!!
            val elemental = game.findPermanent("Air Elemental")!!
            game.castSpell(1, "Lavamancer's Skill", imp).error shouldBe null
            game.resolveStack()
            val ability = (cardRegistry.getCard("Lavamancer's Skill")!!.staticAbilities.single()
                as com.wingedsheep.sdk.scripting.GrantActivatedAbility).ability
            game.execute(ActivateAbility(game.player1Id, imp, ability.id,
                targets = listOf(ChosenTarget.Permanent(elemental)))).error shouldBe null
            game.resolveStack()
            game.isOnBattlefield("Air Elemental") shouldBe true
        }
    }
}
