package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.fra.cards.EdgarAncientBloodlord
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.matchers.shouldBe

class EdgarAncientBloodlordScenarioTest : ScenarioTestBase() {
    private val abilityId = EdgarAncientBloodlord.activatedAbilities.single().id

    init {
        test("sacrificing another creature grows Edgar, grants menace, and the death gains 1 life") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Edgar, Ancient Bloodlord")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withLandsOnBattlefield(1, "Plains", 2)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            val edgar = game.findPermanent("Edgar, Ancient Bloodlord")!!
            val bears = game.findPermanent("Grizzly Bears")!!
            game.execute(
                ActivateAbility(
                    playerId = game.player1Id,
                    sourceId = edgar,
                    abilityId = abilityId,
                    costPayment = AdditionalCostPayment(sacrificedPermanents = listOf(bears)),
                )
            ).error shouldBe null
            game.resolveStack()
            game.isInGraveyard(1, "Grizzly Bears") shouldBe true
            game.getLifeTotal(1) shouldBe 21
            game.state.projectedState.getPower(edgar) shouldBe 3
            game.state.projectedState.getToughness(edgar) shouldBe 4
            game.state.projectedState.hasKeyword(edgar, Keyword.MENACE) shouldBe true
        }

        test("an opponent's creature dying does not gain life") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Edgar, Ancient Bloodlord")
                .withCardOnBattlefield(2, "Grizzly Bears")
                .withCardInHand(1, "Shock")
                .withLandsOnBattlefield(1, "Mountain", 1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            game.castSpell(1, "Shock", game.findPermanent("Grizzly Bears")!!).error shouldBe null
            game.resolveStack()
            game.isInGraveyard(2, "Grizzly Bears") shouldBe true
            game.getLifeTotal(1) shouldBe 20
        }
    }
}
