package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.wwk.cards.KalastriaHighborn
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Kalastria Highborn (WWK, reprinted in FDN) — {B}{B} Creature — Vampire Shaman, 2/2.
 *
 * "Whenever this creature or another Vampire you control dies, you may pay {B}. If you do, target
 *  player loses 2 life and you gain 2 life."
 *
 * Proves: another Vampire dying + paying {B} drains the chosen player and gains you 2; the source's
 * own death also triggers (the "this creature or" clause); declining the {B} does nothing; and a
 * non-Vampire creature dying doesn't trigger at all.
 */
class KalastriaHighbornScenarioTest : FunSpec({

    val vampireToken = card("Test Vampire Ally") {
        manaCost = "{B}"
        typeLine = "Creature — Vampire"
        power = 1
        toughness = 1
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(KalastriaHighborn, vampireToken))
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 20, "Mountain" to 20), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    /**
     * Resolve the stack, answering Kalastria's trigger: pick [targetPlayer], then accept/decline the
     * {B} may-pay per [pay] (auto-tapping mana sources when asked).
     */
    fun GameTestDriver.resolveTrigger(targetPlayer: EntityId, pay: Boolean) {
        var guard = 0
        while ((stackSize > 0 || state.pendingDecision != null) && guard < 40) {
            when (val pending = state.pendingDecision) {
                is ChooseTargetsDecision -> submitTargetSelection(pending.playerId, listOf(targetPlayer))
                is YesNoDecision -> submitYesNo(pending.playerId, pay)
                is SelectManaSourcesDecision -> submitManaAutoPayOrDecline(pending.playerId, true)
                else -> bothPass()
            }
            guard++
        }
    }

    /** Opponent kills [victim] with a Lightning Bolt so it dies to a real death event. */
    fun GameTestDriver.boltToDeath(victim: EntityId) {
        val you = activePlayer!!
        val opponent = getOpponent(you)
        giveMana(opponent, Color.RED, 1)
        val bolt = putCardInHand(opponent, "Lightning Bolt")
        passPriority(you)
        castSpellWithTargets(opponent, bolt, listOf(ChosenTarget.Permanent(victim))).error shouldBe null
    }

    test("another Vampire dying + paying {B} drains the chosen player and gains you 2") {
        val driver = createDriver()
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)
        val youBefore = driver.getLifeTotal(you)
        val oppBefore = driver.getLifeTotal(opponent)

        driver.putCreatureOnBattlefield(you, "Kalastria Highborn")
        val ally = driver.putCreatureOnBattlefield(you, "Test Vampire Ally")
        driver.giveMana(you, Color.BLACK, 1) // to pay the {B}

        driver.boltToDeath(ally)
        driver.resolveTrigger(targetPlayer = opponent, pay = true)

        driver.getLifeTotal(opponent) shouldBe (oppBefore - 2)
        driver.getLifeTotal(you) shouldBe (youBefore + 2)
    }

    test("Kalastria's own death also triggers the drain") {
        val driver = createDriver()
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)
        val youBefore = driver.getLifeTotal(you)
        val oppBefore = driver.getLifeTotal(opponent)

        val highborn = driver.putCreatureOnBattlefield(you, "Kalastria Highborn")
        driver.giveMana(you, Color.BLACK, 1)

        driver.boltToDeath(highborn)
        driver.resolveTrigger(targetPlayer = opponent, pay = true)

        driver.getLifeTotal(opponent) shouldBe (oppBefore - 2)
        driver.getLifeTotal(you) shouldBe (youBefore + 2)
    }

    test("declining the {B} does nothing") {
        val driver = createDriver()
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)
        val youBefore = driver.getLifeTotal(you)
        val oppBefore = driver.getLifeTotal(opponent)

        driver.putCreatureOnBattlefield(you, "Kalastria Highborn")
        val ally = driver.putCreatureOnBattlefield(you, "Test Vampire Ally")

        driver.boltToDeath(ally)
        driver.resolveTrigger(targetPlayer = opponent, pay = false)

        driver.getLifeTotal(opponent) shouldBe oppBefore
        driver.getLifeTotal(you) shouldBe youBefore
    }

    test("a non-Vampire creature dying does not trigger") {
        val driver = createDriver()
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)
        val youBefore = driver.getLifeTotal(you)
        val oppBefore = driver.getLifeTotal(opponent)

        driver.putCreatureOnBattlefield(you, "Kalastria Highborn")
        val bears = driver.putCreatureOnBattlefield(you, "Grizzly Bears") // non-Vampire
        driver.giveMana(you, Color.BLACK, 1)

        driver.boltToDeath(bears)
        // No trigger should be waiting — just let the stack settle.
        var guard = 0
        while (driver.stackSize > 0 && guard < 20) { driver.bothPass(); guard++ }

        driver.getLifeTotal(opponent) shouldBe oppBefore
        driver.getLifeTotal(you) shouldBe youBefore
    }
})
