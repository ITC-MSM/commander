package com.wingedsheep.engine.handlers.effects.zones

import com.wingedsheep.engine.core.EngineServices
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.core.YesNoResponse
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.ComponentContainer
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.GraveyardEntryTurnComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.CommanderComponent
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
import com.wingedsheep.sdk.scripting.effects.ReturnCreaturesPutInGraveyardThisTurnEffect
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Garna's graveyard-return loop used to remove/add cards directly.  These cases prove it now
 * delegates every return through the pauseable CR 903.9b zone-change pipeline and resumes the
 * rest of the frozen batch exactly once.
 */
class ReturnCreaturesPutInGraveyardThisTurnCommanderReplacementTest : FunSpec({
    val services = EngineServices(CardRegistry())
    val owner = EntityId.generate()
    val commander = EntityId.generate()
    val ordinaryCreature = EntityId.generate()

    fun creature(id: String, isCommander: Boolean) = ComponentContainer.of(
        CardComponent(
            cardDefinitionId = id, name = id, manaCost = ManaCost.ZERO,
            typeLine = TypeLine(
                supertypes = if (isCommander) setOf(Supertype.LEGENDARY) else emptySet(),
                cardTypes = setOf(CardType.CREATURE)
            ),
            oracleText = "", baseStats = CreatureStats(2, 2), colors = setOf(Color.RED), ownerId = owner
        ),
        OwnerComponent(owner),
        GraveyardEntryTurnComponent(7)
    ).let { if (isCommander) it.with(CommanderComponent(owner)) else it }

    fun state() = GameState(format = Format.Commander(), turnNumber = 7)
        .withEntity(owner, ComponentContainer.EMPTY)
        .withEntity(commander, creature("Commander", true))
        .withEntity(ordinaryCreature, creature("Ordinary", false))
        .addToZone(ZoneKey(owner, Zone.GRAVEYARD), commander)
        .addToZone(ZoneKey(owner, Zone.GRAVEYARD), ordinaryCreature)
        .copy(turnOrder = listOf(owner))

    fun execute(initial: GameState) = services.effectExecutorRegistry.execute(
        initial,
        ReturnCreaturesPutInGraveyardThisTurnEffect(),
        EffectContext(sourceId = null, controllerId = owner)
    )

    fun resume(state: GameState, accept: Boolean) = state.pendingDecision
        .shouldBeInstanceOf<YesNoDecision>()
        .let { decision ->
            services.continuationHandler.resume(
                state.clearPendingDecision(), YesNoResponse(decision.id, accept)
            )
        }

    test("Garna return pauses for its commander and accepting resumes the remaining return once") {
        val result = execute(state())
        result.isPaused shouldBe true
        result.state.getZone(ZoneKey(owner, Zone.GRAVEYARD)).shouldContainExactly(commander, ordinaryCreature)

        val resolved = resume(result.state, accept = true)
        resolved.state.getZone(ZoneKey(owner, Zone.COMMAND)).shouldContainExactly(commander)
        resolved.state.getZone(ZoneKey(owner, Zone.HAND)).shouldContainExactly(ordinaryCreature)
        resolved.state.getZone(ZoneKey(owner, Zone.GRAVEYARD)).shouldContainExactly()
    }

    test("Garna return decline moves each eligible card to hand exactly once") {
        val result = execute(state())
        val resolved = resume(result.state, accept = false)

        resolved.state.getZone(ZoneKey(owner, Zone.HAND)).shouldContainExactly(commander, ordinaryCreature)
        resolved.state.getZone(ZoneKey(owner, Zone.COMMAND)).shouldContainExactly()
        resolved.state.getZone(ZoneKey(owner, Zone.GRAVEYARD)).shouldContainExactly()
    }
})
