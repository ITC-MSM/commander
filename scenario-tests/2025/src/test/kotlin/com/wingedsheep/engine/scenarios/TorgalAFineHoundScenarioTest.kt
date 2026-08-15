package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.SummonFenrir
import com.wingedsheep.mtg.sets.definitions.fin.cards.TorgalAFineHound
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Torgal, A Fine Hound — {1}{G} Legendary Creature — Wolf, 2/2 (FIN).
 *
 *   Whenever you cast your first Human creature spell each turn, that creature enters with an
 *   additional +1/+1 counter on it for each Dog and/or Wolf you control.
 *   {T}: Add one mana of any color.
 *
 * The trigger is `oncePerTurn`, so only the *first* Human creature spell each turn benefits; the
 * counter rides onto the resolving spell via [EffectTarget.TriggeringEntity]; the count scales with
 * Dogs/Wolves you control. These tests pin all three behaviours plus the "Human only" filter.
 */
class TorgalAFineHoundScenarioTest : FunSpec({

    val projector = StateProjector()

    fun GameTestDriver.plusOneCounters(id: EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(
            TestCards.all + listOf(TorgalAFineHound, SummonFenrir)
        )
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

    fun GameTestDriver.castHuman(me: EntityId, name: String): EntityId {
        val spell = putCardInHand(me, name)
        giveColorlessMana(me, 2)
        giveMana(me, Color.RED, 1)
        giveMana(me, Color.WHITE, 1)
        castSpell(me, spell)
        resolveAll()
        return findPermanent(me, name)!!
    }

    test("first Human creature spell enters with +1/+1 per Dog/Wolf you control (Torgal alone = 1)") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        driver.putCreatureOnBattlefield(me, "Torgal, A Fine Hound")

        // Only Torgal (a Wolf) is on the battlefield -> exactly one +1/+1 counter.
        val human = driver.castHuman(me, "Blade of the Ninth Watch")
        driver.plusOneCounters(human) shouldBe 1
        // 2/1 base + one +1/+1 counter = 3/2.
        projector.project(driver.state).getPower(human) shouldBe 3
        projector.project(driver.state).getToughness(human) shouldBe 2
    }

    test("count scales with Dogs/Wolves you control (Torgal + Fenrir = 2 Wolves)") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        driver.putCreatureOnBattlefield(me, "Torgal, A Fine Hound")
        // Summon: Fenrir is an Enchantment Creature — Saga Wolf, so it counts as a Wolf.
        driver.putPermanentOnBattlefield(me, "Summon: Fenrir")

        val human = driver.castHuman(me, "Blade of the Ninth Watch")
        driver.plusOneCounters(human) shouldBe 2
    }

    test("only the FIRST Human creature spell each turn triggers (oncePerTurn)") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        driver.putCreatureOnBattlefield(me, "Torgal, A Fine Hound")

        val first = driver.castHuman(me, "Blade of the Ninth Watch")
        driver.plusOneCounters(first) shouldBe 1

        // A second Human creature spell the same turn gets no counter.
        val second = driver.castHuman(me, "First Strike Knight")
        driver.plusOneCounters(second) shouldBe 0
    }

    test("non-Human creature spells do not trigger") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        driver.putCreatureOnBattlefield(me, "Torgal, A Fine Hound")

        // Llanowar Elves is an Elf Druid — not a Human, so no counter.
        val elf = driver.putCardInHand(me, "Llanowar Elves")
        driver.giveMana(me, Color.GREEN, 1)
        driver.castSpell(me, elf)
        driver.resolveAll()
        driver.plusOneCounters(driver.findPermanent(me, "Llanowar Elves")!!) shouldBe 0
    }
})
