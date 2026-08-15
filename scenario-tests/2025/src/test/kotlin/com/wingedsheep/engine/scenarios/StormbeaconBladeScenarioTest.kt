package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.battlefield.AttachmentsComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.mtg.sets.definitions.tdm.cards.StormbeaconBlade
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Stormbeacon Blade.
 *
 * Stormbeacon Blade ({1}{W}): Artifact — Equipment
 *   "Equipped creature gets +3/+0.
 *    Whenever equipped creature attacks, draw a card if you control three or more attacking creatures.
 *    Equip {2}"
 *
 * Exercises the new [com.wingedsheep.sdk.dsl.Conditions.YouControlAtLeast] filtered-count
 * condition over attacking creatures: the attack trigger always fires, but the draw is gated on
 * three or more of your creatures attacking when it resolves.
 */
class StormbeaconBladeScenarioTest : FunSpec({

    val Bear = CardDefinition.creature(
        name = "Stormbeacon Bear",
        manaCost = ManaCost.parse("{1}{G}"),
        subtypes = setOf(Subtype("Bear")),
        power = 2,
        toughness = 2,
        oracleText = ""
    )

    fun GameTestDriver.putEquipmentAttached(
        playerId: EntityId,
        cardName: String,
        targetCreatureId: EntityId
    ): EntityId {
        val equipmentId = putPermanentOnBattlefield(playerId, cardName)
        var newState = state.updateEntity(equipmentId) { c -> c.with(AttachedToComponent(targetCreatureId)) }
        val existing = newState.getEntity(targetCreatureId)
            ?.get<AttachmentsComponent>()?.attachedIds ?: emptyList()
        newState = newState.updateEntity(targetCreatureId) { c ->
            c.with(AttachmentsComponent(existing + equipmentId))
        }
        replaceState(newState)
        return equipmentId
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(Bear, StormbeaconBlade))
        return driver
    }

    val stateProjector = StateProjector()

    test("equipped creature gets +3/+0") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val bear = driver.putCreatureOnBattlefield(player, "Stormbeacon Bear")
        driver.putEquipmentAttached(player, "Stormbeacon Blade", bear)

        val projected = stateProjector.project(driver.state)
        projected.getPower(bear) shouldBe 5   // 2 + 3
        projected.getToughness(bear) shouldBe 2 // unchanged
    }

    test("draws a card when three or more creatures attack") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        val defender = driver.getOpponent(player)

        val equipped = driver.putCreatureOnBattlefield(player, "Stormbeacon Bear")
        val bear2 = driver.putCreatureOnBattlefield(player, "Stormbeacon Bear")
        val bear3 = driver.putCreatureOnBattlefield(player, "Stormbeacon Bear")
        listOf(equipped, bear2, bear3).forEach { driver.removeSummoningSickness(it) }
        driver.putEquipmentAttached(player, "Stormbeacon Blade", equipped)

        val handBefore = driver.getHandSize(player)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(player, listOf(equipped, bear2, bear3), defender)
        // Resolve the attack trigger.
        repeat(8) { driver.bothPass() }

        driver.getHandSize(player) shouldBe handBefore + 1
    }

    test("does not draw when fewer than three creatures attack") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        val defender = driver.getOpponent(player)

        val equipped = driver.putCreatureOnBattlefield(player, "Stormbeacon Bear")
        val bear2 = driver.putCreatureOnBattlefield(player, "Stormbeacon Bear")
        listOf(equipped, bear2).forEach { driver.removeSummoningSickness(it) }
        driver.putEquipmentAttached(player, "Stormbeacon Blade", equipped)

        val handBefore = driver.getHandSize(player)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(player, listOf(equipped, bear2), defender)
        repeat(8) { driver.bothPass() }

        // Only two attackers — the intervening-if fails, so no card is drawn.
        driver.getHandSize(player) shouldBe handBefore
    }
})
