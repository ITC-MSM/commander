package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.UnlockRoomDoor
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.identity.RoomComponent
import com.wingedsheep.engine.state.components.identity.RoomFaceId
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dsk.cards.MeatLockerDrownedDiner
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario for `Meat Locker // Drowned Diner` (DSK 65), a split-layout Room (CR 709.5).
 *
 * Meat Locker {2}{U}  — "When you unlock this door, tap up to one target creature and put two
 *                        stun counters on it."
 * Drowned Diner {3}{U}{U} — "When you unlock this door, draw three cards, then discard a card."
 *
 * Both doors fire a "When you unlock this door" trigger (CR 709.5h). We cast a half (which enters
 * unlocked, firing the trigger) and also unlock the second door as a special action to drive the
 * other trigger.
 */
class MeatLockerDrownedDinerTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        d.registerCard(MeatLockerDrownedDiner)
        d.initMirrorMatch(
            deck = Deck.of("Island" to 30, "Grizzly Bears" to 20),
            skipMulligans = true,
        )
        return d
    }

    fun stunCount(d: GameTestDriver, entity: EntityId): Int =
        d.state.getEntity(entity)?.get<CountersComponent>()?.getCount(CounterType.STUN) ?: 0

    test("Meat Locker unlock taps the chosen creature and puts two stun counters on it") {
        val d = driver()
        val p1 = d.activePlayer!!
        val p2 = d.getOpponent(p1)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Opponent has an untapped creature to tap + stun.
        val bears = d.putCreatureOnBattlefield(p2, "Grizzly Bears")

        // Cast Meat Locker ({2}{U}, face 0). The cast face enters unlocked, firing the trigger.
        val roomId = d.putCardInHand(p1, MeatLockerDrownedDiner.name)
        d.giveMana(p1, Color.BLUE, 3)
        d.submitSuccess(CastSpell(p1, roomId, faceIndex = 0))
        d.bothPass()

        val room = d.state.getEntity(roomId)?.get<RoomComponent>()
        room shouldNotBe null
        room!!.unlocked shouldBe setOf(RoomFaceId("Meat Locker"))

        // The trigger asks for its (up-to-one) target — pick the opponent's creature.
        d.submitTargetSelection(p1, listOf(bears))
        d.bothPass()

        d.isTapped(bears) shouldBe true
        stunCount(d, bears) shouldBe 2
    }

    test("Meat Locker unlock with no target chosen does nothing (up to one)") {
        val d = driver()
        val p1 = d.activePlayer!!
        val p2 = d.getOpponent(p1)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val bears = d.putCreatureOnBattlefield(p2, "Grizzly Bears")

        val roomId = d.putCardInHand(p1, MeatLockerDrownedDiner.name)
        d.giveMana(p1, Color.BLUE, 3)
        d.submitSuccess(CastSpell(p1, roomId, faceIndex = 0))
        d.bothPass()

        // "Up to one" — decline by choosing no target.
        d.submitTargetSelection(p1, emptyList())
        d.bothPass()

        d.isTapped(bears) shouldBe false
        stunCount(d, bears) shouldBe 0
    }

    test("Drowned Diner unlock draws three cards then discards one") {
        val d = driver()
        val p1 = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Cast Meat Locker (face 0) first so the Room is on the battlefield with Drowned Diner locked.
        val roomId = d.putCardInHand(p1, MeatLockerDrownedDiner.name)
        d.giveMana(p1, Color.BLUE, 3)
        d.submitSuccess(CastSpell(p1, roomId, faceIndex = 0))
        d.bothPass()
        // Meat Locker's trigger asks for a target; decline.
        d.submitTargetSelection(p1, emptyList())
        d.bothPass()

        // Now unlock Drowned Diner ({3}{U}{U}) as a special action.
        d.giveMana(p1, Color.BLUE, 5)
        val handBefore = d.getHand(p1).size
        d.submitSuccess(UnlockRoomDoor(p1, roomId, RoomFaceId("Drowned Diner")))
        d.state.getEntity(roomId)!!.get<RoomComponent>()!!.isFullyUnlocked shouldBe true

        // Resolve the OnDoorUnlocked trigger: draw three, then discard one (controller chooses).
        d.bothPass()
        // Draw three → +3, then a discard selection peels one back off.
        val toDiscard = d.getHand(p1).first()
        d.submitCardSelection(p1, listOf(toDiscard))
        d.bothPass()

        // Net hand change: +3 drawn − 1 discarded = +2.
        d.getHand(p1).size shouldBe handBefore + 2
    }
})
