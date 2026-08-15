package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.legalactions.EnumerationMode
import com.wingedsheep.engine.legalactions.LegalActionEnumerator
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dft.cards.ProwcatcherSpecialist
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Prowcatcher Specialist (DFT #142) — {1}{R} 2/1 Creature — Goblin Warrior.
 *
 * "Haste
 *  Exhaust — {3}{R}: Put two +1/+1 counters on this creature. (Activate each exhaust ability only
 *  once.)"
 *
 * Exercises the Exhaust keyword's once-per-game activation restriction (the ability resolves and
 * adds counters the first time, and is no longer offered afterward).
 */
class ProwcatcherSpecialistScenarioTest : FunSpec({

    val abilityId = ProwcatcherSpecialist.activatedAbilities.first().id

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun canActivate(driver: GameTestDriver, player: EntityId, source: EntityId): Boolean {
        val enumerator = LegalActionEnumerator.create(driver.cardRegistry)
        val actions = enumerator.enumerate(driver.state, player, EnumerationMode.FULL)
        return actions.any { (it.action as? ActivateAbility)?.sourceId == source }
    }

    fun counters(driver: GameTestDriver, id: EntityId): Int =
        driver.state.getEntity(id)?.get<CountersComponent>()?.counters?.get(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    test("Exhaust ability adds two +1/+1 counters the first time it is activated") {
        val driver = newDriver()
        val player = driver.player1

        val goblin = driver.putCreatureOnBattlefield(player, "Prowcatcher Specialist")
        driver.giveMana(player, Color.RED, 4) // pays {3}{R}

        counters(driver, goblin) shouldBe 0
        canActivate(driver, player, goblin) shouldBe true

        driver.submit(
            ActivateAbility(playerId = player, sourceId = goblin, abilityId = abilityId)
        ).isSuccess shouldBe true
        driver.bothPass()
        driver.isPaused shouldBe false

        counters(driver, goblin) shouldBe 2
    }

    test("Exhaust ability cannot be activated a second time (Exhaust: activate only once)") {
        val driver = newDriver()
        val player = driver.player1

        val goblin = driver.putCreatureOnBattlefield(player, "Prowcatcher Specialist")
        driver.giveMana(player, Color.RED, 8) // enough mana for two activations

        driver.submit(
            ActivateAbility(playerId = player, sourceId = goblin, abilityId = abilityId)
        ).isSuccess shouldBe true
        driver.bothPass()

        // Even with mana to spare, the exhaust ability is no longer offered.
        canActivate(driver, player, goblin) shouldBe false
        counters(driver, goblin) shouldBe 2
    }
})
