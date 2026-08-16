package com.wingedsheep.engine.handlers.effects.library

import com.wingedsheep.engine.core.ZoneChangeEvent
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.effects.EffectExecutorRegistry
import com.wingedsheep.engine.handlers.effects.ZoneTransitionService
import com.wingedsheep.engine.handlers.effects.zones.ExileOpponentsGraveyardsExecutor
import com.wingedsheep.engine.mechanics.sba.zone.TokensInWrongZonesCheck
import com.wingedsheep.engine.state.ComponentContainer
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.LinkedExileComponent
import com.wingedsheep.engine.state.components.battlefield.SuspendedComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.state.components.identity.ExiledFromZoneComponent
import com.wingedsheep.engine.state.components.identity.OwnerComponent
import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.effects.ExileOpponentsGraveyardsEffect
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Engine coverage for `CardDestination.ToZoneExiledFrom` — the CR 610.3 "return the object to its
 * previous zone" destination — and for the `ExiledFromZoneComponent` stamp that backs it.
 *
 * Every case routes the card into exile through [ZoneTransitionService], the real exile path, so
 * these tests prove the stamp is written *and* read; hand-stamping the component would only test
 * the read half. The last two tests pin the two pre-existing fixed-destination recipes so a
 * regression there — them accidentally honouring the origin zone — shows up here.
 */
