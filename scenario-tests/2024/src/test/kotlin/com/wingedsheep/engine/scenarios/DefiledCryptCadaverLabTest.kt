package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.UnlockRoomDoor
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.RoomComponent
import com.wingedsheep.engine.state.components.identity.RoomFaceId
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dsk.cards.DefiledCryptCadaverLab
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario for `Defiled Crypt // Cadaver Lab` (DSK 91), a split-layout Room (CR 709.5).
 *
 * Defiled Crypt {3}{B} — "Whenever one or more cards leave your graveyard, create a 2/2 black
 *                         Horror enchantment creature token. This ability triggers only once each turn."
 * Cadaver Lab {B}      — "When you unlock this door, return target creature card from your
 *                         graveyard to your hand."
 */
class DefiledCryptCadaverLabTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        d.registerCard(DefiledCryptCadaverLab)
        d.initMirrorMatch(
            deck = Deck.of("Swamp" to 20, "Grizzly Bears" to 20),
            skipMulligans = true,
        )
        return d
    }

    /** Count Horror creature tokens controlled by [playerId]. */
    fun GameTestDriver.horrorTokenCount(playerId: com.wingedsheep.sdk.model.EntityId): Int =
        getCreatures(playerId).count { id ->
            state.getEntity(id)?.get<CardComponent>()?.typeLine?.subtypes
                ?.any { it.value.equals("Horror", ignoreCase = true) } == true
        }

    test("casting Cadaver Lab returns a target creature card from your graveyard to your hand") {
        val d = driver()
        val p1 = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val bears = d.putCardInGraveyard(p1, "Grizzly Bears")

        // Cast Cadaver Lab ({B}, face 1). The cast face enters unlocked, firing its trigger.
        val roomId = d.putCardInHand(p1, DefiledCryptCadaverLab.name)
        d.giveMana(p1, Color.BLACK, 1)
        d.submitSuccess(CastSpell(p1, roomId, faceIndex = 1))
        d.bothPass()

        val room = d.state.getEntity(roomId)?.get<RoomComponent>()
        room shouldNotBe null
        room!!.unlocked shouldBe setOf(RoomFaceId("Cadaver Lab"))

        // The "When you unlock this door" trigger asks for its target creature card.
        val handBefore = d.getHand(p1).size
        d.submitTargetSelection(p1, listOf(bears))
        d.bothPass()

        d.getGraveyard(p1).contains(bears) shouldBe false
        d.getHand(p1).contains(bears) shouldBe true
        d.getHand(p1).size shouldBe handBefore + 1
    }

    test("Defiled Crypt makes one 2/2 Horror enchantment token when one or more cards leave your graveyard") {
        val d = driver()
        val p1 = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Two creature cards in the graveyard; both will leave in a single batch (one unlock).
        val bears1 = d.putCardInGraveyard(p1, "Grizzly Bears")
        d.putCardInGraveyard(p1, "Grizzly Bears")

        // Cast Defiled Crypt (face 0, {3}{B}) — its leave-graveyard trigger is now live.
        val roomId = d.putCardInHand(p1, DefiledCryptCadaverLab.name)
        d.giveMana(p1, Color.BLACK, 4)
        d.submitSuccess(CastSpell(p1, roomId, faceIndex = 0))
        d.bothPass()
        d.state.getEntity(roomId)!!.get<RoomComponent>()!!.unlocked shouldBe setOf(RoomFaceId("Defiled Crypt"))

        d.horrorTokenCount(p1) shouldBe 0

        // Unlock Cadaver Lab ({B}); the unlock special action pauses for Cadaver Lab's
        // "When you unlock this door" target. Its return makes a card leave the graveyard,
        // which fires Defiled Crypt's batching leave-graveyard token trigger.
        d.giveMana(p1, Color.BLACK, 1)
        d.submit(UnlockRoomDoor(p1, roomId, RoomFaceId("Cadaver Lab")))
        // Cadaver Lab's unlock trigger targets a graveyard creature card.
        d.submitTargetSelection(p1, listOf(bears1))
        d.bothPass() // resolve Cadaver Lab's return; the card leaving fires Defiled Crypt's trigger
        d.bothPass() // resolve Defiled Crypt's leave-graveyard token trigger

        d.state.getEntity(roomId)!!.get<RoomComponent>()!!.isFullyUnlocked shouldBe true
        d.getHand(p1).contains(bears1) shouldBe true

        // Exactly one 2/2 Horror enchantment token entered (batching trigger fires once per batch).
        d.horrorTokenCount(p1) shouldBe 1
        val token = d.getCreatures(p1).first { id ->
            d.state.getEntity(id)?.get<CardComponent>()?.typeLine?.subtypes
                ?.any { it.value.equals("Horror", ignoreCase = true) } == true
        }
        val tokenCard = d.state.getEntity(token)!!.get<CardComponent>()!!
        tokenCard.typeLine.isEnchantment shouldBe true
        tokenCard.typeLine.isCreature shouldBe true
    }
})
