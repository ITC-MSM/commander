package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

/**
 * Kwia Vigorbloom (FRA #140) — {3}{G}{W}{W} Legendary Creature — Elder Sphinx 6/6.
 *
 *   Flying, vigilance, lifelink, ward {2}
 *   Whenever you gain life, create a colorless artifact token named Lotus with "{T}, Sacrifice
 *   this token: Add three mana of any one color." This ability triggers only once each turn.
 */
class KwiaVigorbloomScenarioTest : ScenarioTestBase() {
    init {
        test("gaining life twice in one turn creates only one Lotus") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Kwia Vigorbloom")
                .withCardsInHand(1, "Sacred Nectar", 2)
                .withLandsOnBattlefield(1, "Plains", 4)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            game.castSpell(1, "Sacred Nectar").error shouldBe null
            game.resolveStack()
            game.findPermanents("Lotus").size shouldBe 1

            game.castSpell(1, "Sacred Nectar").error shouldBe null
            game.resolveStack()
            game.getLifeTotal(1) shouldBe 28
            game.findPermanents("Lotus").size shouldBe 1
        }

        test("Kwia's own lifelink combat damage makes a Lotus, and Kwia stays untapped") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Kwia Vigorbloom")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            game.declareAttackers(mapOf("Kwia Vigorbloom" to 2)).error shouldBe null
            game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)

            game.getLifeTotal(2) shouldBe 14
            game.getLifeTotal(1) shouldBe 26
            game.findPermanents("Lotus").size shouldBe 1
            val kwia = game.findPermanent("Kwia Vigorbloom")!!
            game.state.getEntity(kwia)!!.has<TappedComponent>() shouldBe false
        }

        test("the Lotus taps and sacrifices for three mana of one color") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Kwia Vigorbloom")
                .withCardInHand(1, "Sacred Nectar")
                .withLandsOnBattlefield(1, "Plains", 2)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            game.castSpell(1, "Sacred Nectar").error shouldBe null
            game.resolveStack()
            val lotus = game.findPermanent("Lotus")!!
            val ability = cardRegistry.requireCard("Lotus").activatedAbilities.single().id

            game.execute(
                ActivateAbility(game.player1Id, lotus, ability, manaColorChoice = Color.GREEN)
            ).error shouldBe null

            game.state.getEntity(game.player1Id)!!.get<ManaPoolComponent>()!!.green shouldBe 3
            game.findPermanents("Lotus").size shouldBe 0
        }
    }
}
