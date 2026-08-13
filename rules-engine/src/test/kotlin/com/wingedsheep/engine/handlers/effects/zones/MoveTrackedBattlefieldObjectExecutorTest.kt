package com.wingedsheep.engine.handlers.effects.zones

import com.wingedsheep.engine.core.EngineServices
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.core.YesNoResponse
import com.wingedsheep.engine.core.ZoneChangeEvent
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.ComponentContainer
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.BattlefieldEntryTimestampComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.CommanderComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.state.components.identity.OwnerComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Format
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Supertype
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.CreatureStats
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.effects.MoveTrackedBattlefieldObjectEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/** Ensures delayed single-object movement does not bypass pre-move Commander replacement. */
class MoveTrackedBattlefieldObjectExecutorTest : FunSpec({
    val services = EngineServices(CardRegistry())
    val executor = MoveTrackedBattlefieldObjectExecutor()
    val owner = EntityId.generate()
    val commander = EntityId.generate()

    fun state(): GameState = GameState(format = Format.Commander())
        .withEntity(owner, ComponentContainer.EMPTY)
        .withEntity(commander, ComponentContainer.of(
            CardComponent(
                cardDefinitionId = "TrackedCommander", name = "Tracked Commander",
                manaCost = ManaCost.ZERO,
                typeLine = TypeLine(setOf(Supertype.LEGENDARY), setOf(CardType.CREATURE)),
                oracleText = "", baseStats = CreatureStats(2, 2), colors = setOf(Color.BLUE), ownerId = owner
            ),
            OwnerComponent(owner),
            ControllerComponent(owner),
            CommanderComponent(owner),
            BattlefieldEntryTimestampComponent(42)
        ))
        .addToZone(ZoneKey(owner, Zone.BATTLEFIELD), commander)
        .copy(turnOrder = listOf(owner))

    fun execute(destination: Zone) = executor.execute(
        state(),
        MoveTrackedBattlefieldObjectEffect(
            target = EffectTarget.ContextTarget(0),
            destination = destination,
            enteredBattlefieldTimestamp = 42
        ),
        EffectContext(
            sourceId = null,
            controllerId = owner,
            targets = listOf(ChosenTarget.Permanent(commander))
        )
    )

    fun resume(result: com.wingedsheep.engine.core.EffectResult, accept: Boolean) =
        services.continuationHandler.resume(
            result.state.clearPendingDecision(),
            YesNoResponse(result.pendingDecision.shouldBeInstanceOf<YesNoDecision>().id, accept)
        )

    test("delayed bounce accepts Commander replacement before one command-zone move") {
        val result = execute(Zone.HAND)

        result.isPaused shouldBe true
        result.state.getZone(ZoneKey(owner, Zone.BATTLEFIELD)).shouldContainExactly(commander)

        val resolved = resume(result, accept = true)

        resolved.state.getZone(ZoneKey(owner, Zone.COMMAND)).shouldContainExactly(commander)
        resolved.state.getZone(ZoneKey(owner, Zone.HAND)).shouldContainExactly()
        val events = resolved.events.filterIsInstance<ZoneChangeEvent>()
        events.size shouldBe 1
        events.single().toZone shouldBe Zone.COMMAND
    }

    test("delayed tuck declines Commander replacement before one library move") {
        val result = execute(Zone.LIBRARY)

        result.isPaused shouldBe true
        result.state.getZone(ZoneKey(owner, Zone.BATTLEFIELD)).shouldContainExactly(commander)

        val resolved = resume(result, accept = false)

        resolved.state.getZone(ZoneKey(owner, Zone.LIBRARY)).shouldContainExactly(commander)
        resolved.state.getZone(ZoneKey(owner, Zone.COMMAND)).shouldContainExactly()
        val events = resolved.events.filterIsInstance<ZoneChangeEvent>()
        events.size shouldBe 1
        events.single().toZone shouldBe Zone.LIBRARY
    }
})
