package com.wingedsheep.sdk.scripting

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.scripting.conditions.Condition
import com.wingedsheep.sdk.scripting.effects.Effect
import com.wingedsheep.sdk.scripting.targets.TargetRequirement
import com.wingedsheep.sdk.scripting.text.TextReplaceable
import com.wingedsheep.sdk.scripting.text.TextReplacer
import kotlinx.serialization.Serializable

/**
 * A triggered ability is an ability that fires when a specific condition is met.
 * It combines a trigger condition with an effect.
 *
 * Triggered abilities can optionally require targets. When a triggered ability
 * has a targetRequirement, the player must choose valid targets when the ability
 * goes on the stack. If no legal targets exist, the ability is removed from
 * the stack without resolving.
 */
@Serializable
data class TriggeredAbility(
    val id: AbilityId,
    val trigger: EventPattern,
    val binding: TriggerBinding = TriggerBinding.SELF,
    val effect: Effect,
    val optional: Boolean = false,
    val targetRequirement: TargetRequirement? = null,
    /** Additional target requirements for multi-target triggered abilities (e.g., exchange control). */
    val additionalTargetRequirements: List<TargetRequirement> = emptyList(),
    val elseEffect: Effect? = null,
    /**
     * The zones this ability's trigger condition functions in (CR 113.6b — "an ability that states
     * which zones it functions in functions only from those zones"). Defaults to the battlefield,
     * which CR 113.6 makes the rule for a permanent card's abilities.
     *
     * A *set* rather than a single zone because CR 113.6k allows one triggered ability to function
     * from several zones at once. Two shapes need it today: a graveyard/exile-resident ability
     * (`{GRAVEYARD}`, `{EXILE}` — Pyre Zombie, suspend, madness) and an *eminence* ability, which
     * functions from the command zone **and** the battlefield (`{BATTLEFIELD, COMMAND}` — Edgar
     * Markov). The detector scans each declared zone independently, so a card sitting in exactly
     * one of them fires exactly once.
     */
    val activeZones: Set<Zone> = setOf(Zone.BATTLEFIELD),
    /**
     * Intervening-if condition (Rule 603.4): checked when the trigger would fire. The engine does
     * not re-check it at resolution — an ability that needs CR 603.4's second half gates its own
     * effect on the same condition instead (see Edgar Markov's eminence ability).
     */
    val triggerCondition: Condition? = null,
    /** When true, the triggered ability is controlled by the triggering entity's controller
     * instead of the source permanent's controller. Used for cards like Death Match. */
    val controlledByTriggeringEntityController: Boolean = false,
    /** When true, this triggered ability triggers at most once each turn.
     * Used for cards like Scavenger's Talent: "This ability triggers only once each turn."
     *
     * This is a **trigger** cap: later matching events in the same turn don't trigger at all.
     * For the *other* printed rider, "Do this only once each turn", use [effectOncePerTurn]. */
    val oncePerTurn: Boolean = false,
    /**
     * When true, this ability triggers normally but its **effect** may be applied at most once
     * each turn — the printed rider "*Do this only once each turn*" (Jennifer Walters // The
     * Sensational She-Hulk, Baron Strucker, HYDRA Overlord).
     *
     * Per CR 603.2 the ability triggers once per matching event, so in a multi-block every damaged
     * creature (every Villain entering) puts its own instance on the stack; the controller then
     * declines the ones they don't want and applies the effect on the one they do. Declining an
     * optional instance does **not** spend the budget — the engine lowers this flag into a
     * [com.wingedsheep.sdk.scripting.effects.Gate.OnceEachTurn] gate placed *inside* the consent
     * gate, so the budget is spent only when the effect actually runs.
     *
     * Do not model this wording with [oncePerTurn]: a trigger cap fires only on the *first*
     * matching event and never offers the rest, which makes "decline down to the biggest damage
     * number" (or "pick which Villain connives") unreachable.
     */
    val effectOncePerTurn: Boolean = false,
    /** When true, this triggered ability triggers at most once over the source permanent's
     * lifetime on the battlefield — a permanent (not per-turn) cap. Used for cards like
     * Acrobatic Cheerleader: "This ability triggers only once." Tracked by a component that,
     * unlike the [oncePerTurn] tracker, is NOT cleared at end of turn. */
    val triggersOnce: Boolean = false,
    /** Optional human-readable description that overrides the auto-generated one. */
    val descriptionOverride: String? = null
) : TextReplaceable<TriggeredAbility> {
    /** All target requirements for this ability (primary + additional). */
    val allTargetRequirements: List<TargetRequirement>
        get() = listOfNotNull(targetRequirement) + additionalTargetRequirements

    val description: String
        get() = descriptionOverride ?: buildString {
            append(trigger.description)
            if (triggerCondition != null) {
                append(", ")
                append(triggerCondition.description)
            }
            if (optional) append(", you may")
            append(", ")
            if (targetRequirement != null) {
                append(targetRequirement.description)
                append(" ")
            }
            append(effect.description.replaceFirstChar { it.lowercase() })
            if (elseEffect != null) {
                append(". If you don't, ")
                append(elseEffect.description.replaceFirstChar { it.lowercase() })
            }
            append(".")
            if (effectOncePerTurn) append(" Do this only once each turn.")
        }

    /** Whether this triggered ability requires targets */
    val requiresTargets: Boolean
        get() = targetRequirement != null

    override fun applyTextReplacement(replacer: TextReplacer): TriggeredAbility {
        val newTrigger = trigger.applyTextReplacement(replacer)
        val newEffect = effect.applyTextReplacement(replacer)
        val newTargetReq = targetRequirement?.applyTextReplacement(replacer)
        var addlChanged = false
        val newAddlTargetReqs = additionalTargetRequirements.map {
            val n = it.applyTextReplacement(replacer)
            if (n !== it) addlChanged = true
            n
        }
        val newElseEffect = elseEffect?.applyTextReplacement(replacer)
        val newTriggerCondition = triggerCondition?.applyTextReplacement(replacer)
        return if (newTrigger !== trigger || newEffect !== effect ||
                   newTargetReq !== targetRequirement || addlChanged ||
                   newElseEffect !== elseEffect || newTriggerCondition !== triggerCondition)
            copy(trigger = newTrigger, effect = newEffect,
                 targetRequirement = newTargetReq,
                 additionalTargetRequirements = newAddlTargetReqs,
                 elseEffect = newElseEffect,
                 triggerCondition = newTriggerCondition) else this
    }

    companion object {
        fun create(
            trigger: EventPattern,
            binding: TriggerBinding = TriggerBinding.SELF,
            effect: Effect,
            optional: Boolean = false,
            targetRequirement: TargetRequirement? = null,
            additionalTargetRequirements: List<TargetRequirement> = emptyList(),
            elseEffect: Effect? = null,
            activeZones: Set<Zone> = setOf(Zone.BATTLEFIELD),
            triggerCondition: Condition? = null,
            controlledByTriggeringEntityController: Boolean = false,
            oncePerTurn: Boolean = false,
            effectOncePerTurn: Boolean = false,
            triggersOnce: Boolean = false,
            descriptionOverride: String? = null
        ): TriggeredAbility =
            TriggeredAbility(
                id = AbilityId.generate(),
                trigger = trigger,
                binding = binding,
                effect = effect,
                optional = optional,
                targetRequirement = targetRequirement,
                additionalTargetRequirements = additionalTargetRequirements,
                elseEffect = elseEffect,
                activeZones = activeZones,
                triggerCondition = triggerCondition,
                controlledByTriggeringEntityController = controlledByTriggeringEntityController,
                oncePerTurn = oncePerTurn,
                effectOncePerTurn = effectOncePerTurn,
                triggersOnce = triggersOnce,
                descriptionOverride = descriptionOverride
            )
    }
}
