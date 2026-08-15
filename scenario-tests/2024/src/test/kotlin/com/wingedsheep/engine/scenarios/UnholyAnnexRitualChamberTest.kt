package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.UnlockRoomDoor
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.RoomComponent
import com.wingedsheep.engine.state.components.identity.RoomFaceId
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dsk.cards.UnholyAnnexRitualChamber
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * End-to-end scenario for `Unholy Annex // Ritual Chamber` (DSK 118), the first real
 * Room implemented on top of the Phase 1–3 mechanic.
 */
class UnholyAnnexRitualChamberTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        d.registerCard(UnholyAnnexRitualChamber)
        d.initMirrorMatch(
            deck = Deck.of(
                "Swamp" to 20,
                "Grizzly Bears" to 20,
            ),
            skipMulligans = true,
        )
        return d
    }

    test("cast Unholy Annex; with no Demon, the end-step trigger draws a card and costs the controller 2 life") {
        val d = driver()
        val p1 = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val roomId = d.putCardInHand(p1, UnholyAnnexRitualChamber.name)
        d.giveMana(p1, Color.BLACK, 3)
        d.submitSuccess(CastSpell(p1, roomId, faceIndex = 0))
        d.bothPass()

        val room = d.state.getEntity(roomId)?.get<RoomComponent>()
        room shouldNotBe null
        room!!.unlocked shouldBe setOf(RoomFaceId("Unholy Annex"))
        room.isFullyUnlocked shouldBe false

        // Walk to end step. With no Demon controlled, the "Otherwise, you lose 2 life" branch fires.
        val lifeBefore = d.getLifeTotal(p1)
        val handBefore = d.getHand(p1).size
        d.passPriorityUntil(Step.END)
        d.bothPass()
        d.getLifeTotal(p1) shouldBe lifeBefore - 2
        d.getHand(p1).size shouldBe handBefore + 1
    }

    test("cast Unholy Annex then unlock Ritual Chamber; the unlock trigger creates a 6/6 flying Demon and the end-step trigger flips to the Demon branch") {
        val d = driver()
        val p1 = d.activePlayer!!
        val p2 = d.getOpponent(p1)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Cast Unholy Annex (face 0, {2}{B}).
        val roomId = d.putCardInHand(p1, UnholyAnnexRitualChamber.name)
        d.giveMana(p1, Color.BLACK, 3)
        d.submitSuccess(CastSpell(p1, roomId, faceIndex = 0))
        d.bothPass()

        // The unlock special action is sorcery speed but uses the same main phase. Pay {3}{B}{B}
        // to unlock Ritual Chamber. The "When you unlock this door" trigger should fire and
        // create a 6/6 black flying Demon token.
        d.giveMana(p1, Color.BLACK, 5)
        val demonsBefore = d.getCreatures(p1).count {
            d.state.getEntity(it)?.get<CardComponent>()?.typeLine?.subtypes
                ?.any { st -> st.value.equals("Demon", ignoreCase = true) } == true
        }
        d.submitSuccess(UnlockRoomDoor(p1, roomId, RoomFaceId("Ritual Chamber")))
        d.bothPass()

        val room = d.state.getEntity(roomId)?.get<RoomComponent>()!!
        room.isFullyUnlocked shouldBe true
        val demonsAfter = d.getCreatures(p1).count {
            d.state.getEntity(it)?.get<CardComponent>()?.typeLine?.subtypes
                ?.any { st -> st.value.equals("Demon", ignoreCase = true) } == true
        }
        demonsAfter shouldBe demonsBefore + 1

        // End step: now p1 controls a Demon, so each opponent loses 2 life and p1 gains 2.
        val p1LifeBefore = d.getLifeTotal(p1)
        val p2LifeBefore = d.getLifeTotal(p2)
        val handBefore = d.getHand(p1).size
        d.passPriorityUntil(Step.END)
        d.bothPass()
        d.getLifeTotal(p1) shouldBe p1LifeBefore + 2
        d.getLifeTotal(p2) shouldBe p2LifeBefore - 2
        d.getHand(p1).size shouldBe handBefore + 1
    }
})
