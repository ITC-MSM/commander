package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.handlers.effects.FaceDownTurnUp
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.identity.FaceDownComponent
import com.wingedsheep.engine.state.components.identity.FaceDownModeComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mkm.cards.VannifarEvolvedEnigma
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.effects.FaceDownMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Vannifar, Evolved Enigma (MKM #241) — {2}{G}{U} 3/4.
 *
 * "At the beginning of combat on your turn, choose one —
 *  • Cloak a card from your hand.
 *  • Put a +1/+1 counter on each colorless creature you control."
 *
 * The two modes are one engine and the test proves the link the card never states: a cloaked
 * permanent is *colorless* (CR 708.2), so the second mode grows exactly what the first mode makes.
 * That is why the counter mode is asserted against a cloaked green bear rather than an obviously
 * colorless artifact — if the filter read the card's printed colors instead of projected ones, a
 * green card cloaked face down would be skipped and the card's whole loop would quietly not work.
 *
 * The cloak mode is a hand-side pipeline (gather hand → select one → move face down with
 * [FaceDownMode.CLOAK]); this is the first card in the corpus to cloak from hand rather than from
 * the library, so the selection decision and the face-down mode are both asserted here.
 */
class VannifarEvolvedEnigmaScenarioTest : FunSpec({

    val greenBear = card("Vannifar Test Bear") {
        manaCost = "{1}{G}"
        typeLine = "Creature — Bear"
        power = 2
        toughness = 2
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(VannifarEvolvedEnigma, greenBear))
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        return driver
    }

    // Player 1 may not be active at game start (random turn order) — advance until it is.
    fun GameTestDriver.advanceToPlayer1BeginCombat() {
        passPriorityUntil(Step.BEGIN_COMBAT)
        var safety = 0
        while (activePlayer != player1 && safety < 50) {
            bothPass()
            passPriorityUntil(Step.BEGIN_COMBAT)
            safety++
        }
    }

    fun GameTestDriver.faceDownPermanents(playerId: EntityId) =
        getPermanents(playerId).filter { state.getEntity(it)?.has<FaceDownComponent>() == true }

    /** Put [cardName] onto the battlefield already cloaked, the way a real cloak entry leaves it. */
    fun GameTestDriver.cloak(playerId: EntityId, cardName: String): EntityId {
        val id = putPermanentOnBattlefield(playerId, cardName)
        val cardDef = cardRegistry.requireCard(cardName)
        replaceState(
            state.updateEntity(id) { container ->
                var c = container.with(FaceDownComponent)
                    .with(FaceDownModeComponent(FaceDownMode.CLOAK))
                FaceDownTurnUp.dataFor(cardDef, cardName, FaceDownMode.CLOAK)?.let { c = c.with(it) }
                c
            }
        )
        return id
    }

    fun GameTestDriver.plusOneCounters(entityId: EntityId): Int =
        state.getEntity(entityId)?.get<CountersComponent>()
            ?.counters?.get(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    test("the trigger offers exactly the two printed modes") {
        val driver = createDriver()
        driver.putCreatureOnBattlefield(driver.player1, "Vannifar, Evolved Enigma")
        driver.advanceToPlayer1BeginCombat()
        driver.bothPass()

        val modeDecision = driver.pendingDecision as ChooseOptionDecision
        modeDecision.options shouldBe listOf(
            "Cloak a card from your hand",
            "Put a +1/+1 counter on each colorless creature you control",
        )
    }

    test("mode 1 cloaks the chosen card from hand as a 2/2 with ward") {
        val driver = createDriver()
        driver.putCreatureOnBattlefield(driver.player1, "Vannifar, Evolved Enigma")
        val inHand = driver.putCardInHand(driver.player1, "Vannifar Test Bear")
        driver.advanceToPlayer1BeginCombat()
        driver.bothPass()

        val modeDecision = driver.pendingDecision as ChooseOptionDecision
        driver.submitDecision(driver.player1, OptionChosenResponse(modeDecision.id, 0))
        driver.bothPass() // the mode is picked as the trigger goes on the stack; now resolve it
        driver.submitCardSelection(driver.player1, listOf(inHand))

        driver.faceDownPermanents(driver.player1) shouldBe listOf(inHand)
        driver.state.getEntity(inHand)?.get<FaceDownModeComponent>()?.mode shouldBe FaceDownMode.CLOAK
        driver.state.projectedState.getPower(inHand) shouldBe 2
        driver.state.projectedState.getToughness(inHand) shouldBe 2
        driver.state.projectedState.hasKeyword(inHand, Keyword.WARD) shouldBe true
        driver.state.getHand(driver.player1).contains(inHand) shouldBe false
    }

    test("mode 2 counters a cloaked green card — face-down means colorless, not printed-colorless") {
        val driver = createDriver()
        val vannifar = driver.putCreatureOnBattlefield(driver.player1, "Vannifar, Evolved Enigma")
        // Cloak the green bear directly: this test is about the counter mode's filter, and the
        // cloak-from-hand path has its own test above.
        val bear = driver.cloak(driver.player1, "Vannifar Test Bear")

        driver.advanceToPlayer1BeginCombat()
        driver.bothPass()
        val counterMode = driver.pendingDecision as ChooseOptionDecision
        driver.submitDecision(driver.player1, OptionChosenResponse(counterMode.id, 1))
        driver.bothPass()

        // The face-down bear is colorless (CR 708.2) and gets the counter…
        driver.plusOneCounters(bear) shouldBe 1
        driver.state.projectedState.getPower(bear) shouldBe 3
        driver.state.projectedState.getToughness(bear) shouldBe 3
        // …while Vannifar herself, green-blue and face up, does not.
        driver.plusOneCounters(vannifar) shouldBe 0
    }

    test("mode 1 with an empty hand is a legal choice that simply does nothing") {
        val driver = createDriver()
        driver.putCreatureOnBattlefield(driver.player1, "Vannifar, Evolved Enigma")
        driver.replaceState(
            driver.state.copy(
                zones = driver.state.zones + (ZoneKey(driver.player1, Zone.HAND) to emptyList()),
            )
        )
        driver.advanceToPlayer1BeginCombat()
        driver.bothPass()

        val modeDecision = driver.pendingDecision as ChooseOptionDecision
        driver.submitDecision(driver.player1, OptionChosenResponse(modeDecision.id, 0))
        driver.bothPass()

        driver.faceDownPermanents(driver.player1) shouldBe emptyList()
    }
})
