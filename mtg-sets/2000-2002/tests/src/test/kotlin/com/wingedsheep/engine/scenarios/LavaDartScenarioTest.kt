package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.AlternativeCostType
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.jud.cards.LavaDart
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Lava Dart {R} — Instant.
 * "Lava Dart deals 1 damage to any target.
 *  Flashback—Sacrifice a Mountain."
 *
 * Covers the plain damage spell and the no-mana flashback whose only cost is
 * sacrificing a Mountain (`KeywordAbility.flashback("", SacrificePermanent(Mountain))`).
 */
class LavaDartScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(LavaDart))
        return driver
    }

    test("cast from hand deals 1 damage to a player") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true, startingLife = 20)
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val dart = driver.putCardInHand(me, "Lava Dart")
        driver.putLandOnBattlefield(me, "Mountain")

        driver.submit(
            CastSpell(
                playerId = me,
                cardId = dart,
                targets = listOf(ChosenTarget.Player(opp)),
                paymentStrategy = PaymentStrategy.AutoPay
            )
        ).isSuccess shouldBe true
        driver.bothPass()

        driver.getLifeTotal(opp) shouldBe 19
    }

    test("flashback sacrifices a Mountain, deals 1 damage, and exiles Lava Dart") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true, startingLife = 20)
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val dart = driver.putCardInGraveyard(me, "Lava Dart")
        val mountain = driver.putLandOnBattlefield(me, "Mountain")

        driver.submit(
            CastSpell(
                playerId = me,
                cardId = dart,
                targets = listOf(ChosenTarget.Player(opp)),
                useAlternativeCost = true,
                alternativeCostType = AlternativeCostType.FLASHBACK,
                additionalCostPayment = AdditionalCostPayment(sacrificedPermanents = listOf(mountain)),
                paymentStrategy = PaymentStrategy.AutoPay
            )
        ).isSuccess shouldBe true
        driver.bothPass()

        // Damage dealt.
        driver.getLifeTotal(opp) shouldBe 19
        // The Mountain was sacrificed to pay flashback's additional cost.
        driver.state.getBattlefield().contains(mountain) shouldBe false
        // Flashback exiles Lava Dart from the graveyard.
        driver.state.getZone(ZoneKey(me, Zone.EXILE)).contains(dart) shouldBe true
        driver.state.getZone(ZoneKey(me, Zone.GRAVEYARD)).contains(dart) shouldBe false
    }
})
