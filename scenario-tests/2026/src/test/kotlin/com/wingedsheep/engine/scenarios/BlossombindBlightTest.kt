package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.legalactions.LegalActionEnumerator
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.ecl.cards.Blossombind
import com.wingedsheep.mtg.sets.definitions.ecl.cards.BoneclubBerserker
import com.wingedsheep.mtg.sets.definitions.ecl.cards.BurningCuriosity
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Regression for: a creature enchanted with Blossombind ("can't have counters put on it")
 * still received a -1/-1 counter when its controller paid a Blight cost (Burning Curiosity).
 *
 * CR 614.17b — because putting a counter on that creature is an event that can't happen, the
 * player can't even choose to pay a cost that includes it. So such a creature must be excluded
 * from the legal blight-target pool and the cast must be rejected, rather than the counter being
 * silently dropped while the cost still "succeeds".
 */
class BlossombindBlightTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCards(listOf(Blossombind, BoneclubBerserker, BurningCuriosity))
        return driver
    }

    /** Resolve everything on the stack (the aura, then its enters-the-battlefield tap trigger). */
    fun GameTestDriver.resolveStack() {
        var safety = 0
        while (stackSize > 0 && !isPaused && safety < 20) {
            bothPass()
            safety++
        }
    }

    test("Blossombind-enchanted creature is excluded from the blight-target pool") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 30, "Island" to 10))
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val boneclub = driver.putCreatureOnBattlefield(player, "Boneclub Berserker")
        val grizzly = driver.putCreatureOnBattlefield(player, "Grizzly Bears")
        repeat(4) { driver.putLandOnBattlefield(player, "Mountain") }
        repeat(2) { driver.putLandOnBattlefield(player, "Island") }

        // Enchant the Boneclub Berserker with Blossombind.
        val blossombind = driver.putCardInHand(player, "Blossombind")
        driver.castSpell(player, blossombind, listOf(boneclub))
        driver.resolveStack()
        driver.findPermanent(player, "Blossombind") shouldNotBe null

        val burningCuriosity = driver.putCardInHand(player, "Burning Curiosity")
        val enumerator = LegalActionEnumerator.create(driver.cardRegistry)
        val blightAction = enumerator.enumerate(driver.state, player).firstOrNull { action ->
            action.actionType == "CastSpell" &&
                (action.action as? CastSpell)?.cardId == burningCuriosity &&
                action.additionalCostInfo?.costType == "Blight"
        }
        blightAction shouldNotBe null
        val validTargets = blightAction!!.additionalCostInfo!!.validBlightTargets
        // The unenchanted creature is a legal blight target; the enchanted one is not.
        validTargets shouldContain grizzly
        validTargets shouldNotContain boneclub
    }

    test("casting Burning Curiosity blighting the enchanted creature is rejected, no counter placed") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 30, "Island" to 10))
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val boneclub = driver.putCreatureOnBattlefield(player, "Boneclub Berserker")
        repeat(4) { driver.putLandOnBattlefield(player, "Mountain") }
        repeat(2) { driver.putLandOnBattlefield(player, "Island") }

        val blossombind = driver.putCardInHand(player, "Blossombind")
        driver.castSpell(player, blossombind, listOf(boneclub))
        driver.resolveStack()

        val burningCuriosity = driver.putCardInHand(player, "Burning Curiosity")
        driver.submitExpectFailure(
            CastSpell(
                playerId = player,
                cardId = burningCuriosity,
                paymentStrategy = PaymentStrategy.AutoPay,
                additionalCostPayment = AdditionalCostPayment(blightTargets = listOf(boneclub))
            )
        )

        val counters = driver.state.getEntity(boneclub)?.get<CountersComponent>()
        (counters?.getCount(CounterType.MINUS_ONE_MINUS_ONE) ?: 0) shouldBe 0
    }

    test("blighting an unenchanted creature still places the -1/-1 counter") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 30, "Island" to 10))
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val grizzly = driver.putCreatureOnBattlefield(player, "Grizzly Bears")
        repeat(3) { driver.putLandOnBattlefield(player, "Mountain") }

        val burningCuriosity = driver.putCardInHand(player, "Burning Curiosity")
        driver.submitSuccess(
            CastSpell(
                playerId = player,
                cardId = burningCuriosity,
                paymentStrategy = PaymentStrategy.AutoPay,
                additionalCostPayment = AdditionalCostPayment(blightTargets = listOf(grizzly))
            )
        )

        // The blight counter is applied as the spell is cast (cost payment), before resolution.
        val counters = driver.state.getEntity(grizzly)?.get<CountersComponent>()
        counters?.getCount(CounterType.MINUS_ONE_MINUS_ONE) shouldBe 1
    }
})
