package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseNumberDecision
import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.NumberChosenResponse
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.core.PendingDecision
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.handlers.continuations.entityIdToChosenTarget
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Cemetery Desecrator (VOW #100) — {4}{B}{B} 4/4 Zombie.
 *
 * "Menace
 *  When this creature enters or dies, exile another card from a graveyard. When you do, choose one —
 *  • Remove X counters from target permanent, where X is the mana value of the exiled card.
 *  • Target creature an opponent controls gets -X/-X until end of turn, where X is the mana value
 *    of the exiled card."
 *
 * What these tests pin down, in the order the card's own wording raises the questions:
 *
 *  - **X is dynamic.** `RemoveAnyNumberOfCountersEffect`'s bounds used to be `Int`s, so "remove X
 *    counters, where X is …" had no spelling at all. Both bounds are now `DynamicAmount`s and both
 *    modes read the same `StoredCardManaValue("exiledCard")`, evaluated once at resolution.
 *  - **The floor clamps to what is actually there.** "Remove X counters" from a permanent carrying
 *    fewer than X takes all of them; it is not a fizzle and not a debt.
 *  - **"another" is load-bearing on the dies half.** By the time the dies trigger resolves the
 *    Desecrator's own card is sitting in a graveyard, and it is the one card the exile may not take.
 *  - **CR 603.12 — no exile, no modal.** With nothing exilable the reflexive ability must not
 *    trigger at all, rather than putting a mode question on the stack for an X of 0. That is the
 *    check that needed `ReflexiveTriggerEffectExecutor.gatherableCount` to score a *multi-player*
 *    gather ("a graveyard" is every player's); left unscored it failed open.
 */
class CemeteryDesecratorScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        return driver
    }

    fun addCounters(driver: GameTestDriver, entityId: EntityId, type: CounterType, count: Int) {
        val newState = driver.state.updateEntity(entityId) { container ->
            val existing = container.get<CountersComponent>() ?: CountersComponent()
            container.with(existing.withAdded(type, count))
        }
        driver.replaceState(newState)
    }

    fun counts(driver: GameTestDriver, entityId: EntityId): Map<CounterType, Int> =
        driver.state.getEntity(entityId)?.get<CountersComponent>()?.counters ?: emptyMap()

    /** Pass priority until the pending decision satisfies [matches], or give up. */
    fun GameTestDriver.advanceUntil(what: String, matches: (PendingDecision?) -> Boolean): PendingDecision {
        var safety = 0
        while (!matches(pendingDecision) && safety++ < 12) bothPass()
        return pendingDecision?.takeIf { matches(it) }
            ?: error("expected $what, got ${pendingDecision?.let { it::class.simpleName }}: ${pendingDecision?.prompt}")
    }

    /** Settle every stack object and auto-resolvable step without demanding a decision appear. */
    fun GameTestDriver.settle(times: Int = 8) {
        repeat(times) { if (pendingDecision == null) bothPass() }
    }

    /**
     * Put the Desecrator onto the battlefield by *casting* it, so the enters trigger really fires —
     * direct placement fires no ETB triggers.
     */
    fun castDesecrator(driver: GameTestDriver) {
        val you = driver.player1
        val card = driver.putCardInHand(you, "Cemetery Desecrator")
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.giveColorlessMana(you, 4)
        driver.giveMana(you, Color.BLACK, 2)
        driver.castSpell(you, card).error shouldBe null
        driver.settle()
    }

    /**
     * Answer the "exile another card from a graveyard" selection with [cardId]. Requires more than
     * one exilable card — with exactly one the executor has nothing to ask and takes it silently.
     */
    fun exile(driver: GameTestDriver, cardId: EntityId) {
        val select = driver.advanceUntil("the exile selection") { it is SelectCardsDecision }
        driver.submitCardSelection(select.playerId, listOf(cardId))
    }

    /** Pick the mode whose label contains [needle], then settle onto whatever comes next. */
    fun chooseMode(driver: GameTestDriver, needle: String) {
        val modal = driver.advanceUntil("the mode choice") { it is ChooseOptionDecision } as ChooseOptionDecision
        val index = modal.options.indexOfFirst { it.contains(needle, ignoreCase = true) }
        check(index >= 0) { "no mode matching '$needle' in ${modal.options}" }
        driver.submitDecision(modal.playerId, OptionChosenResponse(modal.id, index))
    }

    /** Answer every per-kind counter prompt with its maximum, so all X counters actually come off. */
    fun takeAllOffered(driver: GameTestDriver) {
        var safety = 0
        while (safety++ < 10) {
            val decision = driver.pendingDecision as? ChooseNumberDecision ?: return
            driver.submitDecision(decision.playerId, NumberChosenResponse(decision.id, decision.maxValue))
        }
    }

    /**
     * A board with two cards in the opponent's graveyard — Centaur Courser ({2}{G}, mana value 3)
     * and Lightning Bolt ({R}, mana value 1) — so the exile is a genuine choice rather than the
     * one card there was. Returns the Courser's id.
     */
    fun stockGraveyard(driver: GameTestDriver): EntityId {
        val courser = driver.putCardInGraveyard(driver.player2, "Centaur Courser")
        driver.putCardInGraveyard(driver.player2, "Lightning Bolt")
        return courser
    }

    test("mode 1 removes exactly the exiled card's mana value in counters") {
        val driver = createDriver()
        val foe = driver.player2

        val courser = stockGraveyard(driver)
        val counted = driver.putCreatureOnBattlefield(foe, "Savannah Lions")
        addCounters(driver, counted, CounterType.PLUS_ONE_PLUS_ONE, 5)

        castDesecrator(driver)
        exile(driver, courser)
        chooseMode(driver, "Remove X counters")

        val targets = driver.advanceUntil("the mode's target") { it is ChooseTargetsDecision }
        driver.submitTargetSelection(targets.playerId, listOf(counted))
        driver.settle()
        takeAllOffered(driver)
        driver.settle()

        // Courser is mana value 3, so three of the five counters come off.
        counts(driver, counted)[CounterType.PLUS_ONE_PLUS_ONE] shouldBe 2
        (driver.getExileCardNames(driver.player1) + driver.getExileCardNames(foe))
            .contains("Centaur Courser") shouldBe true
    }

    test("a permanent carrying fewer than X counters simply loses all of them") {
        val driver = createDriver()
        val foe = driver.player2

        val courser = stockGraveyard(driver)
        val counted = driver.putCreatureOnBattlefield(foe, "Savannah Lions")
        addCounters(driver, counted, CounterType.PLUS_ONE_PLUS_ONE, 1)

        castDesecrator(driver)
        exile(driver, courser)
        chooseMode(driver, "Remove X counters")

        val targets = driver.advanceUntil("the mode's target") { it is ChooseTargetsDecision }
        driver.submitTargetSelection(targets.playerId, listOf(counted))
        driver.settle()
        takeAllOffered(driver)
        driver.settle()

        (counts(driver, counted)[CounterType.PLUS_ONE_PLUS_ONE] ?: 0) shouldBe 0
    }

    test("mode 2 shrinks an opponent's creature by the exiled card's mana value") {
        val driver = createDriver()
        val foe = driver.player2

        val courser = stockGraveyard(driver)
        // Savannah Lions is a 2/1, so -3/-3 from a mana value 3 card kills it.
        driver.putCreatureOnBattlefield(foe, "Savannah Lions")

        castDesecrator(driver)
        exile(driver, courser)
        chooseMode(driver, "-X/-X")

        val targets = driver.advanceUntil("the mode's target") { it is ChooseTargetsDecision }
        val victim = driver.findPermanent(foe, "Savannah Lions")!!
        driver.submitTargetSelection(targets.playerId, listOf(victim))
        driver.settle()

        driver.findPermanent(foe, "Savannah Lions") shouldBe null
    }

    test("the smaller exile gives the smaller X") {
        val driver = createDriver()
        val foe = driver.player2

        stockGraveyard(driver)
        val bolt = driver.getGraveyard(foe).first {
            driver.state.getEntity(it)
                ?.get<com.wingedsheep.engine.state.components.identity.CardComponent>()
                ?.name == "Lightning Bolt"
        }
        val counted = driver.putCreatureOnBattlefield(foe, "Savannah Lions")
        addCounters(driver, counted, CounterType.PLUS_ONE_PLUS_ONE, 5)

        castDesecrator(driver)
        exile(driver, bolt) // {R} — mana value 1
        chooseMode(driver, "Remove X counters")

        val targets = driver.advanceUntil("the mode's target") { it is ChooseTargetsDecision }
        driver.submitTargetSelection(targets.playerId, listOf(counted))
        driver.settle()
        takeAllOffered(driver)
        driver.settle()

        counts(driver, counted)[CounterType.PLUS_ONE_PLUS_ONE] shouldBe 4
    }

    test("with every graveyard empty the reflexive ability never triggers") {
        val driver = createDriver()
        driver.putCreatureOnBattlefield(driver.player2, "Savannah Lions")

        castDesecrator(driver)
        driver.settle()

        driver.getGraveyard(driver.player2).isEmpty() shouldBe true
        // Nothing to exile, so CR 603.12's "when you do" never fires: no mode question at all.
        (driver.pendingDecision is ChooseOptionDecision) shouldBe false
        (driver.pendingDecision is ChooseTargetsDecision) shouldBe false
    }

    test("the dies trigger may not exile the Desecrator's own card") {
        val driver = createDriver()
        val you = driver.player1
        val foe = driver.player2

        val desecrator = driver.putCreatureOnBattlefield(you, "Cemetery Desecrator")
        val courser = stockGraveyard(driver)

        driver.putCardInHand(you, "Doom Blade")
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.giveMana(you, Color.BLACK, 1)
        driver.giveColorlessMana(you, 1)
        driver.castSpellWithTargets(
            you,
            driver.findCardInHand(you, "Doom Blade")!!,
            listOf(entityIdToChosenTarget(driver.state, desecrator))
        ).error shouldBe null
        driver.settle()

        val select = driver.advanceUntil("the exile selection") { it is SelectCardsDecision }
            as SelectCardsDecision
        // The Desecrator is in a graveyard by now, and is the one card "another" excludes.
        driver.getGraveyard(you).contains(desecrator) shouldBe true
        select.options.contains(desecrator) shouldBe false
        select.options.contains(courser) shouldBe true
    }
})
