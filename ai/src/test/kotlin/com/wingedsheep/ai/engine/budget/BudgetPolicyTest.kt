package com.wingedsheep.ai.engine.budget

import com.wingedsheep.engine.core.DeclareAttackers
import com.wingedsheep.engine.core.PassPriority
import com.wingedsheep.engine.legalactions.LegalAction
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Which window gets which tier.
 *
 * The tier assignment is where a budget can be *wrong* rather than merely small: a lethal window
 * mis-tiered as ROUTINE drops the simulation-refined target pick at the exact moment it matters.
 */
class BudgetPolicyTest : FunSpec({

    val bear = CardDefinition.creature(
        name = "Grizzly Bears",
        manaCost = ManaCost.parse("{1}{G}"),
        subtypes = setOf(Subtype("Bear")),
        power = 2, toughness = 2,
    )

    fun driver(): GameTestDriver = GameTestDriver().apply {
        registerCards(listOf(bear))
        registerCards(TestCards.all)
        initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
    }

    fun castAction(playerId: EntityId) = LegalAction(
        action = PassPriority(playerId),
        actionType = "CastSpell",
        description = "Cast something",
    )

    val policy = TieredBudgetPolicy()

    test("nothing meaningful to choose between is TRIVIAL") {
        val d = driver()
        policy.tierFor(d.state, d.player1, emptyList()) shouldBe BudgetTier.TRIVIAL
    }

    test("a combat declaration is always CRITICAL") {
        val d = driver()
        val declare = LegalAction(
            action = DeclareAttackers(d.player1, emptyMap()),
            actionType = "DeclareAttackers",
            description = "Declare attackers",
        )
        policy.tierFor(d.state, d.player1, listOf(declare)) shouldBe BudgetTier.CRITICAL
    }

    test("our own main phase is NORMAL while nobody is threatening lethal") {
        val d = driver()
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        policy.tierFor(d.state, d.player1, listOf(castAction(d.player1))) shouldBe BudgetTier.NORMAL
    }

    test("an opponent's quiet upkeep is ROUTINE") {
        val d = driver()
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        // player2 is not the active player, and their window is not a main phase.
        policy.tierFor(d.state, d.player2, listOf(castAction(d.player2))) shouldBe BudgetTier.ROUTINE
    }

    test("a board that can kill somebody this turn promotes even a quiet window to CRITICAL") {
        val d = driver()
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        repeat(4) {
            val attacker = d.putCreatureOnBattlefield(d.player1, "Grizzly Bears")
            d.removeSummoningSickness(attacker)
        }
        d.replaceState(d.state.withLifeTotal(d.player2, 8)) // 4 x 2 power >= 8 life
        policy.tierFor(d.state, d.player2, listOf(castAction(d.player2))) shouldBe BudgetTier.CRITICAL
    }

    test("the legacy policy never tiers at all") {
        val d = driver()
        LegacyBudgetPolicy.budgetFor(d.state, d.player1, emptyList()).allowances shouldBe
            SearchAllowances.LEGACY
        LegacyBudgetPolicy.budgetForDecision(d.state, d.player1).allowances shouldBe
            SearchAllowances.LEGACY
    }
})
