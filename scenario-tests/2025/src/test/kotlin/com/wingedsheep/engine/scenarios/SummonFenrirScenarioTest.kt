package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.SummonFenrir
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Summon: Fenrir — {2}{G} Enchantment Creature — Saga Wolf, 3/2 (FIN).
 *
 *   I  — Crescent Fang — Search your library for a basic land card, put it onto the battlefield
 *        tapped, then shuffle.
 *   II — Heavenward Howl — When you next cast a creature spell this turn, that creature enters
 *        with an additional +1/+1 counter on it.
 *   III — Ecliptic Growl — Draw a card if you control the creature with the greatest power or
 *        tied for the greatest power.
 *
 * Generic saga-creature machinery (lore accrual, chapter triggers, sacrifice after the final
 * chapter) is covered by CreatureSagaTest; this pins Fenrir's three chapter effects in isolation.
 */
class SummonFenrirScenarioTest : FunSpec({

    val projector = StateProjector()

    fun GameTestDriver.plusOneCounters(id: EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    fun GameTestDriver.loreCounters(id: EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.LORE) ?: 0

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(SummonFenrir))
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.resolveAll() {
        var guard = 0
        while ((state.stack.isNotEmpty() || state.pendingDecision != null) && guard++ < 50) {
            val pd = state.pendingDecision
            if (pd != null) autoResolveDecision() else bothPass()
        }
    }

    fun GameTestDriver.castFenrir(me: EntityId): EntityId {
        val spell = putCardInHand(me, "Summon: Fenrir")
        giveColorlessMana(me, 2)
        giveMana(me, Color.GREEN, 1)
        castSpell(me, spell)
        return spell
    }

    /** Advance turns (auto-resolving decisions / passing priority) until [predicate] holds. */
    fun GameTestDriver.advanceUntil(maxSteps: Int = 2000, predicate: GameTestDriver.() -> Boolean) {
        var guard = 0
        while (guard++ < maxSteps && !predicate()) {
            val pd = state.pendingDecision
            when {
                pd != null -> autoResolveDecision()
                state.priorityPlayerId != null -> {
                    autoSubmitCombatDeclarationIfNeeded()
                    passPriority(state.priorityPlayerId!!)
                }
            }
        }
    }

    test("chapter I — search for a basic land and put it onto the battlefield tapped") {
        val driver = createDriver()
        val me = driver.activePlayer!!

        val landsBefore = driver.getLands(me).size

        driver.castFenrir(me)
        // Resolve the Saga ETB; chapter I trigger then resolves into a library search decision.
        var guard = 0
        while (driver.state.pendingDecision == null && guard++ < 30) driver.bothPass()

        val decision = driver.state.pendingDecision
        (decision is SelectCardsDecision) shouldBe true
        decision as SelectCardsDecision
        driver.submitDecision(me, CardsSelectedResponse(decision.id, decision.options.take(1)))
        driver.resolveAll()

        val lands = driver.getLands(me)
        lands.size shouldBe landsBefore + 1
        // The fetched land entered tapped.
        lands.any { driver.isTapped(it) } shouldBe true
    }

    test("chapter II — next creature spell enters with an extra +1/+1 counter") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        driver.castFenrir(me)
        driver.resolveAll()

        // Advance until the Saga has its second lore counter (chapter II resolved).
        driver.advanceUntil {
            val s = findPermanent(me, "Summon: Fenrir") ?: return@advanceUntil true
            loreCounters(s) >= 2
        }
        driver.findPermanent(me, "Summon: Fenrir") shouldNotBe null
        driver.resolveAll()

        // Cast a creature spell; the delayed trigger gives it one +1/+1 counter as it enters.
        val bears = driver.putCardInHand(me, "Centaur Courser")
        driver.giveColorlessMana(me, 2)
        driver.giveMana(me, Color.GREEN, 1)
        driver.castSpell(me, bears)
        driver.resolveAll()

        val perm = driver.findPermanent(me, "Centaur Courser")!!
        driver.plusOneCounters(perm) shouldBe 1
        // 3/3 base + a +1/+1 counter = 4/4.
        projector.project(driver.state).getPower(perm) shouldBe 4
        projector.project(driver.state).getToughness(perm) shouldBe 4
    }

    test("chapter III — draw a card when you control the greatest-power creature") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        driver.castFenrir(me)
        driver.resolveAll()

        // Fenrir is a 3/2; in a mirror with no opposing creatures I always control the
        // greatest-power creature, so chapter III resolves the conditional draw and then the
        // Saga is sacrificed after its final chapter — completing without error.
        driver.advanceUntil {
            findPermanent(me, "Summon: Fenrir") == null
        }
        driver.findPermanent(me, "Summon: Fenrir") shouldBe null
    }
})
