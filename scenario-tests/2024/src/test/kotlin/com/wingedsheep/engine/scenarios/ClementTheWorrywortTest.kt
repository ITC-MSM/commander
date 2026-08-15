package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.mana.ManaSolver
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.ComponentContainer
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.SummoningSicknessComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.state.components.identity.OwnerComponent
import com.wingedsheep.engine.state.components.identity.PlayerComponent
import com.wingedsheep.engine.state.components.identity.LifeTotalComponent
import com.wingedsheep.mtg.sets.definitions.blb.BloomburrowSet
import com.wingedsheep.mtg.sets.definitions.por.PortalSet
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.effects.ManaRestriction
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class ClementTheWorrywortTest : FunSpec({

    val cardRegistry = CardRegistry().apply {
        register(BloomburrowSet.cards)
        register(BloomburrowSet.basicLands)
        register(PortalSet.cards)
        register(PortalSet.basicLands)
    }
    val manaSolver = ManaSolver(cardRegistry)

    fun createGameWithClement(): Triple<GameState, EntityId, EntityId> {
        val player1 = EntityId.generate()
        val clementId = EntityId.generate()

        val clementDef = cardRegistry.getCard("Clement, the Worrywort")!!

        // Build minimal game state with Clement on the battlefield
        var state = GameState(turnOrder = listOf(player1, EntityId.generate()))

        // Add player entity
        state = state.withEntity(player1, ComponentContainer.of(
            PlayerComponent("Player1"),
            LifeTotalComponent(20)
        ))

        // Add Clement to the battlefield (no summoning sickness)
        val clementContainer = ComponentContainer.of(
            CardComponent(
                cardDefinitionId = clementDef.name,
                name = clementDef.name,
                manaCost = clementDef.manaCost,
                typeLine = clementDef.typeLine,
                oracleText = clementDef.oracleText,
                baseStats = clementDef.creatureStats,
                baseKeywords = clementDef.keywords,
                colors = clementDef.colors,
                ownerId = player1
            ),
            ControllerComponent(player1),
            OwnerComponent(player1)
        )
        state = state.withEntity(clementId, clementContainer)
        state = state.addToZone(ZoneKey(player1, Zone.BATTLEFIELD), clementId)

        // Add a library card to prevent draw-from-empty
        val libraryCardId = EntityId.generate()
        val forestDef = cardRegistry.getCard("Forest")!!
        state = state.withEntity(libraryCardId, ComponentContainer.of(
            CardComponent(
                cardDefinitionId = forestDef.name,
                name = forestDef.name,
                manaCost = ManaCost.ZERO,
                typeLine = forestDef.typeLine,
                ownerId = player1
            ),
            OwnerComponent(player1)
        ))
        state = state.addToZone(ZoneKey(player1, Zone.LIBRARY), libraryCardId)

        return Triple(state, player1, clementId)
    }

    test("ManaSolver recognizes Clement as mana source via its own static grant") {
        val (state, player1, clementId) = createGameWithClement()

        val sources = manaSolver.findAvailableManaSources(state, player1)

        val clementSource = sources.find { it.entityId == clementId }
        clementSource shouldNotBe null
        clementSource!!.producesColors shouldContain Color.GREEN
        clementSource.producesColors shouldContain Color.BLUE
        clementSource.restriction shouldBe ManaRestriction.CreatureSpellsOnly
    }

    test("Clement with summoning sickness is not available as mana source") {
        val (baseState, player1, clementId) = createGameWithClement()

        // Add summoning sickness
        val state = baseState.updateEntity(clementId) { it.with(SummoningSicknessComponent) }

        val sources = manaSolver.findAvailableManaSources(state, player1)

        val clementSource = sources.find { it.entityId == clementId }
        clementSource shouldBe null
    }
})
