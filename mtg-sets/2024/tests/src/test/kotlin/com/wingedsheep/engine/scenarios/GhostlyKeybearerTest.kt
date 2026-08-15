package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.UnlockRoomDoor
import com.wingedsheep.engine.state.components.identity.RoomComponent
import com.wingedsheep.engine.state.components.identity.RoomFaceId
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardLayout
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

/**
 * Ghostly Keybearer ({3}{U} 3/3 Spirit, flying): "Whenever this creature deals combat damage to a
 * player, unlock a locked door of up to one target Room you control."
 *
 * Exercises the resolution-time "unlock a door" effect (CR 709.5f) and its "up to one target Room
 * you control with a locked door" targeting:
 *  - a locked Room is a legal target and its door is unlocked, firing the face's
 *    "When you unlock this door" trigger (CR 709.5h);
 *  - with no Room to unlock, the optional trigger resolves harmlessly (up-to-one);
 *  - a fully-unlocked Room is not a legal target (the locked-door restriction).
 */
class GhostlyKeybearerTest : FunSpec({

    // A Room whose locked face (Test Vault) draws a card when unlocked, so we can observe the
    // "When you unlock this door" trigger firing off the resolution-time unlock.
    val testRoom = card("Keybearer Hall // Keybearer Vault") {
        layout = CardLayout.SPLIT
        face("Keybearer Hall") {
            manaCost = "{2}{B}"
            typeLine = "Enchantment — Room"
            oracleText = "At the beginning of your end step, draw a card."
            triggeredAbility {
                trigger = Triggers.YourEndStep
                effect = Effects.DrawCards(1)
            }
        }
        face("Keybearer Vault") {
            manaCost = "{3}{B}{B}"
            typeLine = "Enchantment — Room"
            oracleText = "When you unlock this door, draw a card."
            triggeredAbility {
                trigger = Triggers.OnDoorUnlocked
                effect = Effects.DrawCards(1)
            }
        }
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(testRoom)
        driver.initMirrorMatch(
            deck = Deck.of("Swamp" to 20, "Grizzly Bears" to 20),
            startingLife = 20,
            skipMulligans = true,
        )
        return driver
    }

    /** Cast the left face so the Room is on the battlefield with the right door locked. */
    fun GameTestDriver.castLockedRoom(player: com.wingedsheep.sdk.model.EntityId): com.wingedsheep.sdk.model.EntityId {
        val roomId = putCardInHand(player, testRoom.name)
        giveMana(player, Color.BLACK, 3)
        submitSuccess(CastSpell(player, roomId, faceIndex = 0))
        bothPass()
        return roomId
    }

    test("combat damage unlocks a locked door of the target Room and fires its unlock trigger") {
        val driver = createDriver()
        val attacker = driver.player1
        val defender = driver.player2

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val roomId = driver.castLockedRoom(attacker)
        driver.state.getEntity(roomId)!!.get<RoomComponent>()!!.unlocked shouldBe
            setOf(RoomFaceId("Keybearer Hall"))

        val keybearer = driver.putCreatureOnBattlefield(attacker, "Ghostly Keybearer")
        driver.removeSummoningSickness(keybearer)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attacker, listOf(keybearer), defender)
        driver.bothPass()
        driver.declareNoBlockers(defender)
        driver.bothPass()

        // The combat-damage trigger asks for its (up-to-one) target. The locked Room is legal.
        val decision = driver.pendingDecision as ChooseTargetsDecision
        decision.legalTargets[0]!! shouldContain roomId

        val handBefore = driver.getHand(attacker).size
        driver.submitTargetSelection(attacker, listOf(roomId))
        // Resolve the unlock trigger, then the "When you unlock this door, draw a card" trigger.
        driver.bothPass()
        driver.bothPass()

        // The locked door is now unlocked (the Room is fully unlocked).
        val room = driver.state.getEntity(roomId)!!.get<RoomComponent>()!!
        room.isUnlocked(RoomFaceId("Keybearer Vault")) shouldBe true
        room.isFullyUnlocked shouldBe true

        // The face's unlock trigger drew a card.
        driver.getHand(attacker).size shouldBe handBefore + 1
        driver.assertLifeTotal(defender, 17)
    }

    test("up to one: with no Room to unlock, the trigger resolves harmlessly") {
        val driver = createDriver()
        val attacker = driver.player1
        val defender = driver.player2

        val keybearer = driver.putCreatureOnBattlefield(attacker, "Ghostly Keybearer")
        driver.removeSummoningSickness(keybearer)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attacker, listOf(keybearer), defender)
        driver.bothPass()
        driver.declareNoBlockers(defender)
        driver.bothPass()

        // No Room with a locked door exists, so the optional trigger has no legal target.
        // It must not get stuck demanding a target; if a decision is offered it allows zero.
        (driver.pendingDecision as? ChooseTargetsDecision)?.let { decision ->
            decision.legalTargets[0].orEmpty().shouldBe(emptyList())
            driver.submitTargetSelection(attacker, emptyList())
        }
        driver.bothPass()

        // Combat damage still landed; nothing else happened.
        driver.assertLifeTotal(defender, 17)
    }

    test("a fully-unlocked Room is not a legal target (locked-door restriction)") {
        val driver = createDriver()
        val attacker = driver.player1
        val defender = driver.player2

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val roomId = driver.castLockedRoom(attacker)

        // Unlock the second door via the special action so the Room is fully unlocked.
        driver.giveMana(attacker, Color.BLACK, 5)
        driver.submitSuccess(UnlockRoomDoor(attacker, roomId, RoomFaceId("Keybearer Vault")))
        driver.bothPass() // resolve the unlock-triggered draw
        driver.state.getEntity(roomId)!!.get<RoomComponent>()!!.isFullyUnlocked shouldBe true

        val keybearer = driver.putCreatureOnBattlefield(attacker, "Ghostly Keybearer")
        driver.removeSummoningSickness(keybearer)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attacker, listOf(keybearer), defender)
        driver.bothPass()
        driver.declareNoBlockers(defender)
        driver.bothPass()

        // The fully-unlocked Room is not offered as a target.
        (driver.pendingDecision as? ChooseTargetsDecision)?.let { decision ->
            decision.legalTargets[0].orEmpty() shouldNotContain roomId
            driver.submitTargetSelection(attacker, emptyList())
        }
        driver.bothPass()

        // The Room is unchanged and combat damage landed.
        driver.state.getEntity(roomId)!!.get<RoomComponent>()!!.isFullyUnlocked shouldBe true
        driver.assertLifeTotal(defender, 17)
    }
})
