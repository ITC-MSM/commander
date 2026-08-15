package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.identity.RoomComponent
import com.wingedsheep.engine.state.components.identity.RoomFaceId
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dsk.cards.MirrorRoomFracturedRealm
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario for `Mirror Room // Fractured Realm` (DSK 67), a split-layout Room (CR 709.5).
 *
 * Mirror Room {2}{U}    — "When you unlock this door, create a token that's a copy of target
 *                          creature you control, except it's a Reflection in addition to its
 *                          other creature types." Casting the half enters it unlocked, firing
 *                          the trigger.
 * Fractured Realm {5}{U}{U} — "If a triggered ability of a permanent you control triggers, that
 *                          ability triggers an additional time." (any permanent you control,
 *                          including Fractured Realm itself — excludeSelf = false).
 */
class MirrorRoomFracturedRealmTest : FunSpec({

    // A creature with an ETB draw trigger, used to observe Fractured Realm's doubling.
    val drawer = card("Test ETB Drawer") {
        manaCost = "{2}"
        typeLine = "Creature — Human"
        power = 2
        toughness = 2
        oracleText = "When Test ETB Drawer enters, draw a card."
        triggeredAbility {
            trigger = Triggers.EntersBattlefield
            effect = Effects.DrawCards(1)
        }
    }

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(MirrorRoomFracturedRealm, drawer))
        d.initMirrorMatch(
            deck = Deck.of("Island" to 20, "Grizzly Bears" to 20),
            skipMulligans = true,
        )
        return d
    }

    test("Mirror Room unlocks its door and tokens a Reflection copy of a creature you control") {
        val d = driver()
        val p1 = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // A creature to copy.
        val bear = d.putCreatureOnBattlefield(p1, "Grizzly Bears")

        // Cast Mirror Room ({2}{U}, face 0). The cast face enters unlocked, firing the trigger.
        val roomId = d.putCardInHand(p1, MirrorRoomFracturedRealm.name)
        d.giveMana(p1, Color.BLUE, 1)
        d.giveColorlessMana(p1, 2)
        d.submitSuccess(CastSpell(p1, roomId, faceIndex = 0))
        d.bothPass()

        val room = d.state.getEntity(roomId)?.get<RoomComponent>()
        room shouldNotBe null
        room!!.unlocked shouldBe setOf(RoomFaceId("Mirror Room"))

        // The "When you unlock this door" trigger asks for its target creature.
        d.submitTargetSelection(p1, listOf(bear))
        d.bothPass()

        // Two Grizzly Bears now: the original plus a token copy.
        val bears = d.state.getBattlefield().filter {
            d.getController(it) == p1 && d.getCardName(it) == "Grizzly Bears"
        }
        bears.size shouldBe 2

        // The token is the new entity and is a Reflection in addition to its other types.
        val token = bears.first { it != bear }
        val projected = d.state.projectedState
        projected.hasSubtype(token, "Reflection") shouldBe true
        projected.hasSubtype(token, "Bear") shouldBe true
    }

    test("Fractured Realm doubles a creature's ETB trigger — controller draws twice") {
        val d = driver()
        val p1 = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Cast Fractured Realm ({5}{U}{U}, face 1) so its static doubler is installed.
        val roomId = d.putCardInHand(p1, MirrorRoomFracturedRealm.name)
        d.giveMana(p1, Color.BLUE, 2)
        d.giveColorlessMana(p1, 5)
        d.submitSuccess(CastSpell(p1, roomId, faceIndex = 1))
        d.bothPass()
        d.state.getEntity(roomId)!!.get<RoomComponent>()!!.unlocked shouldBe setOf(RoomFaceId("Fractured Realm"))

        // A creature with an ETB draw trigger: the trigger should fire an additional time.
        val creature = d.putCardInHand(p1, "Test ETB Drawer")
        d.giveColorlessMana(p1, 2)
        val before = d.getHandSize(p1)
        d.castSpell(p1, creature).isSuccess shouldBe true
        var guard = 0
        while (d.state.stack.isNotEmpty() && guard++ < 20) {
            d.bothPass()
        }

        // Casting moved the card from hand, then two ETB draws (original + additional firing):
        // net delta = -1 (cast) + 2 (draws) = +1.
        (d.getHandSize(p1) - before) shouldBe 1
    }
})
