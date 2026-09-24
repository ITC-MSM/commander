package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

/**
 * Hostility (LRW #176) — a spell you control that would deal damage to an opponent is prevented,
 * and you create a 3/1 haste Elemental Shaman per point prevented.
 */
class HostilityScenarioTest : ScenarioTestBase() {
    init {
        fun base() = scenario().withPlayers("P1", "P2")
            .withCardOnBattlefield(1, "Hostility")
            .withActivePlayer(1).inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

        fun TestGame.shamans() = state.getBattlefield().filter { id ->
            state.getEntity(id)?.get<com.wingedsheep.engine.state.components.identity.CardComponent>()
                ?.name?.startsWith("Elemental Shaman") == true
        }

        test("a burn spell at an opponent becomes three hasty tokens") {
            val game = base().withCardInHand(1, "Lightning Bolt")
                .withLandsOnBattlefield(1, "Mountain", 1).build()
            game.castSpellTargetingPlayer(1, "Lightning Bolt", 2).error shouldBe null
            game.resolveStack()
            game.getLifeTotal(2) shouldBe 20
            val tokens = game.shamans()
            tokens.size shouldBe 3
            tokens.all { game.state.projectedState.getController(it) == game.player1Id } shouldBe true
            tokens.all { game.state.projectedState.hasKeyword(it, Keyword.HASTE) } shouldBe true
            tokens.all { game.state.projectedState.getPower(it) == 3 } shouldBe true
            tokens.all { game.state.projectedState.getToughness(it) == 1 } shouldBe true
        }

        test("a spell's damage to a creature is not prevented") {
            val game = base().withCardOnBattlefield(2, "Hill Giant")
                .withCardInHand(1, "Lightning Bolt")
                .withLandsOnBattlefield(1, "Mountain", 1).build()
            game.castSpell(1, "Lightning Bolt", game.findPermanent("Hill Giant")!!).error shouldBe null
            game.resolveStack()
            game.isInGraveyard(2, "Hill Giant") shouldBe true
            game.shamans().size shouldBe 0
        }

        test("an ability's damage to an opponent is dealt normally") {
            val game = base().withCardOnBattlefield(1, "Prodigal Sorcerer").build()
            game.execute(ActivateAbility(
                game.player1Id, game.findPermanent("Prodigal Sorcerer")!!,
                cardRegistry.getCard("Prodigal Sorcerer")!!.activatedAbilities.single().id,
                targets = listOf(ChosenTarget.Player(game.player2Id))
            )).error shouldBe null
            game.resolveStack()
            game.getLifeTotal(2) shouldBe 19
            game.shamans().size shouldBe 0
        }

        test("an opponent's spell at you is not prevented") {
            val game = base().withCardInHand(2, "Lightning Bolt")
                .withLandsOnBattlefield(2, "Mountain", 1).build()
            game.passPriority().error shouldBe null
            game.castSpellTargetingPlayer(2, "Lightning Bolt", 1).error shouldBe null
            game.resolveStack()
            game.getLifeTotal(1) shouldBe 17
            game.shamans().size shouldBe 0
        }
    }
}