class ReturnLinkedExileToZoneExiledFromTest : FunSpec({

    val ownerId = EntityId.generate()
    val casterId = EntityId.generate()
    val sourceId = EntityId.generate()

    fun cardComponent(name: String, owner: EntityId) = CardComponent(
        cardDefinitionId = name,
        name = name,
        manaCost = ManaCost(emptyList()),
        typeLine = TypeLine(cardTypes = setOf(CardType.CREATURE)),
        ownerId = owner
    )

    /** A game with two players and a linked-exile source controlled by [casterId]. */
    fun baseState(): GameState = GameState(turnOrder = listOf(casterId, ownerId))
        .withEntity(casterId, ComponentContainer())
        .withEntity(ownerId, ComponentContainer())
        .withEntity(
            sourceId,
            ComponentContainer()
                .with(cardComponent("Source", casterId))
                .with(ControllerComponent(casterId))
        )

    /**
     * Put a freshly created card into [from] (owned by [ownerId]), then exile it the way the engine
     * does, and link it to the source. Returns the new state and the card's id.
     */
    fun exileFrom(state: GameState, from: Zone, name: String, isToken: Boolean = false): Pair<GameState, EntityId> {
        val cardId = EntityId.generate()
        var container = ComponentContainer()
            .with(cardComponent(name, ownerId))
            .with(OwnerComponent(ownerId))
        if (from == Zone.BATTLEFIELD) container = container.with(ControllerComponent(ownerId))
        if (isToken) container = container.with(TokenComponent)

        var newState = state.withEntity(cardId, container)
        newState = newState.addToZone(ZoneKey(ownerId, from), cardId)
        newState = ZoneTransitionService.moveToZone(newState, cardId, Zone.EXILE).state

        val linked = newState.getEntity(sourceId)?.get<LinkedExileComponent>()?.exiledIds ?: emptyList()
        newState = newState.updateEntity(sourceId) { c -> c.with(LinkedExileComponent(linked + cardId)) }
        return newState to cardId
    }

    fun context() = EffectContext(sourceId = sourceId, controllerId = casterId)

    fun registry(): EffectExecutorRegistry {
        val cardRegistry = com.wingedsheep.engine.registry.CardRegistry()
        val reg = EffectExecutorRegistry(cardRegistry = cardRegistry)
        reg.registerModule(LibraryExecutors(cardRegistry))
        return reg
    }

    // -------------------------------------------------------------------------------------------
    // The stamp itself
    // -------------------------------------------------------------------------------------------

    test("exiling stamps the zone the object came from, and leaving exile clears it") {
        val (exiled, cardId) = exileFrom(baseState(), Zone.GRAVEYARD, "Bear")
        exiled.getEntity(cardId)?.get<ExiledFromZoneComponent>()?.zone shouldBe Zone.GRAVEYARD

        val back = ZoneTransitionService.moveToZone(exiled, cardId, Zone.HAND).state
        back.getEntity(cardId)?.get<ExiledFromZoneComponent>().shouldBeNull()
    }

    // -------------------------------------------------------------------------------------------
    // One test per supported origin zone
    // -------------------------------------------------------------------------------------------

    test("a card exiled from the battlefield returns to the battlefield under its owner's control") {
        val (state, cardId) = exileFrom(baseState(), Zone.BATTLEFIELD, "Bear")

        val result = registry().execute(state, Patterns.Exile.returnLinkedExileToZoneExiledFrom(), context())

        result.isSuccess shouldBe true
        // CR 610.3c — returned under its OWNER's control, not the returning ability's controller.
        result.state.getZone(ZoneKey(ownerId, Zone.BATTLEFIELD)).shouldContainExactlyInAnyOrder(cardId)
        result.state.getZone(ZoneKey(casterId, Zone.BATTLEFIELD)).shouldBeEmpty()
        result.state.getEntity(cardId)?.get<ControllerComponent>()?.playerId shouldBe ownerId
        result.state.getZone(ZoneKey(ownerId, Zone.EXILE)).shouldBeEmpty()
    }

    test("a card exiled from a hand returns to its owner's hand") {
        val (state, cardId) = exileFrom(baseState(), Zone.HAND, "Bear")

        val result = registry().execute(state, Patterns.Exile.returnLinkedExileToZoneExiledFrom(), context())

        result.isSuccess shouldBe true
        result.state.getZone(ZoneKey(ownerId, Zone.HAND)).shouldContainExactlyInAnyOrder(cardId)
        result.state.getZone(ZoneKey(ownerId, Zone.BATTLEFIELD)).shouldBeEmpty()
        result.state.getZone(ZoneKey(casterId, Zone.HAND)).shouldBeEmpty()
    }

    test("a card exiled from a graveyard returns to its owner's graveyard") {
        val (state, cardId) = exileFrom(baseState(), Zone.GRAVEYARD, "Bear")

        val result = registry().execute(state, Patterns.Exile.returnLinkedExileToZoneExiledFrom(), context())

        result.isSuccess shouldBe true
        result.state.getZone(ZoneKey(ownerId, Zone.GRAVEYARD)).shouldContainExactlyInAnyOrder(cardId)
        result.state.getZone(ZoneKey(ownerId, Zone.BATTLEFIELD)).shouldBeEmpty()
    }

    test("a card exiled from a library returns to its owner's library") {
        val (state, cardId) = exileFrom(baseState(), Zone.LIBRARY, "Bear")

        val result = registry().execute(state, Patterns.Exile.returnLinkedExileToZoneExiledFrom(), context())

        result.isSuccess shouldBe true
        result.state.getZone(ZoneKey(ownerId, Zone.LIBRARY)).shouldContainExactlyInAnyOrder(cardId)
        result.state.getZone(ZoneKey(ownerId, Zone.BATTLEFIELD)).shouldBeEmpty()
    }

    test("a card exiled from the command zone returns to the command zone") {
        val (state, cardId) = exileFrom(baseState(), Zone.COMMAND, "Commander")

        val result = registry().execute(state, Patterns.Exile.returnLinkedExileToZoneExiledFrom(), context())

        result.isSuccess shouldBe true
        result.state.getZone(ZoneKey(ownerId, Zone.COMMAND)).shouldContainExactlyInAnyOrder(cardId)
        result.state.getZone(ZoneKey(ownerId, Zone.BATTLEFIELD)).shouldBeEmpty()
    }

    test("a card exiled from a sideboard returns to the sideboard") {
        val (state, cardId) = exileFrom(baseState(), Zone.SIDEBOARD, "Wished Card")

        val result = registry().execute(state, Patterns.Exile.returnLinkedExileToZoneExiledFrom(), context())

        result.isSuccess shouldBe true
        result.state.getZone(ZoneKey(ownerId, Zone.SIDEBOARD)).shouldContainExactlyInAnyOrder(cardId)
        result.state.getZone(ZoneKey(ownerId, Zone.BATTLEFIELD)).shouldBeEmpty()
    }

    test("one return splits a mixed pile back across the zones each card came from") {
        var state = baseState()
        val (s1, fromHand) = exileFrom(state, Zone.HAND, "Hand Card")
        state = s1
        val (s2, fromBattlefield) = exileFrom(state, Zone.BATTLEFIELD, "Creature")
        state = s2
        val (s3, fromGraveyard) = exileFrom(state, Zone.GRAVEYARD, "Dead Thing")
        state = s3

        val result = registry().execute(state, Patterns.Exile.returnLinkedExileToZoneExiledFrom(), context())

        result.isSuccess shouldBe true
        result.state.getZone(ZoneKey(ownerId, Zone.HAND)).shouldContainExactlyInAnyOrder(fromHand)
        result.state.getZone(ZoneKey(ownerId, Zone.BATTLEFIELD)).shouldContainExactlyInAnyOrder(fromBattlefield)
        result.state.getZone(ZoneKey(ownerId, Zone.GRAVEYARD)).shouldContainExactlyInAnyOrder(fromGraveyard)
        result.state.getZone(ZoneKey(ownerId, Zone.EXILE)).shouldBeEmpty()
        result.events.filterIsInstance<ZoneChangeEvent>().size shouldBe 3
    }

    // -------------------------------------------------------------------------------------------
    // Edge cases
    // -------------------------------------------------------------------------------------------

    test("a card that already left exile by other means is not returned") {
        val (exiled, cardId) = exileFrom(baseState(), Zone.HAND, "Bear")
        // Something else moved it out of exile — into the graveyard — before the return.
        val state = ZoneTransitionService.moveToZone(exiled, cardId, Zone.GRAVEYARD).state

        val result = registry().execute(state, Patterns.Exile.returnLinkedExileToZoneExiledFrom(), context())

        result.isSuccess shouldBe true
        result.state.getZone(ZoneKey(ownerId, Zone.GRAVEYARD)).shouldContainExactlyInAnyOrder(cardId)
        result.state.getZone(ZoneKey(ownerId, Zone.HAND)).shouldBeEmpty()
        result.events.filterIsInstance<ZoneChangeEvent>().shouldBeEmpty()
    }

    test("the return still works when the linking source has already left the battlefield") {
        // Put the source on the battlefield, exile a card with it, then send the source to its
        // graveyard the way the engine does. LinkedExileComponent survives that move, so the
        // leaves-the-battlefield trigger's return still finds the pile.
        val onBattlefield = baseState().addToZone(ZoneKey(casterId, Zone.BATTLEFIELD), sourceId)
        val (exiled, cardId) = exileFrom(onBattlefield, Zone.HAND, "Bear")
        val state = ZoneTransitionService.moveToZone(exiled, sourceId, Zone.GRAVEYARD).state
        state.getZone(ZoneKey(casterId, Zone.BATTLEFIELD)).shouldBeEmpty()

        val result = registry().execute(state, Patterns.Exile.returnLinkedExileToZoneExiledFrom(), context())

        result.isSuccess shouldBe true
        result.state.getZone(ZoneKey(ownerId, Zone.HAND)).shouldContainExactlyInAnyOrder(cardId)
    }

    test("a token exiled from the battlefield ceases to exist and never returns") {
        val (exiled, tokenId) = exileFrom(baseState(), Zone.BATTLEFIELD, "Spirit", isToken = true)
        // CR 704.5d — the token is swept out of exile by a state-based action before the source
        // ever leaves, so there is nothing left to return.
        val state = TokensInWrongZonesCheck().check(exiled).state
        state.getEntity(tokenId).shouldBeNull()

        val result = registry().execute(state, Patterns.Exile.returnLinkedExileToZoneExiledFrom(), context())

        result.isSuccess shouldBe true
        result.state.getZone(ZoneKey(ownerId, Zone.BATTLEFIELD)).shouldBeEmpty()
        result.state.getEntity(tokenId).shouldBeNull()
        result.events.filterIsInstance<ZoneChangeEvent>().shouldBeEmpty()
    }

    test("a card in exile with no recorded origin falls back to the battlefield") {
        var state = baseState()
        val cardId = EntityId.generate()
        state = state.withEntity(
            cardId,
            ComponentContainer().with(cardComponent("Orphan", ownerId)).with(OwnerComponent(ownerId))
        )
        state = state.addToZone(ZoneKey(ownerId, Zone.EXILE), cardId)
        state = state.updateEntity(sourceId) { c -> c.with(LinkedExileComponent(listOf(cardId))) }

        val result = registry().execute(state, Patterns.Exile.returnLinkedExileToZoneExiledFrom(), context())

        result.isSuccess shouldBe true
        result.state.getZone(ZoneKey(ownerId, Zone.BATTLEFIELD)).shouldContainExactlyInAnyOrder(cardId)
    }

    test("a card exiled from exile (CR 406.7) stays in exile instead of taking a spurious transition") {
        // Exile it once from hand, then exile it again while it is already in exile: CR 406.7 —
        // "it doesn't change zones, but it becomes a new object that has just been exiled" — so
        // the re-stamped origin is EXILE itself.
        val (once, cardId) = exileFrom(baseState(), Zone.HAND, "Twice Exiled")
        var state = ZoneTransitionService.moveToZone(once, cardId, Zone.EXILE).state
        state.getEntity(cardId)?.get<ExiledFromZoneComponent>()?.zone shouldBe Zone.EXILE
        // A marker that an exile → exile round trip through ZoneTransitionService would strip.
        state = state.updateEntity(cardId) { c -> c.with(SuspendedComponent) }

        val result = registry().execute(state, Patterns.Exile.returnLinkedExileToZoneExiledFrom(), context())

        result.isSuccess shouldBe true
        withClue("CR 610.3's 'previous zone' for an object exiled from exile is exile — it stays put") {
            result.state.getZone(ZoneKey(ownerId, Zone.EXILE)).shouldContainExactlyInAnyOrder(cardId)
            result.state.getZone(ZoneKey(ownerId, Zone.BATTLEFIELD)).shouldBeEmpty()
            result.state.getZone(ZoneKey(ownerId, Zone.HAND)).shouldBeEmpty()
        }
        withClue("no exile → exile move happened, so no event and no marker stripping") {
            result.events.filterIsInstance<ZoneChangeEvent>().shouldBeEmpty()
            result.state.getEntity(cardId)?.get<SuspendedComponent>().shouldNotBeNull()
        }
    }

    test("cards swept into exile by an opponents'-graveyards effect still remember the graveyard") {
        // ExileOpponentsGraveyardsExecutor moves cards with a bare addToZone rather than through
        // ZoneTransitionService. Without its own origin stamp these would take the BATTLEFIELD
        // fallback — i.e. a return would *reanimate* a graveyard card.
        var state = baseState()
        val cardId = EntityId.generate()
        state = state.withEntity(
            cardId,
            ComponentContainer().with(cardComponent("Dead Thing", ownerId)).with(OwnerComponent(ownerId))
        )
        state = state.addToZone(ZoneKey(ownerId, Zone.GRAVEYARD), cardId)

        val swept = ExileOpponentsGraveyardsExecutor()
            .execute(state, ExileOpponentsGraveyardsEffect, context())
        swept.isSuccess shouldBe true
        swept.state.getEntity(cardId)?.get<ExiledFromZoneComponent>()?.zone shouldBe Zone.GRAVEYARD

        val linked = swept.state.updateEntity(sourceId) { c -> c.with(LinkedExileComponent(listOf(cardId))) }
        val result = registry().execute(linked, Patterns.Exile.returnLinkedExileToZoneExiledFrom(), context())

        result.isSuccess shouldBe true
        withClue("back to the graveyard it was swept out of, not onto the battlefield") {
            result.state.getZone(ZoneKey(ownerId, Zone.GRAVEYARD)).shouldContainExactlyInAnyOrder(cardId)
            result.state.getZone(ZoneKey(ownerId, Zone.BATTLEFIELD)).shouldBeEmpty()
        }
    }

    test("a card recorded as exiled from the stack falls back to the battlefield") {
        var state = baseState()
        val cardId = EntityId.generate()
        state = state.withEntity(
            cardId,
            ComponentContainer()
                .with(cardComponent("Countered Spell", ownerId))
                .with(OwnerComponent(ownerId))
                .with(ExiledFromZoneComponent(Zone.STACK))
        )
        state = state.addToZone(ZoneKey(ownerId, Zone.EXILE), cardId)
        state = state.updateEntity(sourceId) { c -> c.with(LinkedExileComponent(listOf(cardId))) }

        val result = registry().execute(state, Patterns.Exile.returnLinkedExileToZoneExiledFrom(), context())

        result.isSuccess shouldBe true
        result.state.getZone(ZoneKey(ownerId, Zone.BATTLEFIELD)).shouldContainExactlyInAnyOrder(cardId)
    }

    // -------------------------------------------------------------------------------------------
    // The two pre-existing fixed-destination recipes are unchanged by all of the above
    // -------------------------------------------------------------------------------------------

    test("returnLinkedExile still ignores the origin zone and goes to the battlefield") {
        var state = baseState()
        val (s1, fromHand) = exileFrom(state, Zone.HAND, "Hand Card")
        state = s1
        val (s2, fromGraveyard) = exileFrom(state, Zone.GRAVEYARD, "Dead Thing")
        state = s2

        val result = registry().execute(state, Patterns.Exile.returnLinkedExile(), context())

        result.isSuccess shouldBe true
        // Controller's battlefield, not the owner's, and not the zones they came from.
        result.state.getZone(ZoneKey(casterId, Zone.BATTLEFIELD))
            .shouldContainExactlyInAnyOrder(fromHand, fromGraveyard)
        result.state.getZone(ZoneKey(ownerId, Zone.HAND)).shouldBeEmpty()
        result.state.getZone(ZoneKey(ownerId, Zone.GRAVEYARD)).shouldBeEmpty()
    }

    test("returnLinkedExileToHand still ignores the origin zone and goes to hand") {
        var state = baseState()
        val (s1, fromBattlefield) = exileFrom(state, Zone.BATTLEFIELD, "Creature")
        state = s1
        val (s2, fromGraveyard) = exileFrom(state, Zone.GRAVEYARD, "Dead Thing")
        state = s2

        val result = registry().execute(state, Patterns.Exile.returnLinkedExileToHand(), context())

        result.isSuccess shouldBe true
        result.state.getZone(ZoneKey(ownerId, Zone.HAND))
            .shouldContainExactlyInAnyOrder(fromBattlefield, fromGraveyard)
        result.state.getZone(ZoneKey(ownerId, Zone.BATTLEFIELD)).shouldBeEmpty()
        result.state.getZone(ZoneKey(ownerId, Zone.GRAVEYARD)).shouldBeEmpty()
    }
})
