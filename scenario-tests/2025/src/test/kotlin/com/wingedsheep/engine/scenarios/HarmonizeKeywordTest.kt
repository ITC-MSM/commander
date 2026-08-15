package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.legalactions.EnumerationMode
import com.wingedsheep.engine.legalactions.LegalActionEnumerator
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.tdm.cards.ChanneledDragonfire
import com.wingedsheep.mtg.sets.definitions.tdm.cards.MammothBellow
import com.wingedsheep.mtg.sets.definitions.tdm.cards.UnendingWhisper
import com.wingedsheep.mtg.sets.definitions.tdm.cards.WildRide
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.AlternativePaymentChoice
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Harmonize (Tarkir: Dragonstorm): an alternative cost to cast an instant/sorcery from
 * your graveyard. As you cast it, you may tap a single creature you control to reduce
 * the (generic portion of the) harmonize cost by that creature's power. The card is then
 * exiled as it resolves.
 *
 * Cards under test:
 *   Unending Whisper  — {U} Sorcery, "Draw a card." Harmonize {5}{U}
 *   Channeled Dragonfire — {R} Sorcery, "deals 2 damage to any target." Harmonize {5}{R}{R}
 */
class HarmonizeKeywordTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(UnendingWhisper)
        driver.registerCard(ChanneledDragonfire)
        driver.registerCard(MammothBellow)
        driver.registerCard(WildRide)
        driver.initMirrorMatch(deck = Deck.of("Grizzly Bears" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    test("cast from graveyard for the full harmonize cost: draws and exiles the spell") {
        val driver = createDriver()
        val player = driver.activePlayer!!

        val spell = driver.putCardInGraveyard(player, "Unending Whisper")
        driver.giveMana(player, Color.BLUE, 6) // {5}{U}

        val handBefore = driver.state.getZone(ZoneKey(player, Zone.HAND)).size

        driver.submit(
            CastSpell(player, spell, useAlternativeCost = true, paymentStrategy = PaymentStrategy.FromPool)
        ).isSuccess shouldBe true
        driver.bothPass()

        // Drew a card and the spell was exiled (not returned to the graveyard).
        driver.state.getZone(ZoneKey(player, Zone.HAND)).size shouldBe handBefore + 1
        driver.state.getZone(ZoneKey(player, Zone.EXILE)).contains(spell) shouldBe true
        driver.state.getZone(ZoneKey(player, Zone.GRAVEYARD)).contains(spell) shouldBe false
    }

    test("tapping a creature reduces the generic harmonize cost by its power") {
        val driver = createDriver()
        val player = driver.activePlayer!!

        val spell = driver.putCardInGraveyard(player, "Unending Whisper")
        val bears = driver.putCreatureOnBattlefield(player, "Grizzly Bears") // 2/2
        // {5}{U} reduced by the bear's power (2) → {3}{U} = 4 mana.
        driver.giveMana(player, Color.BLUE, 4)

        driver.submit(
            CastSpell(
                player, spell,
                useAlternativeCost = true,
                paymentStrategy = PaymentStrategy.FromPool,
                alternativePayment = AlternativePaymentChoice(harmonizeCreature = bears)
            )
        ).isSuccess shouldBe true
        driver.bothPass()

        driver.isTapped(bears) shouldBe true
        driver.state.getZone(ZoneKey(player, Zone.EXILE)).contains(spell) shouldBe true
    }

    test("not enough mana to pay the full harmonize cost without tapping a creature is rejected") {
        val driver = createDriver()
        val player = driver.activePlayer!!

        val spell = driver.putCardInGraveyard(player, "Unending Whisper")
        driver.giveMana(player, Color.BLUE, 4) // short of {5}{U} = 6, and no creature tapped

        driver.submitExpectFailure(
            CastSpell(player, spell, useAlternativeCost = true, paymentStrategy = PaymentStrategy.FromPool)
        )
        // The spell stayed in the graveyard.
        driver.state.getZone(ZoneKey(player, Zone.GRAVEYARD)).contains(spell) shouldBe true
    }

    test("enumeration surfaces a CastWithHarmonize action carrying tappable creatures") {
        val driver = createDriver()
        val player = driver.activePlayer!!

        val spell = driver.putCardInGraveyard(player, "Unending Whisper")
        val bears = driver.putCreatureOnBattlefield(player, "Grizzly Bears") // 2/2
        // Only 4 Islands on the battlefield: {5}{U} (6) is unaffordable without the
        // reduction, but tapping the 2-power bear makes {3}{U} (4) affordable.
        repeat(4) { driver.putLandOnBattlefield(player, "Island") }

        val enumerator = LegalActionEnumerator.create(driver.cardRegistry)
        val actions = enumerator.enumerate(driver.state, player, EnumerationMode.FULL)

        val harmonizeAction = actions.firstOrNull { la ->
            la.actionType == "CastWithHarmonize" && (la.action as? CastSpell)?.cardId == spell
        }
        harmonizeAction shouldNotBe null
        harmonizeAction!!.affordable shouldBe true
        harmonizeAction.hasHarmonize shouldBe true
        val candidate = harmonizeAction.harmonizeCreatures?.firstOrNull { it.entityId == bears }
        candidate shouldNotBe null
        candidate!!.power shouldBe 2
    }

    test("harmonize cast routes targets and exiles: Channeled Dragonfire deals 2 to a player") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val opponent = driver.state.turnOrder.first { it != player }

        val spell = driver.putCardInGraveyard(player, "Channeled Dragonfire")
        driver.giveMana(player, Color.RED, 7) // full {5}{R}{R}

        driver.submit(
            CastSpell(
                player, spell,
                targets = listOf(ChosenTarget.Player(opponent)),
                useAlternativeCost = true,
                paymentStrategy = PaymentStrategy.FromPool
            )
        ).isSuccess shouldBe true
        driver.bothPass()

        driver.getLifeTotal(opponent) shouldBe 18
        driver.state.getZone(ZoneKey(player, Zone.EXILE)).contains(spell) shouldBe true
    }

    test("Wild Ride harmonize cast pumps a target creature and exiles the spell") {
        val driver = createDriver()
        val player = driver.activePlayer!!

        val spell = driver.putCardInGraveyard(player, "Wild Ride")
        val bears = driver.putCreatureOnBattlefield(player, "Grizzly Bears") // 2/2 target for the pump
        driver.giveMana(player, Color.RED, 5) // full {4}{R}

        driver.submit(
            CastSpell(
                player, spell,
                targets = listOf(ChosenTarget.Permanent(bears)),
                useAlternativeCost = true,
                paymentStrategy = PaymentStrategy.FromPool
            )
        ).isSuccess shouldBe true
        driver.bothPass()

        // +3/+0 and haste applied; the spell is exiled (harmonize), not back in the graveyard.
        driver.state.projectedState.getPower(bears) shouldBe 5
        driver.state.projectedState.getToughness(bears) shouldBe 2
        driver.state.projectedState.hasKeyword(bears, com.wingedsheep.sdk.core.Keyword.HASTE) shouldBe true
        driver.state.getZone(ZoneKey(player, Zone.EXILE)).contains(spell) shouldBe true
        driver.state.getZone(ZoneKey(player, Zone.GRAVEYARD)).contains(spell) shouldBe false
    }

    test("a harmonize card cast normally from hand still goes to the graveyard") {
        val driver = createDriver()
        val player = driver.activePlayer!!

        val spell = driver.putCardInHand(player, "Unending Whisper")
        driver.giveMana(player, Color.BLUE, 1) // hard cast for {U}

        driver.submit(
            CastSpell(player, spell, paymentStrategy = PaymentStrategy.FromPool)
        ).isSuccess shouldBe true
        driver.bothPass()

        // Cast from hand (not graveyard) — Harmonize's exile clause does not apply.
        driver.state.getZone(ZoneKey(player, Zone.GRAVEYARD)).contains(spell) shouldBe true
        driver.state.getZone(ZoneKey(player, Zone.EXILE)).contains(spell) shouldBe false
    }
})
