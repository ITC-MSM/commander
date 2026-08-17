package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.msh.cards.DoomReignsSupreme
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Doom Reigns Supreme (MSH #96) — {1}{B} Enchantment — Plan.
 *
 *   Whenever a Villain you control enters, each opponent loses 1 life and you gain 1 life. Put a
 *   plan counter on this enchantment.
 *   When the fifth plan counter is put on this enchantment, sacrifice it. When you do, target
 *   opponent exiles the top five cards of their library. You may cast up to two spells from among
 *   the exiled cards without paying their mana costs.
 *
 * The card is the only user of the new `maxCasts` bound on
 * [com.wingedsheep.sdk.scripting.effects.CastAnyNumberFromCollectionWithoutPayingCostEffect], so
 * this file doubles as the primitive's test: the capped loop must stop after the second cast even
 * though a third eligible card is still sitting in exile, it must still stop early when the
 * controller declines before spending the budget, the budget must survive a cast that pauses for
 * target selection (the continuation round trip the whole design rests on), and a pick that can't
 * be cast at all for want of a legal target must not spend one of the two.
 *
 * The Villain has to be *cast* rather than dropped in with `putPermanentOnBattlefield`, which
 * writes the battlefield directly and emits no zone-change event — no enters trigger would fire.
 */
class DoomReignsSupremeScenarioTest : FunSpec({

    // A Villain that is not a creature-type stub from the shared catalog, so the enters trigger's
    // subtype match is unambiguous.
    val villain = card("Test Villain Thug") {
        manaCost = "{B}"
        colorIdentity = "B"
        typeLine = "Creature — Human Villain"
        power = 1
        toughness = 1
    }

    // Three distinguishable nonland cards to stack on the opponent's library. Expensive on purpose:
    // they are only ever castable because the free cast waives their mana cost.
    val loot = (1..3).map { index ->
        card("Test Loot $index") {
            manaCost = "{6}{G}"
            colorIdentity = "G"
            typeLine = "Creature — Beast"
            power = 3
            toughness = 3
        }
    }

    /**
     * A spell whose only target requirement can never be satisfied here: the free cast is made by
     * the Plan's controller, and the *opponent* has no creatures. CR 601.2c stops the cast, so the
     * card stays in exile — the loop must not treat that pick as one of the two casts.
     */
    val removal = card("Test Removal") {
        manaCost = "{5}{B}"
        colorIdentity = "B"
        typeLine = "Instant"
        oracleText = "Destroy target creature an opponent controls."
        spell {
            val t = target("target creature an opponent controls", TargetCreature(filter = TargetFilter.CreatureOpponentControls))
            effect = Effects.Destroy(t)
        }
    }

    /** A spell that *does* have a legal target, so its free cast pauses for target selection. */
    val zap = card("Test Zap") {
        manaCost = "{5}{R}"
        colorIdentity = "R"
        typeLine = "Instant"
        oracleText = "Test Zap deals 3 damage to target creature."
        spell {
            val t = target("target creature", TargetCreature())
            effect = Effects.DealDamage(3, t)
        }
    }

    fun driver() = GameTestDriver().apply {
        registerCards(TestCards.all)
        registerCard(DoomReignsSupreme)
        registerCard(villain)
        registerCard(removal)
        registerCard(zap)
        loot.forEach { registerCard(it) }
        initMirrorMatch(Deck.of("Swamp" to 40), skipMulligans = true, startingPlayer = 0)
        passPriorityUntil(Step.PRECOMBAT_MAIN)
    }

    fun GameTestDriver.planCounters(id: EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLAN) ?: 0

    fun GameTestDriver.nameOf(id: EntityId): String? =
        state.getEntity(id)?.get<CardComponent>()?.name

    /** Resolve the stack until it empties or something asks the controller a question. */
    fun GameTestDriver.runUntilDecisionOrEmptyStack() {
        repeat(40) {
            if (state.pendingDecision != null) return
            if (stackSize == 0) return
            bothPass()
        }
    }

    /**
     * Board: the Plan already carrying four counters, and [topOfLibrary] (in order, first = top)
     * on the opponent's all-Swamp library, so the top five are those nonlands plus lands.
     * Casting one Villain puts the fifth counter on and fires the payoff.
     */
    fun setUp(
        topOfLibrary: List<String> = listOf("Test Loot 1", "Test Loot 2", "Test Loot 3"),
    ): Pair<GameTestDriver, EntityId> {
        val d = driver()
        val plan = d.putPermanentOnBattlefield(d.player1, "Doom Reigns Supreme")
        d.addComponent(plan, CountersComponent(mapOf(CounterType.PLAN to 4)))

        // Last call ends up on top, so the list reads top-down.
        topOfLibrary.reversed().forEach { d.putCardOnTopOfLibrary(d.player2, it) }

        val thug = d.putCardInHand(d.player1, "Test Villain Thug")
        d.giveMana(d.player1, Color.BLACK, 1)
        d.castSpell(d.player1, thug).isSuccess shouldBe true
        d.runUntilDecisionOrEmptyStack()
        return d to plan
    }

    test("a Villain entering drains, adds the fifth counter, and fires the exile payoff") {
        val (d, plan) = setUp()

        withClue("each opponent loses 1 life and you gain 1 life") {
            d.getLifeTotal(d.player1) shouldBe 21
            d.getLifeTotal(d.player2) shouldBe 19
        }
        withClue("the fifth plan counter sacrifices the enchantment") {
            d.planCounters(plan) shouldBe 0
            d.getGraveyardCardNames(d.player1).contains("Doom Reigns Supreme") shouldBe true
        }
        withClue("the target opponent exiles the top five cards of their library") {
            d.getExile(d.player2).size shouldBe 5
            d.getExileCardNames(d.player2).count { it == "Swamp" } shouldBe 2
        }

        val decision = d.state.pendingDecision
        withClue("the free-cast loop pauses during the reflexive ability's resolution") {
            (decision is SelectCardsDecision) shouldBe true
        }
        decision as SelectCardsDecision
        withClue("only the nonland cards are offered — a land is not a spell") {
            decision.options.mapNotNull { d.nameOf(it) }.toSet() shouldBe
                setOf("Test Loot 1", "Test Loot 2", "Test Loot 3")
        }
    }

    test("the loop stops after two casts even though a third card is still castable") {
        val (d, _) = setUp()

        repeat(2) { round ->
            val decision = d.state.pendingDecision as SelectCardsDecision
            val pick = decision.options.first { d.nameOf(it) == "Test Loot ${round + 1}" }
            d.submitCardSelection(d.player1, listOf(pick)).error shouldBe null
        }

        withClue("the cap is spent, so no third card is offered even though one is left in exile") {
            d.state.pendingDecision shouldBe null
        }

        d.runUntilDecisionOrEmptyStack()

        val mine = d.getPermanents(d.player1).mapNotNull { d.nameOf(it) }.toSet()
        withClue("both free-cast spells resolve under the caster's control, the third never does") {
            mine.contains("Test Loot 1") shouldBe true
            mine.contains("Test Loot 2") shouldBe true
            mine.contains("Test Loot 3") shouldBe false
        }
        withClue("the uncast third card stays in the opponent's exile with the two lands") {
            d.getExileCardNames(d.player2).toSet() shouldBe setOf("Test Loot 3", "Swamp")
            d.getExile(d.player2).size shouldBe 3
        }
    }

    test("declining before the cap is spent ends the loop early") {
        val (d, _) = setUp()

        val first = d.state.pendingDecision as SelectCardsDecision
        val pick = first.options.first { d.nameOf(it) == "Test Loot 1" }
        d.submitCardSelection(d.player1, listOf(pick)).error shouldBe null

        withClue("one cast of two spent, so the loop offers the remaining cards again") {
            (d.state.pendingDecision is SelectCardsDecision) shouldBe true
        }
        d.submitCardSelection(d.player1, emptyList()).error shouldBe null

        withClue("declining stops the loop rather than re-offering the last of the budget") {
            d.state.pendingDecision shouldBe null
        }

        d.runUntilDecisionOrEmptyStack()

        withClue("only the one chosen card was cast; the rest stay in exile") {
            d.getPermanents(d.player1).mapNotNull { d.nameOf(it) }.contains("Test Loot 1") shouldBe true
            d.getExileCardNames(d.player2).toSet() shouldBe setOf("Test Loot 2", "Test Loot 3", "Swamp")
        }
    }

    test("a pick with no legal target casts nothing and doesn't spend one of the two casts") {
        val (d, _) = setUp(listOf("Test Removal", "Test Loot 1", "Test Loot 2"))

        val first = d.state.pendingDecision as SelectCardsDecision
        val pick = first.options.first { d.nameOf(it) == "Test Removal" }
        d.submitCardSelection(d.player1, listOf(pick)).error shouldBe null

        withClue("the uncastable card is out of the pool, but the loop keeps going") {
            (d.state.pendingDecision as SelectCardsDecision).options.mapNotNull { d.nameOf(it) }.toSet() shouldBe
                setOf("Test Loot 1", "Test Loot 2")
        }

        val second = d.state.pendingDecision as SelectCardsDecision
        d.submitCardSelection(d.player1, listOf(second.options.first { d.nameOf(it) == "Test Loot 1" }))
            .error shouldBe null

        withClue("both casts are still available: one spell cast so far, not two") {
            val third = d.state.pendingDecision as? SelectCardsDecision
            (third != null) shouldBe true
            d.submitCardSelection(d.player1, listOf(third!!.options.first { d.nameOf(it) == "Test Loot 2" }))
                .error shouldBe null
        }
        withClue("and after the second real cast the cap is spent") {
            d.state.pendingDecision shouldBe null
        }

        d.runUntilDecisionOrEmptyStack()

        val mine = d.getPermanents(d.player1).mapNotNull { d.nameOf(it) }.toSet()
        withClue("two spells were cast for free after the wasted pick") {
            mine.contains("Test Loot 1") shouldBe true
            mine.contains("Test Loot 2") shouldBe true
        }
        withClue("the spell that couldn't be cast stays in exile (CR 601.2c)") {
            d.getExileCardNames(d.player2).contains("Test Removal") shouldBe true
        }
    }

    test("the budget survives a free cast that pauses for target selection") {
        val (d, _) = setUp(listOf("Test Zap", "Test Loot 1", "Test Loot 2"))
        val thug = d.getPermanents(d.player1).first { d.nameOf(it) == "Test Villain Thug" }

        val first = d.state.pendingDecision as SelectCardsDecision
        d.submitCardSelection(d.player1, listOf(first.options.first { d.nameOf(it) == "Test Zap" }))
            .error shouldBe null

        withClue("a targeted free cast pauses mid-loop, so the budget has to ride the continuation") {
            (d.state.pendingDecision is ChooseTargetsDecision) shouldBe true
        }
        d.submitTargetSelection(d.player1, listOf(thug)).error shouldBe null

        withClue("the loop resumes on the far side of the pause with the last cast still to spend") {
            (d.state.pendingDecision as SelectCardsDecision).options.mapNotNull { d.nameOf(it) }.toSet() shouldBe
                setOf("Test Loot 1", "Test Loot 2")
        }
        val second = d.state.pendingDecision as SelectCardsDecision
        d.submitCardSelection(d.player1, listOf(second.options.first { d.nameOf(it) == "Test Loot 1" }))
            .error shouldBe null

        withClue("the paused cast counted, so two are spent and no third card is offered") {
            d.state.pendingDecision shouldBe null
        }

        d.runUntilDecisionOrEmptyStack()

        withClue("the last card was never offered and stays in the opponent's exile") {
            d.getExileCardNames(d.player2).contains("Test Loot 2") shouldBe true
        }
    }

    test("three-player: each opponent loses 1 life while the controller gains exactly 1") {
        val d = GameTestDriver().apply {
            registerCards(TestCards.all)
            registerCard(DoomReignsSupreme)
            registerCard(villain)
        }
        val players = d.initMultiplayer(List(3) { Deck.of("Swamp" to 40) }, skipMulligans = true)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Three counters, so the Villain's counter is the fourth — the payoff never fires and this
        // test stays about the life swing alone.
        val plan = d.putPermanentOnBattlefield(players[0], "Doom Reigns Supreme")
        d.addComponent(plan, CountersComponent(mapOf(CounterType.PLAN to 3)))

        val thug = d.putCardInHand(players[0], "Test Villain Thug")
        d.giveMana(players[0], Color.BLACK, 1)
        d.castSpell(players[0], thug).isSuccess shouldBe true
        d.runUntilDecisionOrEmptyStack()

        withClue("the gain is a flat 1, not one per opponent — this is why it isn't DrainLife") {
            d.getLifeTotal(players[0]) shouldBe 21
        }
        d.getLifeTotal(players[1]) shouldBe 19
        d.getLifeTotal(players[2]) shouldBe 19
        d.planCounters(plan) shouldBe 4
    }
})
