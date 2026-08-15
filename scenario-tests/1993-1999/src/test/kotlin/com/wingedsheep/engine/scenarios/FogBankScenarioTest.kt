package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.usg.cards.FogBank
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Fog Bank (USG #75) — "Prevent all combat damage that would be dealt to and dealt by this creature."
 *
 * Exercises the source-relative static prevention pair, including the new
 * [com.wingedsheep.sdk.scripting.events.SourceFilter.Self] filter used for the "dealt by" half.
 */
class FogBankScenarioTest : FunSpec({

    fun newGame(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(FogBank))
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun resolveStack(driver: GameTestDriver) {
        var guard = 0
        while (guard++ < 30 && driver.state.stack.isNotEmpty() && !driver.isPaused) driver.bothPass()
    }

    fun GameTestDriver.markedDamage(id: EntityId): Int =
        state.getEntity(id)?.get<DamageComponent>()?.amount ?: 0

    test("prevents combat damage dealt TO Fog Bank when it blocks") {
        val driver = newGame()
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)

        val attacker = driver.putCreatureOnBattlefield(you, "Centaur Courser") // 3/3
        driver.removeSummoningSickness(attacker)
        val fogBank = driver.putCreatureOnBattlefield(opponent, "Fog Bank") // 0/2

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(you, listOf(attacker), defendingPlayer = opponent).error shouldBe null
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)
        driver.declareBlockers(opponent, mapOf(fogBank to listOf(attacker)))
        driver.passPriorityUntil(Step.COMBAT_DAMAGE)
        resolveStack(driver)

        // The 3 combat damage to Fog Bank is prevented — it takes 0 and survives its 2 toughness.
        driver.markedDamage(fogBank) shouldBe 0
        driver.state.getBattlefield().contains(fogBank) shouldBe true
    }

    test("prevents combat damage dealt BY Fog Bank (SourceFilter.Self)") {
        val driver = newGame()
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)

        val attacker = driver.putCreatureOnBattlefield(you, "Centaur Courser") // 3/3
        driver.removeSummoningSickness(attacker)
        val fogBank = driver.putCreatureOnBattlefield(opponent, "Fog Bank") // 0/2

        // Give Fog Bank a +1/+1 counter so it would deal 1 combat damage — that damage must be
        // prevented by the "dealt by this creature" half.
        driver.replaceState(
            driver.state.updateEntity(fogBank) {
                it.with(CountersComponent(mapOf(CounterType.PLUS_ONE_PLUS_ONE to 1)))
            }
        )

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(you, listOf(attacker), defendingPlayer = opponent).error shouldBe null
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)
        driver.declareBlockers(opponent, mapOf(fogBank to listOf(attacker)))
        driver.passPriorityUntil(Step.COMBAT_DAMAGE)
        resolveStack(driver)

        // Fog Bank's own combat damage is prevented — the attacker takes 0 from it.
        driver.markedDamage(attacker) shouldBe 0
        // And the attacker's damage to Fog Bank is prevented too, so Fog Bank survives.
        driver.state.getBattlefield().contains(fogBank) shouldBe true
    }

    test("does not prevent noncombat damage to Fog Bank") {
        val driver = newGame()
        val you = driver.activePlayer!!

        val fogBank = driver.putCreatureOnBattlefield(you, "Fog Bank") // 0/2

        driver.giveMana(you, Color.RED, 1)
        val bolt = driver.putCardInHand(you, "Lightning Bolt")
        driver.castSpellWithTargets(you, bolt, listOf(ChosenTarget.Permanent(fogBank)))
        resolveStack(driver)

        // Lightning Bolt's 3 noncombat damage is not prevented (prevention is combat-only) — Fog Bank dies.
        driver.state.getBattlefield().contains(fogBank) shouldBe false
    }
})
