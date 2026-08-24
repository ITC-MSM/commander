package com.wingedsheep.engine.legalactions.utils

import com.wingedsheep.engine.handlers.ConditionEvaluator
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.PredicateContext
import com.wingedsheep.engine.handlers.PredicateEvaluator
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantAdditionalLandDrop
import com.wingedsheep.sdk.scripting.PlayersCantPlayLands
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Utility for calculating additional land drops from static abilities.
 */
object LandDropUtils {

    /**
     * Count additional land drops granted by [GrantAdditionalLandDrop] static abilities
     * on permanents controlled by the given player. Multiple sources are additive.
     *
     * A [ConditionalStaticAbility] wrapper is unwrapped and its condition evaluated against the
     * source permanent, so "as long as …" gates are honored — Thranduil's Company only grants the
     * extra drop while you control another Elf. Without the unwrap the grant silently no-ops, the
     * same trap [com.wingedsheep.engine.core.MaximumHandSize] documents for `SetMaximumHandSize`.
     */
    /**
     * True if any permanent on the battlefield forbids [playerId] from playing lands
     * ([com.wingedsheep.sdk.scripting.PlayersCantPlayLands] — Worms of the Earth).
     *
     * Scans the whole battlefield, not just [playerId]'s: the lock is usually somebody else's
     * enchantment. A [ConditionalStaticAbility] wrapper is unwrapped and evaluated against its own
     * source, the same way the land-drop bonus above is, so an "as long as …" gate is honored
     * instead of silently locking forever.
     *
     * [landCardId] scopes the question to one candidate card, which is what a *filtered* lock
     * needs (City in a Bottle stops only the lands originally printed in ARN). Pass `null` — the
     * default — to ask the blanket question "is this player locked out of land drops entirely?";
     * a filtered lock deliberately answers `false` there, so the unaffected lands in the hand stay
     * playable and only the per-card call below rejects the matching ones.
     */
    fun playerCantPlayLands(
        state: GameState,
        playerId: EntityId,
        cardRegistry: CardRegistry,
        conditionEvaluator: ConditionEvaluator = ConditionEvaluator(),
        landCardId: EntityId? = null,
    ): Boolean {
        val projected = state.projectedState
        for (entityId in state.getBattlefield()) {
            val card = state.getEntity(entityId)?.get<CardComponent>() ?: continue
            val cardDef = cardRegistry.getCard(card.cardDefinitionId) ?: continue
            val sourceController = projected.getController(entityId) ?: continue
            for (ability in cardDef.script.staticAbilities) {
                val lock = when (ability) {
                    is PlayersCantPlayLands -> ability
                    is ConditionalStaticAbility -> {
                        val inner = ability.ability as? PlayersCantPlayLands ?: continue
                        val context = EffectContext(sourceId = entityId, controllerId = sourceController)
                        if (!conditionEvaluator.evaluate(state, ability.condition, context)) continue
                        inner
                    }
                    else -> continue
                }
                val affected = when (lock.affected) {
                    is Player.Each -> state.activePlayers
                    is Player.You -> listOf(sourceController)
                    is Player.EachOpponent -> state.getOpponents(sourceController)
                    else -> continue
                }
                if (playerId !in affected) continue
                // A filtered lock only bites on a named candidate; the blanket probe skips it.
                if (lock.landFilter != GameObjectFilter.Any) {
                    if (landCardId == null) continue
                    if (!predicateEvaluator.matches(
                            state, projected, landCardId, lock.landFilter,
                            PredicateContext(controllerId = playerId)
                        )
                    ) continue
                }
                val context = EffectContext(sourceId = entityId, controllerId = sourceController)
                if (lock.condition == null || conditionEvaluator.evaluate(state, lock.condition!!, context)) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * Cheap guard: does any battlefield permanent carry a *filtered* [PlayersCantPlayLands]
     * (`landFilter != Any`)? Lets enumeration skip the per-card [playerCantPlayLands] scan
     * entirely in the common case where none is in play. Cached once per enumeration pass by
     * [com.wingedsheep.engine.legalactions.EnumerationContext]; the mirror of
     * `CastPermissionUtils.anyPerSpellCastRestrictionPresent`.
     */
    fun anyFilteredLandLockPresent(state: GameState, cardRegistry: CardRegistry): Boolean =
        state.getBattlefield().any { id ->
            val cardDef = state.getEntity(id)?.get<CardComponent>()
                ?.let { cardRegistry.getCard(it.cardDefinitionId) }
            cardDef?.script?.staticAbilities?.any { ability ->
                val lock = ability as? PlayersCantPlayLands
                    ?: (ability as? ConditionalStaticAbility)?.ability as? PlayersCantPlayLands
                lock != null && lock.landFilter != GameObjectFilter.Any
            } == true
        }

    private val predicateEvaluator = PredicateEvaluator()

    fun getAdditionalLandDrops(
        state: GameState,
        playerId: EntityId,
        cardRegistry: CardRegistry,
        conditionEvaluator: ConditionEvaluator = ConditionEvaluator(),
    ): Int {
        var bonus = 0
        for (entityId in state.getBattlefield(playerId)) {
            val card = state.getEntity(entityId)?.get<CardComponent>() ?: continue
            val cardDef = cardRegistry.getCard(card.cardDefinitionId) ?: continue
            for (ability in cardDef.script.staticAbilities) {
                when (ability) {
                    is GrantAdditionalLandDrop -> bonus += ability.count
                    is ConditionalStaticAbility -> {
                        val inner = ability.ability as? GrantAdditionalLandDrop ?: continue
                        val context = EffectContext(sourceId = entityId, controllerId = playerId)
                        if (conditionEvaluator.evaluate(state, ability.condition, context)) {
                            bonus += inner.count
                        }
                    }
                    else -> {}
                }
            }
        }
        return bonus
    }
}
