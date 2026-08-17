package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ChooseColorDecision
import com.wingedsheep.engine.core.ColorChosenResponse
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.core.ReorderLibraryDecision
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mkm.cards.Cryptex
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Cryptex (MKM #251) — {2} artifact.
 *
 * "{T}, Collect evidence 3: Add one mana of any color. Put an unlock counter on this artifact."
 * "Sacrifice this artifact: Surveil 3, then draw three cards. Activate only if this artifact has
 *  five or more unlock counters on it."
 *
 * Three claims, each with a plausible wrong implementation:
 *
 *  - the first ability is a **mana ability** (the printed ruling says so), so it must resolve
 *    immediately without going on the stack — and its non-mana rider has to survive that path,
 *    which a naive `isManaAbility` executor that only knows how to add mana would drop;
 *  - collect evidence is a genuine *cost*, so CR 701.59b makes the whole ability unactivatable with
 *    a graveyard that can't reach total mana value 3 — not activatable-then-fizzling;
 *  - the sacrifice ability's gate reads counters **live**, so it is illegal at four and legal at
 *    five. Testing only the legal side would pass against no gate at all.
 */
class CryptexScenarioTest : FunSpec({

    val tapAndUnlock = Cryptex.activatedAbilities[0].id
    val crack = Cryptex.activatedAbilities[1].id

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.unlockCounters(id: EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.UNLOCK) ?: 0

    test("the mana ability adds a chosen colour and an unlock counter, off the stack") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val cryptex = driver.putPermanentOnBattlefield(player, "Cryptex")
        // Centaur Courser is mana value 3 — exactly the collect-evidence threshold on its own.
        driver.putCardInGraveyard(player, "Centaur Courser")

        val result = driver.submit(
            ActivateAbility(
                playerId = player,
                sourceId = cryptex,
                abilityId = tapAndUnlock,
                manaColorChoice = Color.GREEN,
                paymentStrategy = PaymentStrategy.AutoPay,
            )
        )
        result.error shouldBe null
        (driver.pendingDecision as? ChooseColorDecision)?.let {
            driver.submitDecision(player, ColorChosenResponse(it.id, Color.GREEN))
        }

        withClue("a mana ability never uses the stack (CR 605.3a)") {
            driver.stackSize shouldBe 0
        }
        withClue("the mana half ran") {
            driver.state.getEntity(player)?.get<ManaPoolComponent>()?.green shouldBe 1
        }
        withClue("the rider ran too — the counter isn't dropped by the mana-ability path") {
            driver.unlockCounters(cryptex) shouldBe 1
        }
        withClue("the cost was paid: tapped, and the evidence exiled") {
            driver.isTapped(cryptex) shouldBe true
            driver.getExileCardNames(player) shouldBe listOf("Centaur Courser")
        }
    }

    test("an empty graveyard makes the ability unactivatable, not merely ineffective") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val cryptex = driver.putPermanentOnBattlefield(player, "Cryptex")

        val result = driver.submit(
            ActivateAbility(
                playerId = player,
                sourceId = cryptex,
                abilityId = tapAndUnlock,
                manaColorChoice = Color.GREEN,
                paymentStrategy = PaymentStrategy.AutoPay,
            )
        )

        withClue("CR 701.59b: you can't choose to collect evidence you can't pay for") {
            (result.error != null) shouldBe true
            driver.isTapped(cryptex) shouldBe false
            driver.unlockCounters(cryptex) shouldBe 0
        }
    }

    test("the sacrifice ability is gated at five unlock counters") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val cryptex = driver.putPermanentOnBattlefield(player, "Cryptex")

        driver.addComponent(cryptex, CountersComponent(mapOf(CounterType.UNLOCK to 4)))
        val tooEarly = driver.submit(
            ActivateAbility(playerId = player, sourceId = cryptex, abilityId = crack)
        )
        withClue("four counters is not five") {
            (tooEarly.error != null) shouldBe true
            driver.findPermanent(player, "Cryptex") shouldBe cryptex
        }

        driver.addComponent(cryptex, CountersComponent(mapOf(CounterType.UNLOCK to 5)))
        val handBefore = driver.getHandSize(player)
        driver.submit(
            ActivateAbility(playerId = player, sourceId = cryptex, abilityId = crack)
        ).error shouldBe null
        // Unlike the first ability this one uses the stack, so it has to resolve before the
        // surveil prompt appears. Keeping every surveiled card on top means the three cards drawn
        // are exactly the three that were looked at.
        driver.bothPass()
        var guard = 0
        while (driver.isPaused && guard++ < 6) {
            when (val decision = driver.pendingDecision) {
                is SelectCardsDecision -> driver.submitCardSelection(player, emptyList())
                is ReorderLibraryDecision -> driver.submitOrderedResponse(player, decision.cards)
                else -> break
            }
        }

        withClue("the fifth counter opened it: sacrificed, and three cards drawn") {
            driver.findPermanent(player, "Cryptex") shouldBe null
            driver.getHandSize(player) shouldBe handBefore + 3
        }
    }
})
