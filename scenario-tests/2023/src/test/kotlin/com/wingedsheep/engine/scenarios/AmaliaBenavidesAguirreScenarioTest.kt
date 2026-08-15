package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.AmaliaBenavidesAguirre
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Amalia Benavides Aguirre (LCI #221) — {W}{B} Legendary Creature — Vampire Scout, 2/2.
 *
 * "Ward—Pay 3 life.
 *  Whenever you gain life, Amalia Benavides Aguirre explores. Then destroy all other
 *  creatures if its power is exactly 20."
 *
 * Tests cover:
 *  - Gaining life triggers an explore (land path: land → hand, no counter).
 *  - Gaining life triggers an explore (nonland path: +1/+1 counter added, card may
 *    go to graveyard).
 *  - Power == 20 after explore → destroy all other creatures, Amalia survives.
 *  - Power < 20 after explore → no wrath.
 */
class AmaliaBenavidesAguirreScenarioTest : FunSpec({

    // A {W} instant that gains 3 life, used to fire the YouGainLife trigger.
    val TestLifeGain = CardDefinition.instant(
        name = "Test Life Gain",
        manaCost = ManaCost.parse("{W}"),
        oracleText = "You gain 3 life.",
        script = CardScript.spell(effect = Effects.GainLife(3))
    )

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(AmaliaBenavidesAguirre)
        driver.registerCard(TestLifeGain)
        return driver
    }

    /** Helper: +1/+1 counter count on a permanent by ID. */
    fun plusOneCounters(driver: GameTestDriver, id: EntityId): Int =
        driver.state.getEntity(id)?.get<CountersComponent>()
            ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    // -------------------------------------------------------------------------
    // Test 1: explore (land path)
    // -------------------------------------------------------------------------
    test("life gain triggers explore: land goes to hand, no +1/+1 counter added") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 30))
        val controller = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val amalia = driver.putCreatureOnBattlefield(controller, "Amalia Benavides Aguirre")
        // Seed a land on top so explore takes the land-to-hand branch.
        driver.putCardOnTopOfLibrary(controller, "Forest")

        val healer = driver.putCardInHand(controller, "Test Life Gain")
        driver.giveMana(controller, Color.WHITE, 1)
        driver.castSpell(controller, healer)

        driver.bothPass() // resolve Test Life Gain → gains 3 life → trigger on stack
        driver.bothPass() // resolve YouGainLife trigger: explore (Forest → hand), check power (2 ≠ 20)

        // Forest went to hand; no counter on Amalia.
        driver.findCardInHand(controller, "Forest").shouldNotBeNull()
        plusOneCounters(driver, amalia) shouldBe 0
        // Amalia is unharmed and still on the battlefield.
        driver.findPermanent(controller, "Amalia Benavides Aguirre").shouldNotBeNull()
    }

    // -------------------------------------------------------------------------
    // Test 2: explore (nonland path)
    // -------------------------------------------------------------------------
    test("life gain triggers explore: nonland puts +1/+1 counter on Amalia") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 30))
        val controller = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val amalia = driver.putCreatureOnBattlefield(controller, "Amalia Benavides Aguirre")
        // Seed a nonland on top: explore will put a +1/+1 counter on Amalia and pause
        // for the "may put in graveyard" decision.
        driver.putCardOnTopOfLibrary(controller, "Grizzly Bears")

        val healer = driver.putCardInHand(controller, "Test Life Gain")
        driver.giveMana(controller, Color.WHITE, 1)
        driver.castSpell(controller, healer)

        driver.bothPass() // resolve Test Life Gain → gains 3 life → trigger on stack
        driver.bothPass() // resolve YouGainLife trigger: explore reveals nonland →
                          // +1/+1 counter added → pauses for graveyard decision

        // Explore revealed a nonland so there must be a pending decision: the
        // "may put the revealed card into your graveyard" collection-selection choice.
        val decision = driver.pendingDecision
        decision.shouldNotBeNull()
        // The bears are still in the library zone during the decision.
        val bearsId = driver.state.getZone(ZoneKey(controller, Zone.LIBRARY)).first()
        // Choose to put the revealed card into the graveyard. Submit a CardsSelectedResponse
        // by the decision's own id (matching ScenarioTestBase.selectCards) rather than the
        // hard-cast submitCardSelection helper, which requires a SelectCardsDecision.
        driver.submitDecision(controller, CardsSelectedResponse(decision.id, listOf(bearsId)))
        // Continuation resumes: ConditionalEffect runs → power (3) ≠ 20 → no wrath.

        plusOneCounters(driver, amalia) shouldBe 1
        // Amalia is still on the battlefield.
        driver.findPermanent(controller, "Amalia Benavides Aguirre").shouldNotBeNull()
    }

    // -------------------------------------------------------------------------
    // Test 3: power == 20 after explore → destroy all other creatures
    // -------------------------------------------------------------------------
    test("power exactly 20 after explore destroys all other creatures; Amalia survives") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 30))
        val controller = driver.activePlayer!!
        val opponent = driver.getOpponent(controller)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Place Amalia on the battlefield and seed 18 +1/+1 counters so her power = 2 + 18 = 20.
        val amalia = driver.putCreatureOnBattlefield(controller, "Amalia Benavides Aguirre")
        driver.addComponent(amalia, CountersComponent(mapOf(CounterType.PLUS_ONE_PLUS_ONE to 18)))

        // Place a creature that should be destroyed.
        driver.putCreatureOnBattlefield(controller, "Grizzly Bears")
        // Opponent's creature should also be destroyed (it's a creature, not Amalia).
        driver.putCreatureOnBattlefield(opponent, "Centaur Courser")

        // Put a land on top of the library: explore will move it to hand (no counter added),
        // so Amalia's power stays exactly 20 after explore.
        driver.putCardOnTopOfLibrary(controller, "Forest")

        val healer = driver.putCardInHand(controller, "Test Life Gain")
        driver.giveMana(controller, Color.WHITE, 1)
        driver.castSpell(controller, healer)

        driver.bothPass() // resolve Test Life Gain → gains 3 life → trigger on stack
        driver.bothPass() // resolve YouGainLife trigger:
                          //   explore: Forest → hand (no counter, power stays 20)
                          //   ConditionalEffect: power == 20 → destroyAll(other creatures)

        // Amalia is still on the battlefield.
        driver.findPermanent(controller, "Amalia Benavides Aguirre").shouldNotBeNull()
        // All other creatures are destroyed.
        driver.findPermanent(controller, "Grizzly Bears") shouldBe null
        driver.findPermanent(opponent, "Centaur Courser") shouldBe null
        // Amalia still has 18 +1/+1 counters (Forest → hand, no explore counter added).
        plusOneCounters(driver, amalia) shouldBe 18
    }

    // -------------------------------------------------------------------------
    // Test 4: power != 20 → no wrath
    // -------------------------------------------------------------------------
    test("power not 20 after explore does not destroy other creatures") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 30))
        val controller = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Amalia at base 2/2 — power will be 2 after explore (land path).
        driver.putCreatureOnBattlefield(controller, "Amalia Benavides Aguirre")
        driver.putCreatureOnBattlefield(controller, "Grizzly Bears")
        driver.putCardOnTopOfLibrary(controller, "Forest")

        val healer = driver.putCardInHand(controller, "Test Life Gain")
        driver.giveMana(controller, Color.WHITE, 1)
        driver.castSpell(controller, healer)

        driver.bothPass() // resolve Test Life Gain → gains 3 life → trigger on stack
        driver.bothPass() // resolve YouGainLife trigger: explore (land → hand), power 2 ≠ 20

        // Grizzly Bears should still be on the battlefield.
        driver.findPermanent(controller, "Grizzly Bears").shouldNotBeNull()
        driver.findPermanent(controller, "Amalia Benavides Aguirre").shouldNotBeNull()
    }
})
