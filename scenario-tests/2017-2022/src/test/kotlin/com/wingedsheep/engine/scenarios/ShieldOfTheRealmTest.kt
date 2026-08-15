package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.ComponentContainer
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.battlefield.AttachmentsComponent
import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.state.components.battlefield.ReplacementEffectSourceComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.state.components.identity.OwnerComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dom.cards.ShieldOfTheRealm
import com.wingedsheep.sdk.scripting.PreventDamage
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Shield of the Realm (DOM #228).
 *
 * Shield of the Realm: {2}
 * Artifact — Equipment
 * If a source would deal damage to equipped creature, prevent 2 of that damage.
 * Equip {1}
 */
class ShieldOfTheRealmTest : FunSpec({

    val SturdyCreature = CardDefinition.creature(
        name = "Sturdy Creature",
        manaCost = ManaCost.parse("{2}{G}"),
        subtypes = setOf(Subtype("Beast")),
        power = 3,
        toughness = 5,
        oracleText = ""
    )

    val SmallCreature = CardDefinition.creature(
        name = "Small Creature",
        manaCost = ManaCost.parse("{W}"),
        subtypes = setOf(Subtype("Soldier")),
        power = 1,
        toughness = 1,
        oracleText = ""
    )

    fun GameTestDriver.putEquipmentOnBattlefield(
        playerId: EntityId,
        cardDef: CardDefinition,
        targetCreatureId: EntityId
    ): EntityId {
        val equipId = EntityId.generate()

        val cardComponent = CardComponent(
            cardDefinitionId = cardDef.name,
            name = cardDef.name,
            manaCost = cardDef.manaCost,
            typeLine = cardDef.typeLine,
            oracleText = cardDef.oracleText,
            baseStats = cardDef.creatureStats,
            baseKeywords = cardDef.keywords,
            baseFlags = cardDef.flags,
            colors = cardDef.colors,
            ownerId = playerId,
            spellEffect = cardDef.spellEffect
        )

        val runtimeEffects = cardDef.script.replacementEffects.filter {
            it is PreventDamage
        }

        var container = ComponentContainer.of(
            cardComponent,
            OwnerComponent(playerId),
            ControllerComponent(playerId),
            AttachedToComponent(targetCreatureId)
        )

        if (runtimeEffects.isNotEmpty()) {
            container = container.with(ReplacementEffectSourceComponent(runtimeEffects))
        }

        var newState = state.withEntity(equipId, container)

        val battlefieldZone = ZoneKey(playerId, Zone.BATTLEFIELD)
        newState = newState.addToZone(battlefieldZone, equipId)

        val existingAttachments = newState.getEntity(targetCreatureId)
            ?.get<AttachmentsComponent>()?.attachedIds ?: emptyList()
        newState = newState.updateEntity(targetCreatureId) { c ->
            c.with(AttachmentsComponent(existingAttachments + equipId))
        }

        replaceState(newState)
        return equipId
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(SturdyCreature, SmallCreature))
        return driver
    }

    test("prevents 2 combat damage to equipped creature") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Plains" to 20, "Mountain" to 20),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Put a 3/5 creature on our battlefield with Shield of the Realm
        val creature = driver.putCreatureOnBattlefield(activePlayer, "Sturdy Creature")
        driver.removeSummoningSickness(creature)
        driver.putEquipmentOnBattlefield(activePlayer, ShieldOfTheRealm, creature)

        // Opponent has a 3/3 creature
        val opponentCreature = driver.putCreatureOnBattlefield(opponent, "Centaur Courser")
        driver.removeSummoningSickness(opponentCreature)

        // We attack, opponent blocks
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(activePlayer, listOf(creature), opponent)
        driver.bothPass()
        driver.declareBlockers(opponent, mapOf(opponentCreature to listOf(creature)))
        driver.bothPass()

        // First strike step (no first strikers)
        driver.bothPass()

        // Combat damage - our creature should take 1 damage (3 - 2 prevented)
        driver.bothPass()

        val creatureDamage = driver.state.getEntity(creature)?.get<DamageComponent>()?.amount ?: 0
        creatureDamage shouldBe 1
    }

    test("prevents 2 non-combat (spell) damage to equipped creature") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Plains" to 20, "Mountain" to 20),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Put a 3/5 creature with Shield of the Realm
        val creature = driver.putCreatureOnBattlefield(activePlayer, "Sturdy Creature")
        driver.putEquipmentOnBattlefield(activePlayer, ShieldOfTheRealm, creature)

        // Deal 3 damage via Lightning Bolt
        driver.giveMana(activePlayer, Color.RED, 1)
        val bolt = driver.putCardInHand(activePlayer, "Lightning Bolt")
        driver.castSpellWithTargets(activePlayer, bolt, listOf(ChosenTarget.Permanent(creature)))
        driver.bothPass()

        // Should take 1 damage (3 - 2 prevented) — Shield prevents all damage, not just combat
        val damage = driver.state.getEntity(creature)?.get<DamageComponent>()?.amount ?: 0
        damage shouldBe 1
    }

    test("does not prevent damage to non-equipped creatures") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Plains" to 20, "Mountain" to 20),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Put creature A (3/5) with Shield, and creature B (3/5) without
        val creatureA = driver.putCreatureOnBattlefield(activePlayer, "Sturdy Creature")
        driver.removeSummoningSickness(creatureA)
        driver.putEquipmentOnBattlefield(activePlayer, ShieldOfTheRealm, creatureA)

        val creatureB = driver.putCreatureOnBattlefield(activePlayer, "Sturdy Creature")
        driver.removeSummoningSickness(creatureB)

        // Opponent has a 3/3 creature
        val opponentCreature = driver.putCreatureOnBattlefield(opponent, "Centaur Courser")
        driver.removeSummoningSickness(opponentCreature)

        // We attack with creature B (no Shield)
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(activePlayer, listOf(creatureB), opponent)
        driver.bothPass()

        // Opponent blocks
        driver.declareBlockers(opponent, mapOf(opponentCreature to listOf(creatureB)))
        driver.bothPass()

        // First strike step
        driver.bothPass()

        // Combat damage - creature B should take full 3 damage
        driver.bothPass()

        val creatureBDamage = driver.state.getEntity(creatureB)?.get<DamageComponent>()?.amount ?: 0
        creatureBDamage shouldBe 3
    }

    test("prevents only up to the damage amount when damage is less than 2") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Plains" to 20, "Mountain" to 20),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Put a 3/5 creature with Shield of the Realm
        val creature = driver.putCreatureOnBattlefield(activePlayer, "Sturdy Creature")
        driver.removeSummoningSickness(creature)
        driver.putEquipmentOnBattlefield(activePlayer, ShieldOfTheRealm, creature)

        // Opponent has a 1/1 creature
        val opponentCreature = driver.putCreatureOnBattlefield(opponent, "Small Creature")
        driver.removeSummoningSickness(opponentCreature)

        // We attack, opponent blocks with 1/1
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(activePlayer, listOf(creature), opponent)
        driver.bothPass()
        driver.declareBlockers(opponent, mapOf(opponentCreature to listOf(creature)))
        driver.bothPass()

        // First strike step
        driver.bothPass()

        // Combat damage - our creature should take 0 damage (1 damage, 2 prevented, floor is 0)
        driver.bothPass()

        val creatureDamage = driver.state.getEntity(creature)?.get<DamageComponent>()?.amount ?: 0
        creatureDamage shouldBe 0
    }
})
