package com.wingedsheep.engine.handlers.effects.linkedexile

import com.wingedsheep.engine.core.ChampionedEvent
import com.wingedsheep.engine.core.EffectResult
import com.wingedsheep.engine.core.GameEvent
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.effects.EffectExecutor
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.sdk.scripting.effects.EmitChampionedEventEffect
import kotlin.reflect.KClass

/**
 * `then` branch of the champion ability's "exile … unless" gate (wired by
 * [com.wingedsheep.sdk.dsl.champion]): emits one [ChampionedEvent] (CR 702.72c) for each permanent
 * the source just exiled as its champion ability resolved, so "when a [quality] is championed with
 * this creature" payoffs ([com.wingedsheep.sdk.scripting.EventPattern.ChampionedEvent], i.e.
 * Mistbind Clique) fire.
 *
 * CR 702.72c: "A permanent is 'championed' by another permanent if the latter exiles the former as
 * the direct result of a champion ability." The championed permanents are read out of the pipeline
 * collection [EmitChampionedEventEffect.from] — the champion move's `storeMovedAs`, i.e. the cards
 * that *actually reached exile*. That is stricter than the cards the controller picked: a pick that
 * never moved is not championed. Because this only runs on the gate's success branch, declining the
 * choice (which sacrifices the champion instead) emits nothing.
 *
 * The champion is [EffectContext.sourceId] and its controller [EffectContext.controllerId]. The
 * championed permanent is still a live entity in exile at this point, so its name reads directly off
 * the entity rather than needing last-known information — the opposite of the exploit twin, whose
 * subject has been sacrificed by the time the event is built.
 *
 * Card authors should not use [EmitChampionedEventEffect] directly; it is wired into `champion()`.
 */
class EmitChampionedEventExecutor : EffectExecutor<EmitChampionedEventEffect> {

    override val effectType: KClass<EmitChampionedEventEffect> = EmitChampionedEventEffect::class

    override fun execute(
        state: GameState,
        effect: EmitChampionedEventEffect,
        context: EffectContext
    ): EffectResult {
        val championId = context.sourceId ?: return EffectResult.success(state)
        val sourceName = state.getEntity(championId)?.get<CardComponent>()?.name ?: "Unknown"

        val events: List<GameEvent> = context.pipeline.storedCollections[effect.from]
            .orEmpty()
            .map { championedId ->
                ChampionedEvent(
                    championId = championId,
                    championControllerId = context.controllerId,
                    championedId = championedId,
                    championedName = state.getEntity(championedId)?.get<CardComponent>()?.name
                        ?: "Unknown",
                    sourceName = sourceName
                )
            }

        return EffectResult.success(state, events)
    }
}
