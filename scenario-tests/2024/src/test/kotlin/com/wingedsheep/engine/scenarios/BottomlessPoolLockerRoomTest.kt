package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.identity.RoomComponent
import com.wingedsheep.engine.state.components.identity.RoomFaceId
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dsk.cards.BottomlessPoolLockerRoom
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario for `Bottomless Pool // Locker Room` (DSK 43), a split-layout Room (CR 709.5).
 *
 * Bottomless Pool {U}  — "When you unlock this door, return up to one target creature to its
 *                         owner's hand." Casting the half enters it unlocked, firing the trigger.
 * Locker Room {4}{U}   — "Whenever one or more creatures you control deal combat damage to a
 *                         player, draw a card."
 */
class BottomlessPoolLockerRoomTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        d.registerCard(BottomlessPoolLockerRoom)
        d.initMirrorMatch(
            deck = Deck.of("Island" to 20, "Grizzly Bears" to 20),
            skipMulligans = true,
        )
        return d
    }

    test("casting Bottomless Pool unlocks its door and bounces a target creature to its owner's hand") {
        val d = driver()
        val p1 = d.activePlayer!!
        val p2 = d.getOpponent(p1)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Opponent has a creature in play to bounce.
        val bears = d.putCreatureOnBattlefield(p2, "Grizzly Bears")

        // Cast Bottomless Pool ({U}, face 0). The cast face enters unlocked, firing the trigger.
        val roomId = d.putCardInHand(p1, BottomlessPoolLockerRoom.name)
        d.giveMana(p1, Color.BLUE, 1)
        d.submitSuccess(CastSpell(p1, roomId, faceIndex = 0))
        d.bothPass()

        val room = d.state.getEntity(roomId)?.get<RoomComponent>()
        room shouldNotBe null
        room!!.unlocked shouldBe setOf(RoomFaceId("Bottomless Pool"))

        // The "When you unlock this door" trigger asks for its (up-to-one) target.
        val handBefore = d.getHand(p2).size
        d.submitTargetSelection(p1, listOf(bears))
        d.bothPass()

        // The bear is returned to its owner's hand.
        d.getCreatures(p2) shouldBe emptyList()
        d.getHand(p2).size shouldBe handBefore + 1
        d.getHand(p2) shouldContain bears
    }

    test("Locker Room draws a card when creatures you control deal combat damage to a player") {
        val d = driver()
        val p1 = d.activePlayer!!
        val p2 = d.getOpponent(p1)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Cast Locker Room ({4}{U}, face 1).
        val roomId = d.putCardInHand(p1, BottomlessPoolLockerRoom.name)
        d.giveMana(p1, Color.BLUE, 5)
        d.submitSuccess(CastSpell(p1, roomId, faceIndex = 1))
        d.bothPass()
        d.state.getEntity(roomId)!!.get<RoomComponent>()!!.unlocked shouldBe setOf(RoomFaceId("Locker Room"))

        // An attacker connects with the opponent.
        val attacker = d.putCreatureOnBattlefield(p1, "Grizzly Bears")
        d.removeSummoningSickness(attacker)

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(p1, listOf(attacker), p2)
        d.bothPass()
        d.declareNoBlockers(p2)

        val handBefore = d.getHand(p1).size
        // Combat damage resolves, then the "draw a card" trigger resolves.
        d.bothPass()
        d.bothPass()

        d.getHand(p1).size shouldBe handBefore + 1
        d.assertLifeTotal(p2, 18)
    }
})
