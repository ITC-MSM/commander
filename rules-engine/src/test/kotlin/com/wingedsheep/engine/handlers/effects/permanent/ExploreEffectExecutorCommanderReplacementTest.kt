package com.wingedsheep.engine.handlers.effects.permanent

import com.wingedsheep.engine.core.EngineServices
import com.wingedsheep.engine.core.PermanentExploredEvent
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.core.YesNoResponse
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.ComponentContainer
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.CommanderComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.state.components.identity.OwnerComponent
import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Format
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Supertype
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.CreatureStats
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.effects.ExploreEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Regression coverage for the direct Explore land path.  It is intentionally an executor-level
 * test: the commander begins in the library, and the land branch must retain Explore's remaining
 * work while CR 903.9b asks whether its move to hand is replaced.
 */
class ExploreEffectExecutorCommanderReplacementTest : FunSpec({
    val services = EngineServices(CardRegistry())
    val player = EntityId.generate()
    val explorer = EntityId.generate()
    val commanderLand = EntityId.generate()

    fun state() = GameState(format = Format.Commander())
        .withEntity(player, ComponentContainer.EMPTY)
        .withEntity(explorer, ComponentContainer.of(
            CardComponent(
                cardDefinitionId = "Explorer", name = "Explorer", manaCost = ManaCost.ZERO,
                typeLine = TypeLine(cardTypes = setOf(CardType.CREATURE)), oracleText = "",
                baseStats = CreatureStats(1, 1), colors = setOf(Color.GREEN), ownerId = player
            ),
            OwnerComponent(player), ControllerComponent(player)
        ))
        .withEntity(commanderLand, ComponentContainer.of(
            CardComponent(
                cardDefinitionId = "Commander Land", name = "Commander Land", manaCost = ManaCost.ZERO,
                typeLine = TypeLine(
                    supertypes = setOf(Supertype.LEGENDARY), cardTypes = setOf(CardType.LAND)
                ),
                oracleText = "", colors = emptySet(), ownerId = player
            ),
            OwnerComponent(player), CommanderComponent(player)
        ))
        .addToZone(ZoneKey(player, Zone.BATTLEFIELD), explorer)
        .addToZone(ZoneKey(player, Zone.LIBRARY), commanderLand)
        .copy(turnOrder = listOf(player))

    fun explore(initial: GameState) = services.effectExecutorRegistry.execute(
        initial,
        ExploreEffect(EffectTarget.SpecificEntity(explorer)),
        EffectContext(sourceId = explorer, controllerId = player)
    )

    fun resume(state: GameState, choice: Boolean) = state.pendingDecision
        .shouldBeInstanceOf<YesNoDecision>()
        .let { decision ->
            services.continuationHandler.resume(
                state.clearPendingDecision(), YesNoResponse(decision.id, choice)
            )
        }

    test("exploring a commander land pauses before its move to hand and accepting sends it to command") {
        val result = explore(state())

        result.isPaused shouldBe true
        result.pendingDecision.shouldBeInstanceOf<YesNoDecision>().playerId shouldBe player
        result.state.getZone(ZoneKey(player, Zone.LIBRARY)) shouldContain commanderLand

        val resumed = resume(result.state, true)
        resumed.state.getZone(ZoneKey(player, Zone.COMMAND)) shouldContain commanderLand
        resumed.state.getZone(ZoneKey(player, Zone.HAND)) shouldNotContain commanderLand
        resumed.state.pendingDecision shouldBe null
        resumed.events.filterIsInstance<PermanentExploredEvent>()
            .single().revealedCardWasLand shouldBe true
    }

    test("declining the Explore replacement puts the commander land in hand and resumes Explore") {
        val result = explore(state())
        val resumed = resume(result.state, false)

        resumed.state.getZone(ZoneKey(player, Zone.HAND)) shouldContain commanderLand
        resumed.state.getZone(ZoneKey(player, Zone.COMMAND)) shouldNotContain commanderLand
        resumed.state.pendingDecision shouldBe null
        resumed.events.filterIsInstance<PermanentExploredEvent>()
            .single().revealedCardWasLand shouldBe true
    }
})
