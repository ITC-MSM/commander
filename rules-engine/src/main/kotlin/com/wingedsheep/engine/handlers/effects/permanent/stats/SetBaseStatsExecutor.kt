package com.wingedsheep.engine.handlers.effects.permanent.stats

import com.wingedsheep.engine.core.EffectResult
import com.wingedsheep.engine.handlers.DynamicAmountEvaluator
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.effects.EffectExecutor
import com.wingedsheep.engine.mechanics.layers.Layer
import com.wingedsheep.engine.mechanics.layers.SerializableModification
import com.wingedsheep.engine.mechanics.layers.Sublayer
import com.wingedsheep.engine.mechanics.layers.addFloatingEffect
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.sdk.scripting.effects.SetBaseStatsEffect
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.encodeToJsonElement
import kotlin.reflect.KClass

/**
 * Executor for [SetBaseStatsEffect].
 *
 * Creates a floating effect at Layer.POWER_TOUGHNESS, Sublayer.SET_VALUES (CR 613.4b) that sets
 * whichever stats are non-null. Either way the affected set is locked in here, at resolution
 * (CR 611.2c); the two modes differ in when the number is read, and in the two documented ways
 * listed underneath:
 *
 *  - [SetBaseStatsEffect.reevaluateContinuously] `false` (default) — snapshot: the amounts are
 *    evaluated now, against this resolution's [EffectContext], and the fixed modification is
 *    stamped:
 *      - both     -> [SerializableModification.SetPowerToughness]
 *      - power    -> [SerializableModification.SetPower]    (toughness unchanged)
 *      - toughness-> [SerializableModification.SetToughness] (power unchanged)
 *    "Change this creature's base power to target creature's power." / "It has base power and
 *    toughness 2/2 until your next turn."
 *  - `true` — the `DynamicAmount`s are carried into a single
 *    [SerializableModification.SetPowerToughnessDynamic] (independently nullable) and re-evaluated
 *    on every projection pass. That is what an effect handing out a quoted "this creature's base
 *    power is equal to …" static needs (Ms. Marvel, Kamala Khan).
 *
 * The two ways the re-evaluated mode is *not* merely a different clock, both of them consequences
 * of the number being read from the projector rather than from here:
 *
 *  1. **Only projection-scoped amounts work.** The projector rebuilds a bare `EffectContext` from
 *     the source, its controller and the affected entity, so target-, X-, triggering- and
 *     cost-scoped references have nothing to resolve against and would read as absent on every
 *     pass. [contextScopedReferenceIn] rejects those at resolution rather than letting them
 *     silently compute 0 forever; CR 611.2d independently forbids re-evaluating X.
 *  2. **The re-evaluated set applies only while the permanent is a creature.** `EffectApplicator`
 *     gates the dynamic branch on the projected type line (CR 208.3a: the effect is still created,
 *     it just "doesn't do anything unless that permanent becomes a creature"), and re-asks that
 *     gate every pass, so a Vehicle crewed later in the turn picks the value up. The fixed
 *     `SetPower`/`SetToughness`/`SetPowerToughness` branches write unconditionally.
 */
