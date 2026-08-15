package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mkm.cards.BloodSpatterAnalysis
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Blood Spatter Analysis — the bloodstain fuse and the reflexive buy-back.
 *
 * Two things are worth pinning down, and neither is visible from the card text alone:
 *
 * 1. The batched death trigger advances the fuse by exactly one per death *batch*, and the
 *    threshold test lives inside that trigger's resolution — so the enchantment survives four
 *    deaths and sacrifices itself on the fifth.
 * 2. "When you do" is a real CR 603.12 reflexive ability, so the returned creature card is chosen
 *    *after* the sacrifice, as a second stack object. If it were wired as another inline step the
 *    target would have to be picked up front (and the card's ruling says explicitly that it isn't).
 *
 * Savannah Lions (2/1) are the bodies; Lightning Bolt supplies one death per cast, which is also
 * one death batch each.
 */
class BloodSpatterAnalysisScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(BloodSpatterAnalysis)
        return driver
    }

    fun GameTestDriver.bloodstains(id: EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.counters?.get(CounterType.BLOODSTAIN) ?: 0

    /** Bolt [victim] and let every resulting trigger drain. */
    fun GameTestDriver.boltAndSettle(caster: EntityId, victim: EntityId) {
        val bolt = putCardInHand(caster, "Lightning Bolt")
        giveMana(caster, Color.RED, 1)
        castSpell(caster, bolt, listOf(victim)).isSuccess shouldBe true
        var guard = 0
        while (!isPaused && state.stack.isNotEmpty() && guard++ < 20) bothPass()
    }

    test("the enters trigger shocks an opposing creature for 3") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 20, "Mountain" to 20), skipMulligans = true)
        val you = driver.activePlayer!!
        val opp = driver.getOpponent(you)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(opp, "Centaur Courser") // 3/3 — dies to exactly 3
        val courser = driver.findPermanent(opp, "Centaur Courser")!!

        val analysis = driver.putCardInHand(you, "Blood Spatter Analysis")
        driver.giveMana(you, Color.BLACK, 1)
        driver.giveMana(you, Color.RED, 1)
        driver.castSpell(you, analysis).isSuccess shouldBe true
        driver.bothPass() // resolve the enchantment; its enters trigger goes on the stack
        if (driver.state.pendingDecision != null) {
            driver.submitTargetSelection(you, listOf(courser))
        }
        var guard = 0
        while (!driver.isPaused && driver.state.stack.isNotEmpty() && guard++ < 20) driver.bothPass()

        withClue("3 damage killed the 3/3") {
            driver.assertInGraveyard(opp, "Centaur Courser")
        }
    }

    test("five death batches sacrifice it, and the reflexive trigger buys a creature back") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 20, "Mountain" to 20), skipMulligans = true)
        val you = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val analysis = driver.putPermanentOnBattlefield(you, "Blood Spatter Analysis")
        fun librarySize(): Int = driver.state.zones[ZoneKey(you, Zone.LIBRARY)]?.size ?: 0
        val libraryBefore = librarySize()

        // Four deaths: four bloodstains, four mills, no sacrifice yet.
        repeat(4) { i ->
            val lion = driver.putCreatureOnBattlefield(you, "Savannah Lions")
            driver.boltAndSettle(you, lion)
            withClue("death batch ${i + 1} added exactly one bloodstain counter") {
                driver.bloodstains(analysis) shouldBe i + 1
            }
            withClue("…and milled exactly one card") {
                // One mill per batch; the Bolts come from putCardInHand, so nothing else draws.
                librarySize() shouldBe libraryBefore - (i + 1)
            }
            withClue("four counters is below the threshold — it is still on the battlefield") {
                driver.findPermanent(you, "Blood Spatter Analysis") shouldBe analysis
            }
        }

        // Fifth death: fifth counter, sacrifice, then the reflexive return-to-hand trigger.
        val lastLion = driver.putCreatureOnBattlefield(you, "Savannah Lions")
        driver.boltAndSettle(you, lastLion)

        // The reflexive ability targets a creature card in the graveyard as it goes on the stack.
        if (driver.state.pendingDecision != null) {
            driver.submitTargetSelection(you, listOf(lastLion))
        }
        var guard = 0
        while (!driver.isPaused && driver.state.stack.isNotEmpty() && guard++ < 20) driver.bothPass()

        withClue("the fifth bloodstain sacrificed the enchantment") {
            driver.findPermanent(you, "Blood Spatter Analysis") shouldBe null
            driver.assertInGraveyard(you, "Blood Spatter Analysis")
        }
        withClue("the reflexive trigger returned a Savannah Lions to hand") {
            driver.getHand(you).any { driver.getCardName(it) == "Savannah Lions" } shouldBe true
        }
    }
})
