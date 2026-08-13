package com.wingedsheep.engine.handlers.effects.drawing

import com.wingedsheep.engine.core.CardsDrawnEvent
import com.wingedsheep.engine.core.EngineServices
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.core.YesNoResponse
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.ComponentContainer
import com.wingedsheep.engine.state.Component
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.CommanderComponent
import com.wingedsheep.engine.state.components.identity.OwnerComponent
import com.wingedsheep.engine.state.components.player.CardsDrawnThisTurnComponent
import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Format
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Supertype
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.CreatureStats
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * CR 903.9b for the physical library -> hand movement in a draw instruction.
 * These pin both the pause boundary and that a replaced move is not a draw.
 */
class DrawCommanderReplacementTest : FunSpec({
    val services = EngineServices(CardRegistry())
    val player = EntityId.generate()
    val commander = EntityId.generate()
    val second = EntityId.generate()
    val third = EntityId.generate()

    fun card(id: String, name: String, isCommander: Boolean = false): ComponentContainer {
        val components = mutableListOf<Component>(
            CardComponent(
            cardDefinitionId = id,
            name = name,
            manaCost = ManaCost.ZERO,
            typeLine = TypeLine(
                if (isCommander) setOf(Supertype.LEGENDARY) else emptySet(),
                setOf(CardType.CREATURE)
            ),
            oracleText = "",
            baseStats = CreatureStats(2, 2),
            colors = setOf(Color.BLUE),
            ownerId = player
            ),
            OwnerComponent(player)
        )
        if (isCommander) components.add(CommanderComponent(player))
        return ComponentContainer.of(*components.toTypedArray())
    }

    fun state(topIsCommander: Boolean = true): GameState {
        val first = if (topIsCommander) commander else second
        val last = if (topIsCommander) second else commander
        return GameState(format = Format.Commander())
            .withEntity(player, ComponentContainer.EMPTY)
            .withEntity(commander, card("Commander", "Test Commander", true))
            .withEntity(second, card("Second", "Second Card"))
            .withEntity(third, card("Third", "Third Card"))
            .addToZone(ZoneKey(player, Zone.LIBRARY), first)
            .addToZone(ZoneKey(player, Zone.LIBRARY), last)
            .copy(turnOrder = listOf(player))
    }

    fun resume(s: GameState, choice: Boolean) = services.continuationHandler.resume(
        s.clearPendingDecision(),
        YesNoResponse(s.pendingDecision.shouldBeInstanceOf<YesNoDecision>().id, choice)
    )

    test("accepting Commander replacement during draw two moves it once and resumes the second draw") {
        val initial = state()
        val paused = DrawCardsExecutor(cardRegistry = CardRegistry()).executeDraws(initial, player, 2)
        paused.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        paused.state.getZone(ZoneKey(player, Zone.LIBRARY)).shouldContainExactly(commander, second)
        (paused.state.getEntity(player)?.get<CardsDrawnThisTurnComponent>()?.count ?: 0) shouldBe 0

        val resolved = resume(paused.state, true)
        resolved.state.getZone(ZoneKey(player, Zone.COMMAND)).shouldContainExactly(commander)
        resolved.state.getZone(ZoneKey(player, Zone.HAND)).shouldContainExactly(second)
        resolved.state.getEntity(player)?.get<CardsDrawnThisTurnComponent>()?.count shouldBe 1
        resolved.events.filterIsInstance<CardsDrawnEvent>().map { it.cardIds } shouldContainExactly listOf(listOf(second))
    }

    test("declining Commander replacement during draw two counts each card once in draw order") {
        val paused = DrawCardsExecutor(cardRegistry = CardRegistry()).executeDraws(state(), player, 2)
        val resolved = resume(paused.state, false)

        resolved.state.getZone(ZoneKey(player, Zone.HAND)).shouldContainExactly(commander, second)
        resolved.state.getZone(ZoneKey(player, Zone.COMMAND)).shouldContainExactly()
        resolved.state.getEntity(player)?.get<CardsDrawnThisTurnComponent>()?.count shouldBe 2
        resolved.events.filterIsInstance<CardsDrawnEvent>().map { it.cardIds } shouldContainExactly
            listOf(listOf(commander), listOf(second))
        resolved.state.lastCardDrawnThisTurnByPlayer[player] shouldBe second
    }

    test("ordinary draw two retains its single aggregate draw event and does not pause") {
        val initial = state(topIsCommander = false)
            .removeFromZone(ZoneKey(player, Zone.LIBRARY), commander)
            .addToZone(ZoneKey(player, Zone.LIBRARY), third)
        val result = DrawCardsExecutor(cardRegistry = CardRegistry()).executeDraws(initial, player, 2)

        result.pendingDecision shouldBe null
        result.state.getZone(ZoneKey(player, Zone.HAND)).shouldContainExactly(second, third)
        result.state.getEntity(player)?.get<CardsDrawnThisTurnComponent>()?.count shouldBe 2
        result.events.filterIsInstance<CardsDrawnEvent>().map { it.cardIds } shouldContainExactly
            listOf(listOf(second, third))
    }
})
