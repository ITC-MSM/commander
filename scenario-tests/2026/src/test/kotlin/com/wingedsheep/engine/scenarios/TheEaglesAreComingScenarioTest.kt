package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.hob.cards.TheEaglesAreComing
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.ChoiceSlot
import com.wingedsheep.engine.core.PaymentStrategy
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * The Eagles Are Coming! {1}{W} — Instant, Kicker {2}{W}{W}.
 *
 * "Choose target creature you own. If this spell was kicked, instead choose any number of target
 *  creatures you own. Return each chosen creature to your hand. At the beginning of the next upkeep,
 *  create a 4/4 white Bird Soldier creature token with flying for each creature returned to your
 *  hand this way."
 *
 * Two things are worth pinning. The kicker changes the *target count* — one creature unkicked, any
 * number kicked — which is a cast-time announcement, not a resolution-time branch. And the token
 * count is settled when the spell resolves, not when the delayed trigger fires an upkeep later: the
 * pipeline collection it counts is long gone by then, so the count has to have been frozen.
 *
 * Tokens are looked up as "Bird Soldier Token" — `CreateTokenExecutor` derives the name from the
 * creature types, so the bare type finds nothing.
 */
class TheEaglesAreComingScenarioTest : FunSpec({

    val TOKEN = "Bird Soldier Token"

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(TheEaglesAreComing))
        return driver
    }

    fun settleStack(driver: GameTestDriver) {
        var safety = 0
        while (driver.stackSize > 0 && safety < 20) {
            driver.bothPass()
            safety++
        }
    }

    fun tokenCount(driver: GameTestDriver, player: EntityId): Int =
        driver.getPermanents(player).count {
            driver.state.getEntity(it)?.get<CardComponent>()?.name == TOKEN
        }

    /** Cast the Eagles at [targets], kicked or not, and let it resolve. */
    fun castEagles(
        driver: GameTestDriver,
        you: EntityId,
        card: EntityId,
        targets: List<EntityId>,
        kicked: Boolean
    ) {
        driver.submit(
            CastSpell(
                playerId = you,
                cardId = card,
                targets = targets.map { ChosenTarget.Permanent(it) },
                declaredCostSlot = if (kicked) ChoiceSlot.KICKED else null,
                paymentStrategy = PaymentStrategy.AutoPay
            )
        ).isSuccess shouldBe true
        settleStack(driver)
    }

    /**
     * Walk forward to the *next* upkeep step and let the delayed trigger resolve. The main-phase hop
     * first is load-bearing: `passPriorityUntil` returns immediately when the game is already in the
     * requested step, so calling it with UPKEEP from within an upkeep would silently do nothing.
     */
    fun advanceToNextUpkeep(driver: GameTestDriver) {
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.passPriorityUntil(Step.UPKEEP)
        settleStack(driver)
    }

    /**
     * Priority does not reliably revert to the active player after a resolution, so normalise it
     * before acting rather than reasoning about where it landed. With an empty stack a single pass
     * moves priority across without advancing the step.
     */
    fun handPriorityTo(driver: GameTestDriver, player: EntityId) {
        driver.priorityPlayer?.takeIf { it != player }?.let { driver.passPriority(it) }
    }

    test("unkicked: returns the one target and makes a single 4/4 flier at the next upkeep") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val you = driver.activePlayer!!

        val bear = driver.putCreatureOnBattlefield(you, "Centaur Courser")
        val eagles = driver.putCardInHand(you, "The Eagles Are Coming!")
        repeat(2) { driver.putLandOnBattlefield(you, "Plains") }

        castEagles(driver, you, eagles, listOf(bear), kicked = false)

        // Returned to hand immediately; the tokens wait for the upkeep.
        driver.findPermanent(you, "Centaur Courser") shouldBe null
        driver.findCardInHand(you, "Centaur Courser") shouldBe bear
        tokenCount(driver, you) shouldBe 0

        advanceToNextUpkeep(driver)
        tokenCount(driver, you) shouldBe 1
    }

    test("kicked: any number of targets, one token per creature returned") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val you = driver.activePlayer!!

        val a = driver.putCreatureOnBattlefield(you, "Centaur Courser")
        val b = driver.putCreatureOnBattlefield(you, "Savannah Lions")
        val c = driver.putCreatureOnBattlefield(you, "Phantom Warrior")
        val eagles = driver.putCardInHand(you, "The Eagles Are Coming!")
        // {1}{W} + kicker {2}{W}{W} = {3}{W}{W}{W} → six Plains cover it.
        repeat(6) { driver.putLandOnBattlefield(you, "Plains") }

        castEagles(driver, you, eagles, listOf(a, b, c), kicked = true)

        driver.getCreatures(you).isEmpty() shouldBe true
        listOf(a, b, c).forEach { driver.getHand(you).contains(it) shouldBe true }

        advanceToNextUpkeep(driver)
        tokenCount(driver, you) shouldBe 3
    }

    test("unkicked cast may not choose more than one target") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val you = driver.activePlayer!!

        val a = driver.putCreatureOnBattlefield(you, "Centaur Courser")
        val b = driver.putCreatureOnBattlefield(you, "Savannah Lions")
        val eagles = driver.putCardInHand(you, "The Eagles Are Coming!")
        repeat(2) { driver.putLandOnBattlefield(you, "Plains") }

        // Without the kicker the spell has exactly one target slot, so a two-target
        // announcement is illegal.
        driver.submit(
            CastSpell(
                playerId = you,
                cardId = eagles,
                targets = listOf(a, b).map { ChosenTarget.Permanent(it) },
                declaredCostSlot = null,
                paymentStrategy = PaymentStrategy.AutoPay
            )
        ).isSuccess shouldBe false
    }

    test("a token creature returned to hand still counts toward the Bird Soldiers") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val you = driver.activePlayer!!

        // Cast the Eagles once to manufacture a token creature to bounce.
        val seed = driver.putCreatureOnBattlefield(you, "Savannah Lions")
        val first = driver.putCardInHand(you, "The Eagles Are Coming!")
        repeat(8) { driver.putLandOnBattlefield(you, "Plains") }
        castEagles(driver, you, first, listOf(seed), kicked = false)
        advanceToNextUpkeep(driver)

        val birdToken = driver.findPermanent(you, TOKEN)!!
        tokenCount(driver, you) shouldBe 1

        // Bounce the token itself, at instant speed in the opponent's upkeep. It ceases to exist on
        // reaching the hand (CR 111.7), but the ruling says it is still counted — one new Bird
        // Soldier next upkeep.
        handPriorityTo(driver, you)
        val second = driver.putCardInHand(you, "The Eagles Are Coming!")
        castEagles(driver, you, second, listOf(birdToken), kicked = false)

        tokenCount(driver, you) shouldBe 0   // the bounced token is gone
        advanceToNextUpkeep(driver)
        tokenCount(driver, you) shouldBe 1   // and it earned a replacement
    }
})
