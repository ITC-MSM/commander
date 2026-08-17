package com.wingedsheep.sdk.scripting.effects

import kotlinx.serialization.Serializable
import com.wingedsheep.sdk.scripting.text.TextReplaceable
import com.wingedsheep.sdk.scripting.text.TextReplacer
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Sealed hierarchy of effects.
 * Effects define WHAT happens when an ability resolves.
 *
 * Effect implementations are organized across multiple files in the effects/ subdirectory:
 * - LifeEffects.kt - Life gain/loss effects
 * - DamageEffects.kt - Damage dealing effects
 * - DrawingEffects.kt - Card draw/discard effects
 * - RemovalEffects.kt - Destroy/exile/sacrifice effects
 * - PermanentEffects.kt - Permanent state transformations (animation, morph, equipment)
 * - CounterEffects.kt - Counter manipulation effects
 * - KeywordAndAbilityEffects.kt - Keyword/ability grant effects
 * - TypeAndColorEffects.kt - Type/subtype/color change effects
 * - ControlEffects.kt - Control change effects
 * - StatsEffects.kt - Power/toughness modification effects
 * - TapEffects.kt - Tap/untap effects
 * - ManaEffects.kt - Mana-producing effects
 * - TokenEffects.kt - Token creation effects
 * - LibraryEffects.kt - Library manipulation effects
 * - StackEffects.kt - Counterspell effects
 * - PlayerEffects.kt - Turn/phase manipulation effects
 * - CombatEffects.kt - Combat-specific effects
 * - CompositeEffects.kt - Composite/modal/may effects
 * - TransformEffects.kt - Transform effects
 *
 * Supporting types are organized in subdirectories:
 * - values/ - DynamicAmount, PlayerReference, ZoneReference
 * - filters/unified/ - GameObjectFilter, GroupFilter, TargetFilter
 * - targets/ - EffectTarget
 * - costs/ - PayCost
 */
@Serializable
sealed interface Effect : TextReplaceable<Effect> {
    /** Human-readable description of the effect */
    val description: String

    /**
     * Returns a description with dynamic amounts evaluated to concrete values.
     * Override in effects that use [DynamicAmount] to show runtime values on the stack.
     *
     * [resolver] returns `null` when the current context cannot determine the amount yet. The case
     * that matters is a [DynamicAmount] reading a property off a target the player has not chosen
     * yet — the targeting banner renders its hint in exactly that state, before any target exists.
     * An override MUST fall back to the amount's own [DynamicAmount.description] there: rendering
     * an absent value as `0` claims a concrete number ("+0/+0") for something merely unknown, and
     * is indistinguishable from an amount that genuinely resolved to zero.
     */
    fun runtimeDescription(resolver: (DynamicAmount) -> Int?): String = description

    /**
     * Operator to chain effects.
     * Allows syntax like: EffectA then EffectB
     */
    infix fun then(next: Effect): CompositeEffect {
        return if (this is CompositeEffect) {
            CompositeEffect(this.effects + next)
        } else {
            CompositeEffect(listOf(this, next))
        }
    }
}