package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.UnlockRoomDoor
import com.wingedsheep.engine.state.components.identity.RoomFaceId
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dsk.cards.BottomlessPoolLockerRoom
import com.wingedsheep.mtg.sets.definitions.dsk.cards.CentralElevatorPromisingStairs
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardLayout
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

/**
 * Scenario for `Central Elevator // Promising Stairs` (DSK 44), a split-layout Room (CR 709.5).
 *
 * Central Elevator {3}{U} — "When you unlock this door, search your library for a Room card that
 *   doesn't have the same name as a Room you control, reveal it, put it into your hand, then
 *   shuffle." — exercises [com.wingedsheep.sdk.scripting.predicates.CardPredicate.NameNotSharedWithControlledRoom].
 * Promising Stairs {2}{U} — "At the beginning of your upkeep, surveil 1. You win the game if there
 *   are eight or more different names among unlocked doors of Rooms you control." — a state-triggered
 *   ability (CR 603.8) over `DynamicAmount.UnlockedDoors(distinctNames = true)`.
 */
class CentralElevatorPromisingStairsTest : FunSpec({

    // Ability-less two-door helper Rooms, each contributing two distinct door names when fully
    // unlocked — used to drive Promising Stairs' "different names" win threshold.
    fun roomWith(a: String, b: String) = card("$a // $b") {
        layout = CardLayout.SPLIT
        colorIdentity = "W"
        face(a) { manaCost = "{W}"; typeLine = "Enchantment — Room"; oracleText = "" }
        face(b) { manaCost = "{W}"; typeLine = "Enchantment — Room"; oracleText = "" }
    }

    val room1 = roomWith("Alpha", "Beta")
    val room2 = roomWith("Gamma", "Delta")
    val room3 = roomWith("Epsilon", "Zeta")
    val room4 = roomWith("Eta", "Theta")

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        d.registerCard(CentralElevatorPromisingStairs)
        d.registerCard(BottomlessPoolLockerRoom)
        listOf(room1, room2, room3, room4).forEach { d.registerCard(it) }
        d.initMirrorMatch(
            deck = Deck.of("Island" to 20, "Grizzly Bears" to 20),
            skipMulligans = true,
        )
        return d
    }

    test("Central Elevator's search offers only Room cards not sharing a name with a Room you control") {
        val d = driver()
        val p1 = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Library candidates: a copy of this card (shares the unlocked "Central Elevator" door name)
        // and a Bottomless Pool // Locker Room (shares neither name).
        val sharingCopy = d.putCardOnTopOfLibrary(p1, CentralElevatorPromisingStairs.name)
        val nonSharing = d.putCardOnTopOfLibrary(p1, BottomlessPoolLockerRoom.name)

        // Cast Central Elevator (face 0). It enters with "Central Elevator" unlocked, firing the
        // search trigger; "Promising Stairs" stays locked, so only "Central Elevator" is controlled.
        val roomId = d.putCardInHand(p1, CentralElevatorPromisingStairs.name)
        d.giveMana(p1, Color.BLUE, 4)
        d.submitSuccess(CastSpell(p1, roomId, faceIndex = 0))
        d.bothPass() // resolve the Room spell → door unlocks → trigger goes on the stack
        d.bothPass() // resolve the trigger → search → SelectCardsDecision

        val decision = d.pendingDecision as SelectCardsDecision
        decision.options shouldContain nonSharing
        decision.options shouldNotContain sharingCopy

        // Find the legal (non-sharing) Room → it goes to hand revealed.
        d.submitCardSelection(p1, listOf(nonSharing))
        d.getHand(p1) shouldContain nonSharing
    }

    test("Promising Stairs wins the game at eight different unlocked door names, but not at seven") {
        val d = driver()
        val p1 = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Promising Stairs in play (its state-triggered ability now watches the door count).
        // Casting face 1 unlocks "Promising Stairs" → 1 distinct name.
        val stairs = d.putCardInHand(p1, CentralElevatorPromisingStairs.name)
        d.giveMana(p1, Color.BLUE, 3)
        d.submitSuccess(CastSpell(p1, stairs, faceIndex = 1))
        d.bothPass()

        // Fully unlock room1..room3 → +6 names (Alpha,Beta,Gamma,Delta,Epsilon,Zeta) → 7 total.
        // Casting each Room (face 0) unlocks its first door; UnlockRoomDoor unlocks the second.
        // The unlock is a special action that resolves immediately, so we must NOT pass priority
        // after it (an empty-stack pass would advance out of the main phase).
        for (room in listOf(room1, room2, room3)) {
            val id = d.putCardInHand(p1, room.name)
            d.giveMana(p1, Color.WHITE, 1)
            d.submitSuccess(CastSpell(p1, id, faceIndex = 0))
            d.bothPass()
            d.giveMana(p1, Color.WHITE, 1)
            d.submitSuccess(UnlockRoomDoor(p1, id, RoomFaceId(room.cardFaces[1].name)))
        }
        d.state.gameOver shouldBe false

        // Cast room4 (face 0) → "Eta" is the eighth distinct name → the win ability triggers.
        val r4 = d.putCardInHand(p1, room4.name)
        d.giveMana(p1, Color.WHITE, 1)
        d.submitSuccess(CastSpell(p1, r4, faceIndex = 0))

        // The state-triggered ability goes on the stack at the next priority check; step through
        // priority/decisions until the win resolves and the opponent loses.
        var guard = 0
        while (!d.state.gameOver && guard++ < 50) {
            if (d.pendingDecision != null) d.autoResolveDecision() else d.bothPass()
        }

        d.assertGameOver(expectedWinner = p1)
    }
})
