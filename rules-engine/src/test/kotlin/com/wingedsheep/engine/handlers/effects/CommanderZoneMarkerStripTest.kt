package com.wingedsheep.engine.handlers.effects

import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.ComponentContainer
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.CommanderComponent
import com.wingedsheep.engine.state.components.identity.CommanderZoneChoiceAskedComponent
import com.wingedsheep.engine.state.components.identity.OwnerComponent
import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Format
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Supertype
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.CreatureStats
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Every commander zone transition must clear the CR 903.9a "already asked this stay" marker
 * so the next entry into a non-command zone produces a fresh prompt.
 */
class CommanderZoneMarkerStripTest : FunSpec({
    val zones = ZoneTransitionService(CardRegistry())

    val ownerId = EntityId.generate()
    val cmdrId = EntityId.generate()

    fun stateWithAskedCommanderIn(zone: Zone): GameState {
        val cardContainer = ComponentContainer.of(
            CardComponent(
                cardDefinitionId = "Test Commander",
                name = "Test Commander",
                manaCost = ManaCost.parse("{2}{R}"),
                typeLine = TypeLine(
                    supertypes = setOf(Supertype.LEGENDARY),
                    cardTypes = setOf(CardType.CREATURE),
                    subtypes = setOf(Subtype("Human")),
                ),
                oracleText = "",
                baseStats = CreatureStats(2, 2),
                colors = setOf(Color.RED),
                ownerId = ownerId,
                spellEffect = null,
            ),
            OwnerComponent(ownerId),
            CommanderComponent(ownerId = ownerId),
            CommanderZoneChoiceAskedComponent,
        )
        return GameState(format = Format.Commander())
            .withEntity(ownerId, ComponentContainer.EMPTY)
            .withEntity(cmdrId, cardContainer)
            .addToZone(ZoneKey(ownerId, zone), cmdrId)
            .copy(turnOrder = listOf(ownerId))
    }

    test("moving a commander from graveyard to command zone strips the asked marker") {
        val state = stateWithAskedCommanderIn(Zone.GRAVEYARD)
        state.getEntity(cmdrId)!!.has<CommanderZoneChoiceAskedComponent>() shouldBe true

        val result = zones.moveToZone(state, cmdrId, Zone.COMMAND)
        result.state.getEntity(cmdrId)!!.has<CommanderZoneChoiceAskedComponent>() shouldBe false
    }

    test("moving a commander from exile to hand also strips the marker") {
        val state = stateWithAskedCommanderIn(Zone.EXILE)
        val result = zones.moveToZone(state, cmdrId, Zone.HAND)
        result.state.getEntity(cmdrId)!!.has<CommanderZoneChoiceAskedComponent>() shouldBe false
    }

    test("the strip happens even when the destination is yet another non-command zone") {
        // E.g. graveyard → exile via Tormod's-Crypt-style effect: SBA should re-ask on the
        // new entry, so the marker must not survive the trip.
        val state = stateWithAskedCommanderIn(Zone.GRAVEYARD)
        val result = zones.moveToZone(state, cmdrId, Zone.EXILE)
        result.state.getEntity(cmdrId)!!.has<CommanderZoneChoiceAskedComponent>() shouldBe false
    }

    test("non-commander entities aren't affected by the strip path") {
        // Add a regular card with the marker (a degenerate, never-happens setup) and confirm
        // the strip is gated behind CommanderComponent — no spurious component removal on
        // ordinary zone transitions.
        val regularId = EntityId.generate()
        val regular = ComponentContainer.of(
            CardComponent(
                cardDefinitionId = "Plain Card",
                name = "Plain Card",
                manaCost = ManaCost.parse("{1}"),
                typeLine = TypeLine(cardTypes = setOf(CardType.CREATURE)),
                oracleText = "",
                baseStats = CreatureStats(1, 1),
                colors = emptySet(),
                ownerId = ownerId,
                spellEffect = null,
            ),
            OwnerComponent(ownerId),
            // Synthetic — non-commanders never legitimately carry this, but the test
            // is verifying our strip is correctly gated.
            CommanderZoneChoiceAskedComponent,
        )
        val state = GameState(format = Format.Commander())
            .withEntity(ownerId, ComponentContainer.EMPTY)
            .withEntity(regularId, regular)
            .addToZone(ZoneKey(ownerId, Zone.GRAVEYARD), regularId)
            .copy(turnOrder = listOf(ownerId))

        val result = zones.moveToZone(state, regularId, Zone.EXILE)
        result.state.getEntity(regularId)!!.has<CommanderZoneChoiceAskedComponent>() shouldBe true
    }
})
