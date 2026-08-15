package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.legalactions.EnumerationMode
import com.wingedsheep.engine.legalactions.LegalActionEnumerator
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.UchbenbakTheGreatMistake
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Uchbenbak, the Great Mistake (LCI #242) — {3}{U}{B} 6/4 Legendary Creature — Skeleton Horror.
 *
 * "Vigilance, menace
 *  Descend 8 — {4}{U}{B}: Return this card from your graveyard to the battlefield with a finality
 *  counter on it. Activate only if there are eight or more permanent cards in your graveyard and
 *  only as a sorcery. (If a creature with a finality counter on it would die, exile it instead.)"
 *
 * Exercises the Descend-8-gated ([Conditions.CardsInGraveyardMatchingAtLeast] over
 * [GameObjectFilter.Permanent]) graveyard activated ability that returns Uchbenbak to the
 * battlefield with a finality counter, and confirms non-permanent cards don't count toward the gate.
 */
class UchbenbakTheGreatMistakeScenarioTest : FunSpec({

    val abilityId = UchbenbakTheGreatMistake.activatedAbilities.first().id

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun giveActivationMana(driver: GameTestDriver, player: EntityId) {
        // {4}{U}{B}: 4 generic + 1 blue + 1 black.
        driver.giveMana(player, Color.BLUE, 5)
        driver.giveMana(player, Color.BLACK, 1)
    }

    fun canActivate(driver: GameTestDriver, player: EntityId, uchbenbak: EntityId): Boolean {
        val enumerator = LegalActionEnumerator.create(driver.cardRegistry)
        val actions = enumerator.enumerate(driver.state, player, EnumerationMode.FULL)
        return actions.any { (it.action as? ActivateAbility)?.sourceId == uchbenbak }
    }

    test("keyword abilities: vigilance and menace") {
        val keywords = UchbenbakTheGreatMistake.keywords
        keywords.contains(com.wingedsheep.sdk.core.Keyword.VIGILANCE) shouldBe true
        keywords.contains(com.wingedsheep.sdk.core.Keyword.MENACE) shouldBe true
    }

    test("Descend 8 ability is not offered with fewer than eight permanent cards in graveyard") {
        val driver = newDriver()
        val player = driver.player1

        val uchbenbak = driver.putCardInGraveyard(player, "Uchbenbak, the Great Mistake")
        // Six more permanent (land) cards → seven permanent cards total, one short of eight.
        repeat(6) { driver.putCardInGraveyard(player, "Forest") }
        giveActivationMana(driver, player)

        canActivate(driver, player, uchbenbak) shouldBe false
    }

    test("Descend 8 counts only permanent cards, not instants/sorceries") {
        val driver = newDriver()
        val player = driver.player1

        val uchbenbak = driver.putCardInGraveyard(player, "Uchbenbak, the Great Mistake")
        // Seven instants: eight cards total, but only one permanent card (Uchbenbak itself).
        repeat(7) { driver.putCardInGraveyard(player, "Lightning Bolt") }
        giveActivationMana(driver, player)

        canActivate(driver, player, uchbenbak) shouldBe false
    }

    test("Descend 8 ability is sorcery-speed only: not offered outside a main phase") {
        val driver = newDriver()
        val player = driver.player1

        val uchbenbak = driver.putCardInGraveyard(player, "Uchbenbak, the Great Mistake")
        // Eight permanent cards — the graveyard gate is satisfied; only timing forbids it.
        repeat(7) { driver.putCardInGraveyard(player, "Forest") }

        // The engine skips DECLARE_ATTACKERS entirely without a valid attacker — add a decoy.
        val decoy = driver.putCreatureOnBattlefield(player, "Grizzly Bears")
        driver.removeSummoningSickness(decoy)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        giveActivationMana(driver, player)

        canActivate(driver, player, uchbenbak) shouldBe false
    }

    test("Descend 8 ability returns Uchbenbak to the battlefield with a finality counter") {
        val driver = newDriver()
        val player = driver.player1

        val uchbenbak = driver.putCardInGraveyard(player, "Uchbenbak, the Great Mistake")
        // Seven more permanent (land) cards → eight permanent cards total.
        repeat(7) { driver.putCardInGraveyard(player, "Forest") }
        giveActivationMana(driver, player)

        canActivate(driver, player, uchbenbak) shouldBe true

        driver.submit(
            ActivateAbility(playerId = player, sourceId = uchbenbak, abilityId = abilityId)
        ).isSuccess shouldBe true
        driver.bothPass()
        driver.isPaused shouldBe false

        // Uchbenbak is back on the battlefield and out of the graveyard.
        driver.state.getZone(ZoneKey(player, Zone.BATTLEFIELD)).contains(uchbenbak) shouldBe true
        driver.state.getZone(ZoneKey(player, Zone.GRAVEYARD)).contains(uchbenbak) shouldBe false

        // It entered with a finality counter.
        val counters = driver.state.getEntity(uchbenbak)?.get<CountersComponent>()?.counters ?: emptyMap()
        counters[CounterType.FINALITY] shouldBe 1
    }
})
