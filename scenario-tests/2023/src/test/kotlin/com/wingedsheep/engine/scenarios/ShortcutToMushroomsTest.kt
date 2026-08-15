package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.ltr.cards.ShortcutToMushrooms
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Shortcut to Mushrooms (LTR) — end-step counter trigger gated on Gap 19's per-player
 * `YouHadPermanentLeaveBattlefieldThisTurn` tracker.
 */
class ShortcutToMushroomsTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(ShortcutToMushrooms))
        return driver
    }

    fun GameTestDriver.plusOnePlusOneOn(entityId: EntityId): Int =
        state.getEntity(entityId)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    test("end-step trigger fires when a permanent you controlled died this turn") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), skipMulligans = true)
        val active = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(active, "Shortcut to Mushrooms")
        val bear = driver.putCreatureOnBattlefield(active, "Grizzly Bears")
        val target = driver.putCreatureOnBattlefield(active, "Grizzly Bears")

        // Move bear to graveyard this turn (sets the per-player LTB tracker).
        val moveResult = com.wingedsheep.engine.handlers.effects.ZoneTransitionService.moveToZone(
            state = driver.state,
            entityId = bear,
            destinationZone = Zone.GRAVEYARD
        )
        driver.replaceState(moveResult.state)

        driver.passPriorityUntil(Step.END)
        // End-step trigger paused for target selection on a ChooseTargetsDecision.
        driver.submitTargetSelection(active, listOf(target))
        while (driver.state.stack.isNotEmpty()) driver.bothPass()

        driver.plusOnePlusOneOn(target) shouldBe 1
    }

    test("trigger does NOT fire when no permanent of yours left the battlefield this turn") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), skipMulligans = true)
        val active = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(active, "Shortcut to Mushrooms")
        val target = driver.putCreatureOnBattlefield(active, "Grizzly Bears")

        driver.passPriorityUntil(Step.END)
        // No pending decision: the intervening-if fails and the end-step trigger never targets.
        driver.pendingDecision shouldBe null
        while (driver.state.stack.isNotEmpty()) driver.bothPass()
        driver.plusOnePlusOneOn(target) shouldBe 0
    }
})
