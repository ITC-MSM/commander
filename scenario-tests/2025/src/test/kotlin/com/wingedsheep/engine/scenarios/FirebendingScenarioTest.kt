package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.tla.cards.FireSages
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.firebending
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.effects.ManaExpiry
import com.wingedsheep.sdk.scripting.effects.ManaRestriction
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Firebending N (Avatar: The Last Airbender, CR 702.189) — "Whenever this creature attacks,
 * add N {R}. Until end of combat, you don't lose this mana as steps and phases end."
 *
 * Exercised through Fire Sages (firebending 1) plus an inline firebending-2 creature. The
 * mechanic is composed entirely from existing primitives: an attack-triggered [AddManaEffect]
 * with [ManaExpiry.END_OF_COMBAT]. These tests pin the rules clauses: mana is added on attack,
 * held as a spendable combat-duration entry, multiple sources trigger separately, a non-attacker
 * adds nothing, and any leftover mana is discarded when combat ends.
 */
class FirebendingScenarioTest : FunSpec({

    // firebending 2 to prove the N parameter, plus a vanilla creature to attack with when we
    // want the firebending creature to stay home.
    val firebrand = card("Test Firebrand") {
        manaCost = "{1}{R}"
        typeLine = "Creature — Elemental"
        power = 1
        toughness = 1
        firebending(2)
    }
    val bear = card("Test Bear") {
        manaCost = "{1}{G}"
        typeLine = "Creature — Bear"
        power = 2
        toughness = 2
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(FireSages, firebrand, bear))
        return driver
    }

    /** Fully resolve the stack, resolving every triggered ability that lands on it. */
    fun GameTestDriver.resolveStack() {
        var guard = 0
        while (state.stack.isNotEmpty() && guard < 50) {
            bothPass()
            guard++
        }
    }

    fun GameTestDriver.combatMana(playerId: com.wingedsheep.sdk.model.EntityId) =
        (state.getEntity(playerId)?.get<ManaPoolComponent>()?.restrictedMana ?: emptyList())
            .filter { it.expiry == ManaExpiry.END_OF_COMBAT }

    test("attacking with firebending 1 adds one red, combat-duration, spendable-anywhere mana") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val attacker = driver.activePlayer!!
        val defender = driver.getOpponent(attacker)

        val sages = driver.putCreatureOnBattlefield(attacker, "Fire Sages")
        driver.removeSummoningSickness(sages)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attacker, listOf(sages), defender)
        driver.resolveStack()

        val combat = driver.combatMana(attacker)
        combat.size shouldBe 1
        combat.first().color shouldBe Color.RED
        // Firebending mana is spendable on anything (CR 702.189), modeled as an AnySpend entry.
        combat.first().restriction shouldBe ManaRestriction.AnySpend
        // It rides in the restricted list, not the plain red counter.
        driver.state.getEntity(attacker)?.get<ManaPoolComponent>()?.red shouldBe 0
    }

    test("firebending 2 adds two red mana — the N parameter is honored") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val attacker = driver.activePlayer!!
        val defender = driver.getOpponent(attacker)

        val fb = driver.putCreatureOnBattlefield(attacker, "Test Firebrand")
        driver.removeSummoningSickness(fb)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attacker, listOf(fb), defender)
        driver.resolveStack()

        driver.combatMana(attacker).size shouldBe 2
    }

    test("multiple firebending attackers trigger separately") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val attacker = driver.activePlayer!!
        val defender = driver.getOpponent(attacker)

        val sages = driver.putCreatureOnBattlefield(attacker, "Fire Sages")       // firebending 1
        val fb = driver.putCreatureOnBattlefield(attacker, "Test Firebrand")      // firebending 2
        driver.removeSummoningSickness(sages)
        driver.removeSummoningSickness(fb)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attacker, listOf(sages, fb), defender)
        driver.resolveStack()

        driver.combatMana(attacker).size shouldBe 3
    }

    test("a firebending creature that does not attack adds no mana") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val attacker = driver.activePlayer!!
        val defender = driver.getOpponent(attacker)

        val sages = driver.putCreatureOnBattlefield(attacker, "Fire Sages")
        val bearId = driver.putCreatureOnBattlefield(attacker, "Test Bear")
        driver.removeSummoningSickness(sages)
        driver.removeSummoningSickness(bearId)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        // Only the bear attacks; Fire Sages stays home, so its firebending never triggers.
        driver.declareAttackers(attacker, listOf(bearId), defender)
        driver.resolveStack()

        driver.combatMana(attacker).size shouldBe 0
    }

    test("combat-duration mana is discarded when combat ends (CR 702.189)") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val attacker = driver.activePlayer!!
        val defender = driver.getOpponent(attacker)

        val sages = driver.putCreatureOnBattlefield(attacker, "Fire Sages")
        driver.removeSummoningSickness(sages)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attacker, listOf(sages), defender)
        driver.resolveStack()
        driver.combatMana(attacker).size shouldBe 1

        // Run real game flow through end of combat into the postcombat main phase, where
        // CombatManager.endCombat clears END_OF_COMBAT mana.
        driver.passPriorityUntil(Step.POSTCOMBAT_MAIN)

        driver.combatMana(attacker).size shouldBe 0
        // The whole restricted list is empty — nothing leaked into end-of-turn mana.
        driver.state.getEntity(attacker)?.get<ManaPoolComponent>()?.restrictedMana?.size shouldBe 0
    }
})
