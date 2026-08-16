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
 *     pass. Nothing here has to check for them: `SetBaseStatsEffect`'s own `init` rejects them via
 *     [com.wingedsheep.sdk.scripting.values.contextScopedReferenceIn] when the card is loaded, so
 *     by the time an effect reaches this executor its amounts are already known re-evaluable.
 *     CR 611.2d independently forbids re-evaluating X.
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
            // Carry the DynamicAmounts through; the projector re-reads them on every pass. That the
            // amounts are ones it *can* re-read was settled when the effect was constructed —
            // SetBaseStatsEffect's init rejects context-scoped references at card-load time.
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