class SetBaseStatsExecutor(
    private val amountEvaluator: DynamicAmountEvaluator = DynamicAmountEvaluator()
) : EffectExecutor<SetBaseStatsEffect> {

    override val effectType: KClass<SetBaseStatsEffect> = SetBaseStatsEffect::class

    override fun execute(
        state: GameState,
        effect: SetBaseStatsEffect,
        context: EffectContext
    ): EffectResult {
        val targetId = context.resolveTarget(effect.target, state)
            ?: return EffectResult.success(state)

        // Verify target is on the battlefield
        if (targetId !in state.getBattlefield()) {
            return EffectResult.success(state)
        }

        val modification: SerializableModification = if (effect.reevaluateContinuously) {
            // Carry the DynamicAmounts through; the projector re-reads them on every pass — which
            // is only meaningful for amounts it can still evaluate. Fail loudly here rather than
            // reading 0 on every pass for the rest of the effect's duration.
            listOfNotNull(effect.power, effect.toughness).forEach { amount ->
                val offending = contextScopedReferenceIn(amount)
                require(offending == null) {
                    "SetBaseStatsEffect(reevaluateContinuously = true) cannot carry the " +
                        "context-scoped reference '$offending' in ${amount.description}: the " +
                        "projector re-evaluates the amount with only the source, its controller " +
                        "and the affected entity in scope, so target-, X-, triggering- and " +
                        "cost-scoped references resolve to nothing on every pass. Use the default " +
                        "snapshot mode (CR 611.2d requires X to be fixed on resolution anyway), " +
                        "or an amount computable from the source, the affected entity and global " +
                        "game state."
                }
            }
            if (effect.power == null && effect.toughness == null) return EffectResult.success(state)
            SerializableModification.SetPowerToughnessDynamic(effect.power, effect.toughness)
        } else {
            val power = effect.power?.let { amountEvaluator.evaluate(state, it, context) }
            val toughness = effect.toughness?.let { amountEvaluator.evaluate(state, it, context) }
            when {
                power != null && toughness != null -> SerializableModification.SetPowerToughness(power, toughness)
                power != null -> SerializableModification.SetPower(power)
                toughness != null -> SerializableModification.SetToughness(toughness)
                else -> return EffectResult.success(state) // nothing to set
            }
        }

        val newState = state.addFloatingEffect(
            layer = Layer.POWER_TOUGHNESS,
            modification = modification,
            affectedEntities = setOf(targetId),
            duration = effect.duration,
            context = context,
            sublayer = Sublayer.SET_VALUES
        )

        return EffectResult.success(newState)
    }
}

/**
 * Serial names of the `DynamicAmount` / `EntityReference` / `Player` shapes whose value lives in
 * the resolution-time `EffectContext` — the chosen targets, the announced X, the triggering object,
 * the things sacrificed or tapped to pay a cost, the resolution pipeline's stored collections.
 *
 * `EffectApplicator` rebuilds a bare `EffectContext(sourceId, controllerId, affectedEntityId)` on
 * every projection pass, so none of these can resolve there; an amount containing one would read as
 * absent (0, or an empty player/entity list) forever. They are therefore rejected for
 * [SetBaseStatsEffect.reevaluateContinuously] rather than silently mis-evaluated. Everything not
 * listed here is evaluated from the source, the affected entity, or global game state, which the
 * projector does carry.
 *
 * Kept as a deny list rather than an allow list because the projector-safe set is open-ended (every
 * `Count`/`AggregateBattlefield`/`EntityProperty(Source | AffectedEntity)` shape works); the
 * *traversal* is what has to be exhaustive, and encoding to JSON makes it so — no nesting site can
 * be missed the way a hand-written `when` over the composite amounts could.
 */
private val CONTEXT_SCOPED_SERIAL_NAMES: Set<String> = setOf(
    // DynamicAmount
    "XValue", "CastX", "CastChoice", "ContextProperty", "VariableReference", "StoredCardManaValue",
    "DistinctEntitiesInCollections", "DistinctCardTypesInCollections", "ManaValueSumOfCollection",
    "TotalManaSpent", "ManaSpentOnX", "PermanentsSacrificedThisWay", "StationCharge",
    "LastKnownSourceCounters", "LastKnownDamageDealtToSource",
    // EntityReference
    "Target", "Triggering", "Sacrificed", "TappedAsCost", "FromCostStorage", "AmassedArmy",
    "IterationEntity",
    // Player
    "TargetPlayer", "TargetOpponent", "ContextPlayer", "TriggeringPlayer", "ControllerOf", "OwnerOf",
)

private val CONTEXT_SCAN_JSON = Json

/**
 * The serial name of the first context-scoped reference anywhere inside [amount], or null when the
 * whole tree is projector-safe. See [CONTEXT_SCOPED_SERIAL_NAMES].
 */
internal fun contextScopedReferenceIn(amount: DynamicAmount): String? =
    findContextScoped(CONTEXT_SCAN_JSON.encodeToJsonElement<DynamicAmount>(amount))

private fun findContextScoped(element: JsonElement): String? = when (element) {
    is JsonObject -> element.entries.firstNotNullOfOrNull { (key, value) ->
        val discriminator = (value as? JsonPrimitive)?.takeIf { it.isString }?.content
        if (key == "type" && discriminator in CONTEXT_SCOPED_SERIAL_NAMES) discriminator
        else findContextScoped(value)
    }
    is JsonArray -> element.firstNotNullOfOrNull { findContextScoped(it) }
    else -> null
}
