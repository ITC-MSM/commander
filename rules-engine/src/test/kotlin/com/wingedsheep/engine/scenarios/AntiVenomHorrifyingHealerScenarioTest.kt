package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Anti-Venom, Horrifying Healer (SPM) — "If damage would be dealt to Anti-Venom, prevent that
 * damage and put that many +1/+1 counters on him." Pins the `RecipientFilter.Self`
 * `ReplaceDamageWithCounters` now wired on the creature-damage paths (`DamageUtils.applyDamage`
 * non-player branch + `CombatDamageManager.applyDamageToCreature`).
 */
class AntiVenomHorrifyingHealerScenarioTest : FunSpec({

    fun newGame(): Triple<GameTestDriver, EntityId, EntityId> {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val you = driver.activePlayer!!
        val opponent = driver.state.turnOrder.first { it != you }
        return Triple(driver, you, opponent)
    }

    fun resolveStack(driver: GameTestDriver) {
        var guard = 0
        while (guard++ < 30 && driver.state.stack.isNotEmpty() && !driver.isPaused) driver.bothPass()
    }

    fun GameTestDriver.bolt(player: EntityId, target: ChosenTarget) {
        giveMana(player, Color.RED, 1)
        val b = putCardInHand(player, "Lightning Bolt")
        castSpellWithTargets(player, b, listOf(target))
        bothPass()
        resolveStack(this)
    }

    test("noncombat damage to Anti-Venom is prevented and put on him as +1/+1 counters") {
        val (driver, you, opponent) = newGame()
        val av = driver.putCreatureOnBattlefield(you, "Anti-Venom, Horrifying Healer") // 5/5

        // You have priority in your main phase; bolt your own Anti-Venom (damage to him is replaced
        // regardless of the source).
        driver.bolt(you, ChosenTarget.Permanent(av)) // 3 damage

        // Damage replaced entirely — none marked, three +1/+1 counters, still on the battlefield.
        (driver.state.getEntity(av)?.get<DamageComponent>()?.amount ?: 0) shouldBe 0
        (driver.state.getEntity(av)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0) shouldBe 3
        driver.state.getBattlefield().contains(av) shouldBe true
    }
})
