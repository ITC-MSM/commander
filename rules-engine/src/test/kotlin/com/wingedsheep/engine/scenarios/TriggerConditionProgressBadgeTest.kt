package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.SolvedComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.engine.view.ClientCardEffect
import com.wingedsheep.engine.view.ClientStateTransformer
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.solvedStaticAbility
import com.wingedsheep.sdk.dsl.toSolve
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * The client's "how close is this?" badge on a permanent whose triggered ability has an
 * intervening-if (`ClientStateTransformer.buildTriggerConditionBadges`).
 *
 * A Case is the motivating shape: its "to solve" line is the printed criterion ANDed with "and this
 * Case is not solved" (CR 719.3a), so the condition the engine holds is a composite and the badge
 * has to look inside it. The counts themselves come from
 * [com.wingedsheep.engine.handlers.ConditionEvaluator.countProgress], which runs the same counting
 * code the trigger does — a badge reading "3/3" and a Case that refuses to solve would be a bug in
 * one of the two.
 */
class TriggerConditionProgressBadgeTest : FunSpec({

    /** "To solve — You control three or more creatures." — a plain battlefield count. */
    val countCase = card("Test Case Creature Count") {
        manaCost = "{W}"
        typeLine = "Enchantment — Case"
        oracleText = "To solve — You control three or more creatures.\n" +
            "Solved — Creatures you control get +1/+0."
        toSolve(Conditions.YouControlAtLeast(3, GameObjectFilter.Creature))
        solvedStaticAbility { ability = ModifyStats(1, 0, Filters.AllControlledCreatures) }
    }

    /** "To solve — You've cast two or more instant and sorcery spells this turn." */
    val castCase = card("Test Case Spell Count") {
        manaCost = "{U}"
        typeLine = "Enchantment — Case"
        oracleText = "To solve — You've cast two or more instant and sorcery spells this turn.\n" +
            "Solved — Creatures you control get +1/+0."
        toSolve(Conditions.YouCastSpellsThisTurn(2, GameObjectFilter.InstantOrSorcery))
        solvedStaticAbility { ability = ModifyStats(1, 0, Filters.AllControlledCreatures) }
    }

    /** "To solve — Two or more creatures attacked this turn." */
    val attackCase = card("Test Case Attack Count") {
        manaCost = "{R}"
        typeLine = "Enchantment — Case"
        oracleText = "To solve — Two or more creatures attacked this turn.\n" +
            "Solved — Creatures you control get +1/+0."
        toSolve(Conditions.CreaturesAttackedThisTurn(2))
        solvedStaticAbility { ability = ModifyStats(1, 0, Filters.AllControlledCreatures) }
    }

    /** "To solve — You control no creatures." — the criterion that counts *down* to zero. */
    val emptyBoardCase = card("Test Case Empty Board") {
        manaCost = "{B}"
        typeLine = "Enchantment — Case"
        oracleText = "To solve — You control no creatures.\nSolved — Creatures you control get +1/+0."
        toSolve(Conditions.YouControl(GameObjectFilter.Creature, negate = true))
        solvedStaticAbility { ability = ModifyStats(1, 0, Filters.AllControlledCreatures) }
    }

    /** Two countable clauses in one intervening-if — not a Case, just the composite shape. */
    val twoClauseEnchantment = card("Test Two Clause Watcher") {
        manaCost = "{G}"
        typeLine = "Enchantment"
        oracleText = "At the beginning of your end step, if you control a creature and you have 25 " +
            "or more life, you gain 1 life."
        triggeredAbility {
            trigger = Triggers.YourEndStep
            interveningIf = Conditions.All(
                Conditions.YouControlAtLeast(1, GameObjectFilter.Creature),
                Conditions.LifeAtLeast(25)
            )
            effect = Effects.GainLife(1)
        }
    }

    /** A cheap instant, so a test can raise the spells-cast-this-turn count. */
    val cantrip = card("Test Cantrip") {
        manaCost = "{U}"
        typeLine = "Instant"
        oracleText = "Draw a card."
        spell { effect = Effects.DrawCards(1) }
    }

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        listOf(countCase, castCase, attackCase, emptyBoardCase, twoClauseEnchantment, cantrip)
            .forEach { driver.registerCard(it) }
        driver.initMirrorMatch(Deck.of("Forest" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    /** The progress badges the first player's client would render on [id]. */
    fun GameTestDriver.progressBadges(id: EntityId): List<ClientCardEffect> =
        ClientStateTransformer(cardRegistry)
            .transform(state, player1)
            .cards.getValue(id)
            .activeEffects
            .filter { it.effectId.startsWith("condition_compare") }

    fun GameTestDriver.isSolved(id: EntityId): Boolean =
        state.getEntity(id)?.has<SolvedComponent>() == true

    test("a Case's criterion is badged even though the engine sees it ANDed with 'not solved'") {
        val driver = newDriver()
        val case = driver.putPermanentOnBattlefield(driver.player1, "Test Case Creature Count")

        driver.progressBadges(case).map { it.name } shouldBe listOf("0/3")
        driver.progressBadges(case).single().icon shouldBe "condition-unmet"

        driver.putCreatureOnBattlefield(driver.player1, "Centaur Courser")
        driver.progressBadges(case).map { it.name } shouldBe listOf("1/3")

        repeat(2) { driver.putCreatureOnBattlefield(driver.player1, "Centaur Courser") }
        driver.progressBadges(case).map { it.name } shouldBe listOf("3/3")
        driver.progressBadges(case).single().icon shouldBe "condition-met"
    }

    test("the badge's tooltip names the criterion it is counting") {
        val driver = newDriver()
        val case = driver.putPermanentOnBattlefield(driver.player1, "Test Case Creature Count")

        driver.progressBadges(case).single().description shouldBe
            "the number of creatures you control is at least 3 (0/3)"
    }

    test("a solved Case drops the badge instead of freezing it at a met count") {
        val driver = newDriver()
        val case = driver.putPermanentOnBattlefield(driver.player1, "Test Case Creature Count")
        repeat(3) { driver.putCreatureOnBattlefield(driver.player1, "Centaur Courser") }

        driver.passPriorityUntil(Step.END)
        driver.bothPass()

        driver.isSolved(case) shouldBe true
        driver.progressBadges(case) shouldBe emptyList()
    }

    test("spells cast this turn count toward the badge") {
        val driver = newDriver()
        val case = driver.putPermanentOnBattlefield(driver.player1, "Test Case Spell Count")

        driver.progressBadges(case).map { it.name } shouldBe listOf("0/2")

        val spell = driver.putCardInHand(driver.player1, "Test Cantrip")
        driver.giveMana(driver.player1, Color.BLUE, 1)
        driver.castSpell(driver.player1, spell).error shouldBe null
        driver.bothPass()

        driver.progressBadges(case).map { it.name } shouldBe listOf("1/2")
    }

    test("creatures that attacked this turn count toward the badge") {
        val driver = newDriver()
        val case = driver.putPermanentOnBattlefield(driver.player1, "Test Case Attack Count")
        val opponent = driver.state.turnOrder.first { it != driver.player1 }
        val attackers = (1..2).map {
            driver.putCreatureOnBattlefield(driver.player1, "Centaur Courser")
                .also { id -> driver.removeSummoningSickness(id) }
        }

        driver.progressBadges(case).map { it.name } shouldBe listOf("0/2")

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(driver.player1, attackers, defendingPlayer = opponent).error shouldBe null

        driver.progressBadges(case).map { it.name } shouldBe listOf("2/2")
        driver.progressBadges(case).single().icon shouldBe "condition-met"
    }

    test("a criterion that wants none of something counts down to zero") {
        val driver = newDriver()
        val case = driver.putPermanentOnBattlefield(driver.player1, "Test Case Empty Board")
        val creature = driver.putCreatureOnBattlefield(driver.player1, "Centaur Courser")

        driver.progressBadges(case).map { it.name } shouldBe listOf("1/0")
        driver.progressBadges(case).single().icon shouldBe "condition-unmet"

        driver.moveToGraveyard(creature)
        driver.progressBadges(case).map { it.name } shouldBe listOf("0/0")
        driver.progressBadges(case).single().icon shouldBe "condition-met"
    }

    test("two countable clauses badge separately, under distinct ids the client can key on") {
        val driver = newDriver()
        val watcher = driver.putPermanentOnBattlefield(driver.player1, "Test Two Clause Watcher")
        driver.putCreatureOnBattlefield(driver.player1, "Centaur Courser")
        driver.setLifeTotal(driver.player1, 20)

        val badges = driver.progressBadges(watcher)
        badges.map { it.name } shouldBe listOf("1/1", "20/25")
        badges.map { it.effectId }.toSet().size shouldBe 2
        badges.map { it.icon } shouldBe listOf("condition-met", "condition-unmet")
    }
})
