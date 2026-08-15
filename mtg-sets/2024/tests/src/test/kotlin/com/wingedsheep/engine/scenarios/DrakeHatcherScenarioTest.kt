package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fdn.cards.DrakeHatcher
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Drake Hatcher (FDN #35) — {1}{U} 1/3 Creature — Human Wizard.
 *
 * "Vigilance, prowess
 *  Whenever this creature deals combat damage to a player, put that many incubation counters on it.
 *  Remove three incubation counters from this creature: Create a 2/2 blue Drake creature token with flying."
 *
 * Pins the two counter-driven halves: the combat-damage trigger accumulates "that many"
 * incubation counters ([CounterType.INCUBATION], a new card-specific resource counter), and the
 * activated ability spends three of them to hatch a Drake token.
 */
class DrakeHatcherScenarioTest : FunSpec({

    val hatchAbilityId = DrakeHatcher.activatedAbilities.first().id

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + DrakeHatcher)
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true, startingPlayer = 0)
        return driver
    }

    test("combat damage to a player adds that many incubation counters") {
        val driver = newDriver()
        val player = driver.player1
        val opponent = driver.getOpponent(player)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val hatcher = driver.putCreatureOnBattlefield(player, "Drake Hatcher")

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(player, listOf(hatcher), opponent)
        driver.passPriorityUntil(Step.POSTCOMBAT_MAIN)

        // 1-power attacker, unblocked: opponent takes 1 and the Hatcher gains 1 incubation counter.
        driver.getLifeTotal(opponent) shouldBe 19
        val counters = driver.state.getEntity(hatcher)?.get<CountersComponent>()?.counters ?: emptyMap()
        counters[CounterType.INCUBATION] shouldBe 1
    }

    test("removing three incubation counters hatches a 2/2 Drake token") {
        val driver = newDriver()
        val player = driver.player1
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val hatcher = driver.putCreatureOnBattlefield(player, "Drake Hatcher")
        driver.addComponent(hatcher, CountersComponent(mapOf(CounterType.INCUBATION to 3)))

        val battlefieldBefore = driver.state.getZone(ZoneKey(player, Zone.BATTLEFIELD)).size

        driver.submit(
            ActivateAbility(playerId = player, sourceId = hatcher, abilityId = hatchAbilityId)
        ).isSuccess shouldBe true
        driver.bothPass()
        driver.isPaused shouldBe false

        // A new permanent (the Drake token) joined the battlefield.
        driver.state.getZone(ZoneKey(player, Zone.BATTLEFIELD)).size shouldBe battlefieldBefore + 1
        // The three incubation counters were spent as the activation cost.
        val counters = driver.state.getEntity(hatcher)?.get<CountersComponent>()?.counters ?: emptyMap()
        (counters[CounterType.INCUBATION] ?: 0) shouldBe 0
    }
})
