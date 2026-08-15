package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.ltr.cards.NastyEnd
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Supertype
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario coverage for Gap 17 (`SacrificedPermanentWasLegendary`) via Nasty End:
 *
 *  - Sacrificing a legendary creature ⇒ draw 3.
 *  - Sacrificing a nonlegendary creature ⇒ draw 2.
 *
 * The legendary status is read from the `EntitySnapshot.supertypes` set captured by
 * `captureEntitySnapshots` at cost-payment time — the supertype field is freshly added
 * for Gap 17 (previous snapshots only carried subtypes). The condition data-only path runs
 * inside `ConditionalEffect`'s `Gate.WhenCondition`, lowered from the SDK facade
 * `Conditions.SacrificedWasLegendary`.
 */
class NastyEndTest : FunSpec({

    // A bare legendary 1/1 used as cost sacrifice.
    val LegendaryFodder = CardDefinition.creature(
        name = "Legendary Fodder",
        manaCost = com.wingedsheep.sdk.core.ManaCost.parse("{1}"),
        subtypes = setOf(Subtype.HUMAN),
        power = 1,
        toughness = 1,
        supertypes = setOf(Supertype.LEGENDARY),
    )

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(NastyEnd, LegendaryFodder))
        return driver
    }

    test("Nasty End: sacrificing a nonlegendary creature draws two") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true)
        val active = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val fodder = driver.putCreatureOnBattlefield(active, "Grizzly Bears")
        val nasty = driver.putCardInHand(active, "Nasty End")
        driver.giveMana(active, Color.BLACK, 1)
        driver.giveColorlessMana(active, 1)

        val handBefore = driver.getHandSize(active)
        val result = driver.submit(
            CastSpell(
                playerId = active,
                cardId = nasty,
                paymentStrategy = PaymentStrategy.AutoPay,
                additionalCostPayment = AdditionalCostPayment(sacrificedPermanents = listOf(fodder))
            )
        )
        result.isSuccess shouldBe true
        driver.bothPass()
        while (driver.state.stack.isNotEmpty()) driver.bothPass()

        // -1 for casting Nasty End out of hand, +2 for the draw.
        driver.getHandSize(active) shouldBe (handBefore - 1 + 2)
        driver.findPermanent(active, "Grizzly Bears") shouldBe null
    }

    test("Nasty End: sacrificing a legendary creature draws three instead") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true)
        val active = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val fodder = driver.putCreatureOnBattlefield(active, "Legendary Fodder")
        val nasty = driver.putCardInHand(active, "Nasty End")
        driver.giveMana(active, Color.BLACK, 1)
        driver.giveColorlessMana(active, 1)

        val handBefore = driver.getHandSize(active)
        val result = driver.submit(
            CastSpell(
                playerId = active,
                cardId = nasty,
                paymentStrategy = PaymentStrategy.AutoPay,
                additionalCostPayment = AdditionalCostPayment(sacrificedPermanents = listOf(fodder))
            )
        )
        result.isSuccess shouldBe true
        driver.bothPass()
        while (driver.state.stack.isNotEmpty()) driver.bothPass()

        // -1 for casting Nasty End out of hand, +3 for the conditional draw.
        driver.getHandSize(active) shouldBe (handBefore - 1 + 3)
        driver.findPermanent(active, "Legendary Fodder") shouldBe null
    }
})
