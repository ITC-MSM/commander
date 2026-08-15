package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.legalactions.LegalActionEnumerator
import com.wingedsheep.engine.mechanics.mana.ManaSolver
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.inv.cards.IrrigationDitch
import com.wingedsheep.mtg.sets.definitions.inv.cards.NomadicElf
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Irrigation Ditch has a sacrifice-free mana ability *and* a sacrifice one:
 *   {T}: Add {W}
 *   {T}, Sacrifice this land: Add {G}{U}
 *
 * Sacrificing for {G}{U} is a legal way to pay — so a spell that needs green (Nomadic Elf,
 * {1}{G}) must be *shown as playable*. But, exactly like sacrificing a Treasure for mana, the
 * sacrifice has to be a deliberate player action: the auto-pay solver must never silently
 * sacrifice the land. The {W} path is sacrifice-free and stays available to auto-pay.
 */
class IrrigationDitchAutoTapTest : FunSpec({

    // Minimal {W} creature so we can prove the sacrifice-free white ability still auto-taps.
    val whiteBear = card("White Bear") {
        manaCost = "{W}"
        typeLine = "Creature — Bear"
        power = 1
        toughness = 1
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(IrrigationDitch, NomadicElf, whiteBear))
        return driver
    }

    test("Nomadic Elf ({1}{G}) is shown as playable via Irrigation Ditch's sacrifice ability") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(activePlayer, "Irrigation Ditch")
        driver.putPermanentOnBattlefield(activePlayer, "Island")
        driver.putCardInHand(activePlayer, "Nomadic Elf")

        // Affordable: the only green comes from "{T}, Sacrifice: Add {G}{U}", but the spell is
        // still a legal play (the player can opt into the sacrifice), so canPay reports true...
        val solver = ManaSolver(driver.cardRegistry)
        solver.canPay(driver.state, activePlayer, ManaCost.parse("{1}{G}")) shouldBe true

        // ...and it surfaces as a legal cast action for the player to choose.
        val actions = LegalActionEnumerator.create(driver.cardRegistry).enumerate(driver.state, activePlayer)
        val offered = actions.any { it.toString().contains("Nomadic Elf", ignoreCase = true) }
        offered shouldBe true
    }

    test("auto-pay refuses to sacrifice Irrigation Ditch to cast Nomadic Elf ({1}{G})") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val ditch = driver.putPermanentOnBattlefield(activePlayer, "Irrigation Ditch")
        driver.putPermanentOnBattlefield(activePlayer, "Island")
        val elf = driver.putCardInHand(activePlayer, "Nomadic Elf")

        // The only source of {G} is Irrigation Ditch's "{T}, Sacrifice: Add {G}{U}" ability.
        // Auto-pay must not pick it — there is no sacrifice-free way to make green, and the
        // sacrifice must be a deliberate choice.
        val result = driver.castSpell(activePlayer, elf)

        result.isSuccess shouldBe false
        // Irrigation Ditch was NOT sacrificed: still on the battlefield, untapped.
        driver.findPermanent(activePlayer, "Irrigation Ditch") shouldBe ditch
        driver.isTapped(ditch) shouldBe false
        driver.getGraveyardCardNames(activePlayer).contains("Irrigation Ditch") shouldBe false
    }

    test("auto-pay still uses Irrigation Ditch's sacrifice-free {W} ability") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val ditch = driver.putPermanentOnBattlefield(activePlayer, "Irrigation Ditch")
        val bear = driver.putCardInHand(activePlayer, "White Bear")

        // {W}: Irrigation Ditch's plain "{T}: Add {W}" ability covers it without sacrificing.
        val result = driver.castSpell(activePlayer, bear)

        result.isSuccess shouldBe true
        // Tapped for {W}, but still on the battlefield (not sacrificed).
        driver.isTapped(ditch) shouldBe true
        driver.findPermanent(activePlayer, "Irrigation Ditch") shouldBe ditch
        driver.getGraveyardCardNames(activePlayer).contains("Irrigation Ditch") shouldBe false
    }
})
