package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dsk.cards.FuneralRoomAwakeningHall
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario for `Funeral Room // Awakening Hall` (DSK 100), a split-layout Room (CR 709.5).
 *
 * Funeral Room {2}{B}   — "Whenever a creature you control dies, each opponent loses 1 life and you
 *                          gain 1 life."
 * Awakening Hall {6}{B}{B} — "When you unlock this door, return all creature cards from your
 *                            graveyard to the battlefield."
 */
class FuneralRoomAwakeningHallTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        d.registerCard(FuneralRoomAwakeningHall)
        d.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true, startingLife = 20)
        return d
    }

    fun GameTestDriver.bearsOnBattlefield(playerId: EntityId): Int =
        getCreatures(playerId).count { getCardName(it) == "Grizzly Bears" }

    test("Funeral Room drains each opponent and gains you life when your creature dies") {
        val d = driver()
        val me = d.activePlayer!!
        val opp = d.getOpponent(me)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Cast Funeral Room (face 0, {2}{B}); it enters unlocked, arming the dies trigger.
        val roomId = d.putCardInHand(me, FuneralRoomAwakeningHall.name)
        d.giveMana(me, Color.BLACK, 3)
        d.submitSuccess(CastSpell(me, roomId, faceIndex = 0))
        d.bothPass()

        // A creature I control dies.
        val bears = d.putCreatureOnBattlefield(me, "Grizzly Bears")
        val bolt = d.putCardInHand(me, "Lightning Bolt")
        d.giveMana(me, Color.RED, 1)
        val myLifeBefore = d.getLifeTotal(me)
        val oppLifeBefore = d.getLifeTotal(opp)
        d.castSpell(me, bolt, listOf(bears))
        d.bothPass() // resolve Bolt → Bears dies → Funeral Room trigger on stack
        d.bothPass() // resolve the drain trigger

        d.getLifeTotal(opp) shouldBe (oppLifeBefore - 1)
        d.getLifeTotal(me) shouldBe (myLifeBefore + 1)
    }

    test("Awakening Hall returns all creature cards from your graveyard to the battlefield") {
        val d = driver()
        val me = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Three creature cards (and a noncreature) in my graveyard.
        d.putCardInGraveyard(me, "Grizzly Bears")
        d.putCardInGraveyard(me, "Grizzly Bears")
        d.putCardInGraveyard(me, "Centaur Courser")
        d.putCardInGraveyard(me, "Lightning Bolt") // noncreature — stays put

        // Cast Awakening Hall (face 1, {6}{B}{B}); entering unlocked fires "when you unlock this door".
        val roomId = d.putCardInHand(me, FuneralRoomAwakeningHall.name)
        d.giveMana(me, Color.BLACK, 8)
        d.submitSuccess(CastSpell(me, roomId, faceIndex = 1))
        // Resolve the Room spell onto the battlefield, then its "when you unlock this door" trigger.
        var guard = 0
        while ((d.state.stack.isNotEmpty() || d.pendingDecision != null) && guard++ < 10) d.bothPass()

        // All three creatures are back on the battlefield; the instant remains in the graveyard.
        d.bearsOnBattlefield(me) shouldBe 2
        d.getCreatures(me).count { d.getCardName(it) == "Centaur Courser" } shouldBe 1
        d.getGraveyard(me).mapNotNull { d.getCardName(it) } shouldBe listOf("Lightning Bolt")
    }
})
