package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.battlefield.AttachmentsComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.big.cards.SwordOfWealthAndPower
import com.wingedsheep.mtg.sets.tokens.PredefinedTokens
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Sword of Wealth and Power — {3} Artifact — Equipment
 *
 * "Equipped creature gets +2/+2 and has protection from instants and from sorceries.
 *  Whenever equipped creature deals combat damage to a player, create a Treasure token. When you
 *  next cast an instant or sorcery spell this turn, copy that spell. You may choose new targets
 *  for the copy.
 *  Equip {2}"
 */
class SwordOfWealthAndPowerScenarioTest : FunSpec({

    val projector = StateProjector()

    fun GameTestDriver.putEquipmentAttached(
        playerId: EntityId,
        cardName: String,
        targetCreatureId: EntityId
    ): EntityId {
        val equipmentId = putPermanentOnBattlefield(playerId, cardName)
        var newState = state.updateEntity(equipmentId) { c ->
            c.with(AttachedToComponent(targetCreatureId))
        }
        val existing = newState.getEntity(targetCreatureId)
            ?.get<AttachmentsComponent>()?.attachedIds ?: emptyList()
        newState = newState.updateEntity(targetCreatureId) { c ->
            c.with(AttachmentsComponent(existing + equipmentId))
        }
        replaceState(newState)
        return equipmentId
    }

    fun GameTestDriver.countByName(playerId: EntityId, name: String): Int =
        getPermanents(playerId).count { state.getEntity(it)?.get<CardComponent>()?.name == name }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(SwordOfWealthAndPower, PredefinedTokens.Treasure))
        return driver
    }

    test("equipped creature gets +2/+2") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)

        val me = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val creature = driver.putCreatureOnBattlefield(me, "Grizzly Bears") // 2/2
        driver.putEquipmentAttached(me, "Sword of Wealth and Power", creature)

        val projected = projector.project(driver.state)
        projected.getPower(creature) shouldBe 4
        projected.getToughness(creature) shouldBe 4
    }

    test("equipped creature has protection from instants — can't be targeted by an instant") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)

        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val equipped = driver.putCreatureOnBattlefield(me, "Grizzly Bears")
        driver.putEquipmentAttached(me, "Sword of Wealth and Power", equipped)

        driver.passPriority(me)

        // Opponent's Lightning Bolt (instant) can't target the equipped creature.
        val bolt = driver.putCardInHand(opp, "Lightning Bolt")
        driver.giveMana(opp, Color.RED, 1)
        val result = driver.castSpell(opp, bolt, listOf(equipped))
        result.isSuccess shouldBe false
    }

    test("equipped creature has protection from sorceries — can't be targeted by a sorcery") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40, "Swamp" to 40), startingLife = 20)

        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val equipped = driver.putCreatureOnBattlefield(me, "Grizzly Bears")
        driver.putEquipmentAttached(me, "Sword of Wealth and Power", equipped)

        driver.passPriority(me)

        // Doom Blade is a sorcery in TestCards ("Destroy target creature.").
        val doomBlade = driver.putCardInHand(opp, "Doom Blade")
        driver.giveMana(opp, Color.BLACK, 2)
        val result = driver.castSpell(opp, doomBlade, listOf(equipped))
        result.isSuccess shouldBe false
    }

    test("an unequipped creature can still be targeted by an instant") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)

        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val equipped = driver.putCreatureOnBattlefield(me, "Grizzly Bears")
        driver.putEquipmentAttached(me, "Sword of Wealth and Power", equipped)
        val unequipped = driver.putCreatureOnBattlefield(me, "Grizzly Bears")

        driver.passPriority(me)

        val bolt = driver.putCardInHand(opp, "Lightning Bolt")
        driver.giveMana(opp, Color.RED, 1)
        val result = driver.castSpell(opp, bolt, listOf(unequipped))
        result.isSuccess shouldBe true
    }

    test("dealing combat damage to a player creates a Treasure token") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)

        val attacker = driver.player1
        val defender = driver.player2

        val equipped = driver.putCreatureOnBattlefield(attacker, "Grizzly Bears")
        driver.removeSummoningSickness(equipped)
        driver.putEquipmentAttached(attacker, "Sword of Wealth and Power", equipped)
        driver.countByName(attacker, "Treasure") shouldBe 0

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attacker, listOf(equipped), defender)
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)

        var safety = 0
        while (driver.countByName(attacker, "Treasure") == 0 && safety++ < 20) driver.bothPass()

        driver.countByName(attacker, "Treasure") shouldBe 1
    }
})
