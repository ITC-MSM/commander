package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.UnlockRoomDoor
import com.wingedsheep.engine.state.components.identity.RoomFaceId
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dsk.cards.RampagingSoulrager
import com.wingedsheep.mtg.sets.definitions.dsk.cards.UnholyAnnexRitualChamber
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Rampaging Soulrager (DSK 151): "+3/+0 as long as there are two or more unlocked doors among
 * Rooms you control." Proves the [com.wingedsheep.sdk.scripting.values.DynamicAmount.UnlockedDoors]
 * door count drives a [com.wingedsheep.sdk.scripting.ConditionalStaticAbility] under projection,
 * counting *door faces* — so a single Room with both doors unlocked supplies the two needed.
 */
class RampagingSoulragerScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        d.registerCard(RampagingSoulrager)
        d.registerCard(UnholyAnnexRitualChamber)
        d.initMirrorMatch(
            deck = Deck.of("Swamp" to 20, "Grizzly Bears" to 20),
            skipMulligans = true,
        )
        return d
    }

    test("Soulrager is a 1/4 with zero or one unlocked door, and a 4/4 once a second door is unlocked") {
        val d = driver()
        val p1 = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val soulrager = d.putCreatureOnBattlefield(p1, RampagingSoulrager.name)

        // No Rooms yet → 0 unlocked doors → base 1/4.
        d.state.projectedState.getPower(soulrager) shouldBe 1
        d.state.projectedState.getToughness(soulrager) shouldBe 4

        // Cast Unholy Annex (face 0). One unlocked door — still below the threshold.
        val roomId = d.putCardInHand(p1, UnholyAnnexRitualChamber.name)
        d.giveMana(p1, Color.BLACK, 3)
        d.submitSuccess(CastSpell(p1, roomId, faceIndex = 0))
        d.bothPass()
        d.state.projectedState.getPower(soulrager) shouldBe 1
        d.state.projectedState.getToughness(soulrager) shouldBe 4

        // Unlock the other door ({3}{B}{B}). Now both doors of the one Room are unlocked = two
        // unlocked doors → the +3/+0 turns on.
        d.giveMana(p1, Color.BLACK, 5)
        d.submitSuccess(UnlockRoomDoor(p1, roomId, RoomFaceId("Ritual Chamber")))
        d.bothPass()
        d.state.projectedState.getPower(soulrager) shouldBe 4
        d.state.projectedState.getToughness(soulrager) shouldBe 4
    }
})
