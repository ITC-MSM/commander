package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ChooseColorDecision
import com.wingedsheep.engine.core.ColorChosenResponse
import com.wingedsheep.engine.state.components.battlefield.SummoningSicknessComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Vraska, Soul of Stone (FRA #277) — "Artifact creatures you control have vigilance. Whenever you
 * cast a noncreature spell, create a 1/1 colorless Sculpture Treasure artifact creature token with
 * '{T}, Sacrifice this token: Add one mana of any color.'"
 */
class VraskaSoulOfStoneScenarioTest : ScenarioTestBase() {
    init {
        fun build() = scenario().withPlayers()
            .withCardOnBattlefield(1, "Vraska, Soul of Stone")
            .withCardInHand(1, "Lightning Bolt")
            .withCardInHand(1, "Grizzly Bears")
            .withLandsOnBattlefield(1, "Mountain", 1)
            .withLandsOnBattlefield(1, "Forest", 2)
            .withCardInLibrary(1, "Mountain")
            .withCardInLibrary(2, "Mountain")
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            .build()

        fun sculptures(game: TestGame): List<EntityId> =
            game.state.getBattlefield(game.player1Id).filter { id ->
                val card = game.state.getEntity(id)?.get<CardComponent>() ?: return@filter false
                card.typeLine.subtypes.any { it.value == "Sculpture" }
            }

        test("a noncreature spell creates a vigilant Sculpture Treasure artifact creature") {
            val game = build()
            game.castSpellTargetingPlayer(1, "Lightning Bolt", 2).error shouldBe null
            game.resolveStack()

            val token = sculptures(game).single()
            val card = game.state.getEntity(token)!!.get<CardComponent>()!!
            card.typeLine.isArtifact shouldBe true
            card.typeLine.isCreature shouldBe true
            card.typeLine.subtypes.map { it.value }.toSet() shouldBe setOf("Sculpture", "Treasure")
            card.colors shouldBe emptySet()
            val projected = game.state.projectedState
            projected.getPower(token) shouldBe 1
            projected.getToughness(token) shouldBe 1
            withClue("artifact creatures you control have vigilance") {
                projected.hasKeyword(token, Keyword.VIGILANCE) shouldBe true
            }
            // Vraska herself is not an artifact.
            projected.hasKeyword(game.findPermanent("Vraska, Soul of Stone")!!, Keyword.VIGILANCE) shouldBe false
        }

        test("a creature spell does not trigger") {
            val game = build()
            game.castSpell(1, "Grizzly Bears").error shouldBe null
            game.resolveStack()
            sculptures(game).shouldBeEmpty()
        }

        test("the token taps and sacrifices itself for one mana of any color") {
            val game = build()
            game.castSpellTargetingPlayer(1, "Lightning Bolt", 2).error shouldBe null
            game.resolveStack()
            val token = sculptures(game).single()
            val abilityId = game.state.grantedActivatedAbilities.first { it.entityId == token }.ability.id

            withClue("a creature token is summoning sick the turn it's created") {
                game.execute(ActivateAbility(game.player1Id, token, abilityId)).error shouldNotBe null
            }

            game.state = game.state.withEntity(
                token, game.state.getEntity(token)!!.without<SummoningSicknessComponent>()
            )
            game.execute(ActivateAbility(game.player1Id, token, abilityId)).error shouldBe null
            val decision = game.getPendingDecision()
            if (decision is ChooseColorDecision) {
                game.submitDecision(ColorChosenResponse(decision.id, Color.BLUE)).error shouldBe null
            }
            game.state.getEntity(game.player1Id)!!.get<ManaPoolComponent>()!!.blue shouldBe 1
            sculptures(game).shouldBeEmpty()
        }
    }
}
