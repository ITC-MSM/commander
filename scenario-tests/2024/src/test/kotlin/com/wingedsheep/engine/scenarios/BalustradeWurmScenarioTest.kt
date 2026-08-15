package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.legalactions.EnumerationMode
import com.wingedsheep.engine.legalactions.LegalActionEnumerator
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dsk.cards.BalustradeWurm
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Balustrade Wurm (DSK #168) — {3}{G}{G} 5/5 Creature — Wurm.
 *
 * "This spell can't be countered.
 *  Trample, haste
 *  Delirium — {2}{G}{G}: Return this card from your graveyard to the battlefield with a finality
 *  counter on it. Activate only if there are four or more card types among cards in your graveyard
 *  and only as a sorcery."
 *
 * Exercises the Delirium-gated ([Conditions.Delirium]) graveyard activated ability that returns
 * the Wurm to the battlefield with a finality counter, and the can't-be-countered flag.
 */
class BalustradeWurmScenarioTest : FunSpec({

    val abilityId = BalustradeWurm.activatedAbilities.first().id

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun canActivate(driver: GameTestDriver, player: EntityId, wurm: EntityId): Boolean {
        val enumerator = LegalActionEnumerator.create(driver.cardRegistry)
        val actions = enumerator.enumerate(driver.state, player, EnumerationMode.FULL)
        return actions.any { (it.action as? ActivateAbility)?.sourceId == wurm }
    }

    test("can't be countered: the spell carries the uncounterable flag") {
        // The card's script flags the spell as uncounterable at card-definition level.
        BalustradeWurm.script.cantBeCountered shouldBe true
    }

    test("Delirium ability is not offered with fewer than four card types in graveyard") {
        val driver = newDriver()
        val player = driver.player1

        val wurm = driver.putCardInGraveyard(player, "Balustrade Wurm")
        // Two more types alongside the Wurm (creature): instant, sorcery → three types total.
        driver.putCardInGraveyard(player, "Lightning Bolt")
        driver.putCardInGraveyard(player, "Doom Blade")
        driver.giveMana(player, Color.GREEN, 4)

        canActivate(driver, player, wurm) shouldBe false
    }

    test("Delirium ability returns the Wurm to the battlefield with a finality counter") {
        val driver = newDriver()
        val player = driver.player1

        val wurm = driver.putCardInGraveyard(player, "Balustrade Wurm")
        // Four card types: creature (Wurm), instant, sorcery, enchantment.
        driver.putCardInGraveyard(player, "Lightning Bolt")
        driver.putCardInGraveyard(player, "Doom Blade")
        driver.putCardInGraveyard(player, "Test Enchantment")
        driver.giveMana(player, Color.GREEN, 4)

        canActivate(driver, player, wurm) shouldBe true

        driver.submit(
            ActivateAbility(playerId = player, sourceId = wurm, abilityId = abilityId)
        ).isSuccess shouldBe true
        driver.bothPass()
        driver.isPaused shouldBe false

        // The Wurm is back on the battlefield and out of the graveyard.
        driver.state.getZone(ZoneKey(player, Zone.BATTLEFIELD)).contains(wurm) shouldBe true
        driver.state.getZone(ZoneKey(player, Zone.GRAVEYARD)).contains(wurm) shouldBe false

        // It entered with a finality counter.
        val counters = driver.state.getEntity(wurm)?.get<CountersComponent>()?.counters ?: emptyMap()
        counters[CounterType.FINALITY] shouldBe 1
    }
})
