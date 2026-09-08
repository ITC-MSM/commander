package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.handlers.PredicateEvaluator
import com.wingedsheep.engine.handlers.TargetFinder
import com.wingedsheep.engine.handlers.effects.DamageUtils
import com.wingedsheep.engine.legalactions.utils.TargetEnumerationUtils
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.state.components.battlefield.WasDealtDamageThisTurnComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.GameObjectFilter
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class NeedleDropScenarioTest : ScenarioTestBase() {
    private val requirement = Targets.Any(GameObjectFilter.Any.wasDealtDamageThisTurn())

    private fun board() = scenario()
        .withPlayers("Caster", "Opponent")
        .withCardInHand(1, "Needle Drop")
        .withCardInHand(1, "Shock")
        .withCardInHand(1, "Unsummon")
        .withLandsOnBattlefield(1, "Mountain", 3)
        .withLandsOnBattlefield(1, "Island", 1)
        .withCardOnBattlefield(2, "Hill Giant")
        .withCardInLibrary(1, "Forest")
        .withCardInLibrary(1, "Forest")
        .withCardInLibrary(2, "Forest")
        .withActivePlayer(1)
        .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
        .build()

    private fun TestGame.damage(id: EntityId, amount: Int = 1) {
        state = DamageUtils.dealDamageToTarget(state, id, amount, null).state
    }

    private fun TestGame.checkTargets(expected: List<EntityId> = emptyList()) {
        val source = findCardsInHand(1, "Needle Drop").single()
        TargetFinder().findLegalTargets(state, requirement, player1Id, source)
            .shouldContainExactlyInAnyOrder(expected)
        TargetEnumerationUtils(PredicateEvaluator()).findValidTargets(state, player1Id, requirement, source)
            .shouldContainExactlyInAnyOrder(expected)
    }

    init {
        test("no damaged targets means no legal cast, including forged undamaged player and permanent targets") {
            val game = board()
            game.checkTargets()
            game.castSpellTargetingPlayer(1, "Needle Drop", 2).error shouldNotBe null
            game.castSpell(1, "Needle Drop", game.findPermanent("Hill Giant")!!).error shouldNotBe null
        }

        for (player in listOf(1, 2)) {
            test("damaged player $player is targetable and resolution deals one then draws") {
                val game = board()
                val recipient = if (player == 1) game.player1Id else game.player2Id
                game.damage(recipient)
                game.checkTargets(listOf(recipient))
                game.castSpellTargetingPlayer(1, "Needle Drop", player).error shouldBe null
                game.resolveStack()
                game.getLifeTotal(player) shouldBe 18
                game.isInHand(1, "Forest") shouldBe true
            }
        }

        test("Shock damage enables a creature target and Needle Drop draws even when its damage is lethal") {
            val game = board()
            val giant = game.findPermanent("Hill Giant")!!
            game.castSpell(1, "Shock", giant).error shouldBe null
            game.resolveStack()
            game.checkTargets(listOf(giant))
            game.castSpell(1, "Needle Drop", giant).error shouldBe null
            game.resolveStack()
            game.isInGraveyard(2, "Hill Giant") shouldBe true
            game.isInHand(1, "Forest") shouldBe true
        }

        test("removing marked damage preserves damage history") {
            val game = board()
            val giant = game.findPermanent("Hill Giant")!!
            game.damage(giant)
            game.state = game.state.updateEntity(giant) { it.without<DamageComponent>() }
            game.checkTargets(listOf(giant))
            game.castSpell(1, "Needle Drop", giant).error shouldBe null
            game.resolveStack()
            game.state.getEntity(giant)!!.get<DamageComponent>()!!.amount shouldBe 1
        }

        test("life loss and zero damage do not enable a player target") {
            val game = board()
            game.state = game.state.withLifeTotal(game.player2Id, 15)
            game.damage(game.player2Id, 0)
            game.checkTargets()
            game.castSpellTargetingPlayer(1, "Needle Drop", 2).error shouldNotBe null
        }

        test("fully prevented damage does not enable a target") {
            val game = scenario()
                .withPlayers("Caster", "Opponent")
                .withCardInHand(1, "Needle Drop")
                .withLandsOnBattlefield(1, "Mountain", 1)
                .withCardOnBattlefield(2, "Dawn Elemental")
                .build()
            val elemental = game.findPermanent("Dawn Elemental")!!
            game.damage(elemental)
            game.checkTargets()
            game.castSpell(1, "Needle Drop", elemental).error shouldNotBe null
        }

        test("a target removed in response makes the whole spell fail to resolve, including its draw") {
            val game = board()
            val giant = game.findPermanent("Hill Giant")!!
            game.damage(giant)
            game.castSpell(1, "Needle Drop", giant).error shouldBe null
            game.castSpell(1, "Unsummon", giant).error shouldBe null
            game.resolveStack()
            game.isInHand(2, "Hill Giant") shouldBe true
            game.isInHand(1, "Forest") shouldBe false
            game.isInGraveyard(1, "Needle Drop") shouldBe true
            game.state.getEntity(giant)?.has<WasDealtDamageThisTurnComponent>() shouldBe false
        }

        test("a restriction that stops holding is rechecked on resolution") {
            val game = board()
            game.damage(game.player2Id)
            game.castSpellTargetingPlayer(1, "Needle Drop", 2).error shouldBe null
            game.state = game.state.updateEntity(game.player2Id) { it.without<WasDealtDamageThisTurnComponent>() }
            game.resolveStack()
            game.getLifeTotal(2) shouldBe 19
            game.isInHand(1, "Forest") shouldBe false
        }

        test("cleanup clears history for both players and permanents") {
            val game = board()
            val giant = game.findPermanent("Hill Giant")!!
            game.damage(game.player2Id)
            game.damage(giant)
            game.checkTargets(listOf(game.player2Id, giant))
            game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
            game.checkTargets()
        }

        test("noncombat damage to a planeswalker enables Needle Drop and removes loyalty") {
            val game = scenario()
                .withPlayers("Caster", "Opponent")
                .withCardInHand(1, "Needle Drop")
                .withLandsOnBattlefield(1, "Mountain", 1)
                .withCardOnBattlefield(2, "Jace Beleren")
                .withCardInLibrary(1, "Forest")
                .build()
            val jace = game.findPermanent("Jace Beleren")!!
            game.state = game.state.updateEntity(jace) { it.with(CountersComponent().withAdded(CounterType.LOYALTY, 5)) }
            game.damage(jace)
            game.checkTargets(listOf(jace))
            game.castSpell(1, "Needle Drop", jace).error shouldBe null
            game.resolveStack()
            game.state.getEntity(jace)!!.get<CountersComponent>()!!.getCount(CounterType.LOYALTY) shouldBe 3
            game.isInHand(1, "Forest") shouldBe true
        }
    }
}
