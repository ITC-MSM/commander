package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.legalactions.EnumerationMode
import com.wingedsheep.engine.legalactions.LegalActionEnumerator
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dsk.cards.ResurrectedCultist
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Resurrected Cultist (DSK #115) — {2}{B} 4/1 Creature — Human Cleric.
 *
 * "Delirium — {2}{B}{B}: Return this card from your graveyard to the battlefield with a finality
 *  counter on it. Activate only if there are four or more card types among cards in your graveyard
 *  and only as a sorcery."
 *
 * Exercises the Delirium-gated ([Conditions.Delirium]) graveyard activated ability that returns
 * the Cultist to the battlefield with a finality counter. Mirrors Balustrade Wurm's ability.
 */
class ResurrectedCultistScenarioTest : FunSpec({

    val abilityId = ResurrectedCultist.activatedAbilities.first().id

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun canActivate(driver: GameTestDriver, player: EntityId, cultist: EntityId): Boolean {
        val enumerator = LegalActionEnumerator.create(driver.cardRegistry)
        val actions = enumerator.enumerate(driver.state, player, EnumerationMode.FULL)
        return actions.any { (it.action as? ActivateAbility)?.sourceId == cultist }
    }

    test("Delirium ability is not offered with fewer than four card types in graveyard") {
        val driver = newDriver()
        val player = driver.player1

        val cultist = driver.putCardInGraveyard(player, "Resurrected Cultist")
        // Two more types alongside the Cultist (creature): instant, sorcery → three types total.
        driver.putCardInGraveyard(player, "Lightning Bolt")
        driver.putCardInGraveyard(player, "Doom Blade")
        driver.giveMana(player, Color.BLACK, 4)

        canActivate(driver, player, cultist) shouldBe false
    }

    test("Delirium ability returns the Cultist to the battlefield with a finality counter") {
        val driver = newDriver()
        val player = driver.player1

        val cultist = driver.putCardInGraveyard(player, "Resurrected Cultist")
        // Four card types: creature (Cultist), instant, sorcery, enchantment.
        driver.putCardInGraveyard(player, "Lightning Bolt")
        driver.putCardInGraveyard(player, "Doom Blade")
        driver.putCardInGraveyard(player, "Test Enchantment")
        driver.giveMana(player, Color.BLACK, 4)

        canActivate(driver, player, cultist) shouldBe true

        driver.submit(
            ActivateAbility(playerId = player, sourceId = cultist, abilityId = abilityId)
        ).isSuccess shouldBe true
        driver.bothPass()
        driver.isPaused shouldBe false

        driver.state.getZone(ZoneKey(player, Zone.BATTLEFIELD)).contains(cultist) shouldBe true
        driver.state.getZone(ZoneKey(player, Zone.GRAVEYARD)).contains(cultist) shouldBe false

        val counters = driver.state.getEntity(cultist)?.get<CountersComponent>()?.counters ?: emptyMap()
        counters[CounterType.FINALITY] shouldBe 1
    }
})
