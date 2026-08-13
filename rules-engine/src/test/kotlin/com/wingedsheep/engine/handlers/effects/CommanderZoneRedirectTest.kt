package com.wingedsheep.engine.handlers.effects

import com.wingedsheep.engine.state.ComponentContainer
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.CommanderComponent
import com.wingedsheep.engine.state.components.identity.OwnerComponent
import com.wingedsheep.sdk.core.CardType
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
 * Commander choices are not synchronous redirects. CR 903.9a is checked after a move to
 * graveyard/exile and CR 903.9b requires an owner choice before a move to hand/library, so this
 * helper must leave all commander destinations untouched.
 */
class CommanderZoneRedirectTest : FunSpec({

    val ownerId = EntityId.generate()
    val cmdrId = EntityId.generate()

    val commander = Format.Commander()

    fun stateWithCommander(format: Format, commanderZone: Zone): GameState {
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
                colors = setOf(com.wingedsheep.sdk.core.Color.RED),
                ownerId = ownerId,
                spellEffect = null,
            ),
            OwnerComponent(ownerId),
            CommanderComponent(ownerId = ownerId),
        )
        return GameState(format = format)
            .withEntity(ownerId, ComponentContainer.EMPTY)
            .withEntity(cmdrId, cardContainer)
            .addToZone(ZoneKey(ownerId, commanderZone), cmdrId)
            .copy(turnOrder = listOf(ownerId))
    }

    test("destroyed commander reaches the graveyard before the 903.9a SBA") {
        val state = stateWithCommander(commander, Zone.BATTLEFIELD)
        val result = ZoneMovementUtils.checkZoneChangeRedirect(
            state, cmdrId, Zone.BATTLEFIELD, Zone.GRAVEYARD,
        )
        result.destinationZone shouldBe Zone.GRAVEYARD
    }

    test("milled commander reaches the graveyard before the 903.9a SBA") {
        val state = stateWithCommander(commander, Zone.LIBRARY)
        val result = ZoneMovementUtils.checkZoneChangeRedirect(
            state, cmdrId, Zone.LIBRARY, Zone.GRAVEYARD,
        )
        result.destinationZone shouldBe Zone.GRAVEYARD
    }

    test("exiled commander reaches exile before the 903.9a SBA") {
        val state = stateWithCommander(commander, Zone.BATTLEFIELD)
        val result = ZoneMovementUtils.checkZoneChangeRedirect(
            state, cmdrId, Zone.BATTLEFIELD, Zone.EXILE,
        )
        result.destinationZone shouldBe Zone.EXILE
    }

    test("legacy diversion flag cannot silently replace a bounce") {
        val state = stateWithCommander(Format.Commander(alwaysDivertToCommand = true), Zone.BATTLEFIELD)
        val result = ZoneMovementUtils.checkZoneChangeRedirect(
            state, cmdrId, Zone.BATTLEFIELD, Zone.HAND,
        )
        result.destinationZone shouldBe Zone.HAND
    }

    test("commander leaving the command zone is not redirected back") {
        val state = stateWithCommander(commander, Zone.COMMAND)
        // The commander is on the stack heading toward the battlefield (cast resolution).
        val result = ZoneMovementUtils.checkZoneChangeRedirect(
            state, cmdrId, Zone.COMMAND, Zone.BATTLEFIELD,
        )
        result.destinationZone shouldBe Zone.BATTLEFIELD
    }

    test("default Commander format leaves the destination unchanged") {
        val state = stateWithCommander(
            Format.Commander(alwaysDivertToCommand = false),
            Zone.BATTLEFIELD,
        )
        val result = ZoneMovementUtils.checkZoneChangeRedirect(
            state, cmdrId, Zone.BATTLEFIELD, Zone.GRAVEYARD,
        )
        result.destinationZone shouldBe Zone.GRAVEYARD
    }

    test("Standard format does not redirect even with CommanderComponent attached") {
        val state = stateWithCommander(Format.Standard, Zone.BATTLEFIELD)
        val result = ZoneMovementUtils.checkZoneChangeRedirect(
            state, cmdrId, Zone.BATTLEFIELD, Zone.GRAVEYARD,
        )
        result.destinationZone shouldBe Zone.GRAVEYARD
    }
})
