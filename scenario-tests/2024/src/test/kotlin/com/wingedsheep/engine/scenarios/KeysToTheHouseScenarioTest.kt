package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.handlers.continuations.entityIdToChosenTarget
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.RoomComponent
import com.wingedsheep.engine.state.components.identity.RoomFaceId
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dsk.cards.KeysToTheHouse
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardLayout
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.TimingRule
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Keys to the House (DSK #251) — {1} Artifact.
 *
 *  - "{1}, {T}, Sacrifice this artifact: Search your library for a basic land card, reveal it, put
 *    it into your hand, then shuffle."
 *  - "{3}, {T}, Sacrifice this artifact: Lock or unlock a door of target Room you control. Activate
 *    only as a sorcery."
 *
 * The lock/unlock mechanic itself is covered exhaustively by [RoomLockTest]; here we prove the card
 * wires both abilities — the basic-land tutor and the resolution-time lock/unlock mode choice on a
 * Room you control — and that the second ability is sorcery-speed.
 */
class KeysToTheHouseScenarioTest : FunSpec({

    val testRoom = card("Foyer // Cellar") {
        layout = CardLayout.SPLIT
        face("Foyer") {
            manaCost = "{1}{B}"
            typeLine = "Enchantment — Room"
            oracleText = "Foyer."
        }
        face("Cellar") {
            manaCost = "{2}{B}"
            typeLine = "Enchantment — Room"
            oracleText = "Cellar."
        }
    }

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        d.registerCard(KeysToTheHouse)
        d.registerCard(testRoom)
        d.initMirrorMatch(deck = Deck.of("Swamp" to 25, "Grizzly Bears" to 15), skipMulligans = true)
        return d
    }

    test("the second ability is sorcery-speed (CR: 'Activate only as a sorcery')") {
        KeysToTheHouse.activatedAbilities[1].timing shouldBe TimingRule.SorcerySpeed
    }

    test("first ability tutors a basic land to hand and sacrifices the artifact") {
        val d = driver()
        val p = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val keys = d.putPermanentOnBattlefield(p, KeysToTheHouse.name)
        d.giveMana(p, Color.BLACK, 1)

        d.submitSuccess(
            ActivateAbility(playerId = p, sourceId = keys, abilityId = KeysToTheHouse.activatedAbilities[0].id)
        )
        d.bothPass() // resolve the ability off the stack

        // The mandatory search surfaces a card selection over the basic lands in the library.
        val search = d.pendingDecision as SelectCardsDecision
        val chosen = search.options.first()
        d.submitCardSelection(p, listOf(chosen))

        // The tutored basic land is now in hand and Keys was sacrificed as a cost.
        d.getHand(p).contains(chosen) shouldBe true
        d.state.getEntity(chosen)?.get<CardComponent>()?.name shouldBe "Swamp"
        d.state.getZone(ZoneKey(p, Zone.GRAVEYARD)).contains(keys) shouldBe true
        d.state.getZone(ZoneKey(p, Zone.BATTLEFIELD)).contains(keys) shouldBe false
    }

    test("second ability unlocks a door of a Room you control via the lock/unlock mode choice") {
        val d = driver()
        val p = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Cast Foyer → Foyer unlocked, Cellar locked.
        val roomId = d.putCardInHand(p, testRoom.name)
        d.giveMana(p, Color.BLACK, 2)
        d.submitSuccess(CastSpell(p, roomId, faceIndex = 0))
        d.bothPass()
        d.state.getEntity(roomId)!!.get<RoomComponent>()!!.unlocked shouldBe setOf(RoomFaceId("Foyer"))

        val keys = d.putPermanentOnBattlefield(p, KeysToTheHouse.name)
        d.giveMana(p, Color.BLACK, 3)
        d.submitSuccess(
            ActivateAbility(
                playerId = p,
                sourceId = keys,
                abilityId = KeysToTheHouse.activatedAbilities[1].id,
                targets = listOf(entityIdToChosenTarget(d.state, roomId)),
            )
        )
        d.bothPass()

        // Resolution offers "Lock a door" / "Unlock a door"; choose unlock.
        val mode = d.pendingDecision as ChooseOptionDecision
        mode.options shouldBe listOf("Lock a door", "Unlock a door")
        d.submitDecision(p, OptionChosenResponse(mode.id, 1))

        // Cellar (the only locked door) is now unlocked, and Keys was sacrificed.
        d.state.getEntity(roomId)!!.get<RoomComponent>()!!.unlocked shouldBe
            setOf(RoomFaceId("Foyer"), RoomFaceId("Cellar"))
        d.state.getZone(ZoneKey(p, Zone.GRAVEYARD)).contains(keys) shouldBe true
    }
})
