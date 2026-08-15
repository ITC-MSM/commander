package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.identity.RoomComponent
import com.wingedsheep.engine.state.components.identity.RoomFaceId
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dsk.cards.GlassworksShatteredYard
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * End-to-end scenario for `Glassworks // Shattered Yard` (DSK 137), a mono-red Room.
 *
 * Glassworks {2}{R}: "When you unlock this door, this Room deals 4 damage to target creature an
 * opponent controls."
 * Shattered Yard {4}{R}: "At the beginning of your end step, this Room deals 1 damage to each
 * opponent."
 */
class GlassworksShatteredYardScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        d.registerCard(GlassworksShatteredYard)
        d.initMirrorMatch(
            deck = Deck.of(
                "Mountain" to 20,
                "Grizzly Bears" to 10,
            ),
            skipMulligans = true,
        )
        return d
    }

    test("unlocking Glassworks deals 4 damage to a target creature an opponent controls") {
        val d = driver()
        val p1 = d.activePlayer!!
        val p2 = d.getOpponent(p1)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Opponent controls a 2/2 to be the damage target; 4 damage is lethal.
        val bears = d.putPermanentOnBattlefield(p2, "Grizzly Bears")

        // Cast Glassworks (face 0, {2}{R}); it enters unlocked, firing the unlock trigger.
        val roomId = d.putCardInHand(p1, GlassworksShatteredYard.name)
        d.giveMana(p1, Color.RED, 3)
        d.submitSuccess(CastSpell(p1, roomId, faceIndex = 0))
        // Resolve the Room spell; it enters unlocked and the "when you unlock this door" trigger
        // goes on the stack, prompting for its target.
        d.bothPass()

        // The unlock trigger targets a creature an opponent controls.
        d.submitTargetSelection(p1, listOf(bears))
        d.bothPass()

        val room = d.state.getEntity(roomId)?.get<RoomComponent>()
        room shouldNotBe null
        room!!.unlocked shouldBe setOf(RoomFaceId("Glassworks"))

        // 4 damage -> the 2/2 dies (moves to its owner's graveyard).
        d.findPermanent(p2, "Grizzly Bears") shouldBe null
        (bears in d.getGraveyard(p2)) shouldBe true
    }

    test("Shattered Yard deals 1 damage to each opponent at the beginning of your end step") {
        val d = driver()
        val p1 = d.activePlayer!!
        val p2 = d.getOpponent(p1)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Cast Shattered Yard (face 1, {4}{R}).
        val roomId = d.putCardInHand(p1, GlassworksShatteredYard.name)
        d.giveMana(p1, Color.RED, 5)
        d.submitSuccess(CastSpell(p1, roomId, faceIndex = 1))
        d.bothPass()

        val room = d.state.getEntity(roomId)?.get<RoomComponent>()!!
        room.unlocked shouldBe setOf(RoomFaceId("Shattered Yard"))

        val opponentLifeBefore = d.getLifeTotal(p2)

        // End step: the "deals 1 damage to each opponent" trigger fires.
        d.passPriorityUntil(Step.END)
        d.bothPass()

        d.getLifeTotal(p2) shouldBe opponentLifeBefore - 1
    }
})
