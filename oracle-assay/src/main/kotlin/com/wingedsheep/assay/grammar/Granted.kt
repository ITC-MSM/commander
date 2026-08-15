package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.Phrase
import com.wingedsheep.assay.syntax.bind
import com.wingedsheep.assay.syntax.oneOf
import com.wingedsheep.assay.syntax.phrase
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantTriggeredAbility
import com.wingedsheep.sdk.scripting.StaticAbility
import com.wingedsheep.sdk.scripting.TriggerSpec
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.Gate
import com.wingedsheep.sdk.scripting.effects.GatedEffect
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.dsl.Triggers as SdkTriggers

/**
 * "Whenever a Sliver deals combat damage to a player, its controller may draw a card." — a
 * triggered ability handed to a whole *group*, printed as one sentence.
 *
 * ### The sentence looks like a trigger and the model is a static
 *
 * Every Sliver lord in Legions prints this shape, and the SDK reads it the way the rules do: the
 * card grants an ability to each Sliver, so the model is a `GrantTriggeredAbility` static whose
 * `filter` is the group and whose `ability` carries the *ordinary* self-bound trigger. The noun in
 * "Whenever **a Sliver** deals damage" therefore lands on the grant and never on the event — which
 * is exactly the difference from [Triggers.filteredTriggerRule], where the noun *is* the event's
 * filter because no ability is being handed out.
 *
 * It is the unquoted twin of [Activated.quoted]: the same thing said two ways, "All Slivers have
 * "…"" and "Whenever a Sliver …, its controller …", and Legions prints both.
 *
 * ### "Its controller" is "you" seen from outside
 *
 * A quoted grant is written from the *gaining* permanent's point of view, so its subject is "you";
 * an unquoted one is written from the granting card's, so the same player is "its controller". The
 * model is identical — the effect's controller — and the two words are a printed-shape difference
 * with nothing in the model to hold it.
 *
 * That is why the third-person clauses are **here** rather than as alternate spellings in [Steps]:
 * outside a grant, "its controller" means the controller of whatever the sentence was just talking
 * about, which is a different player. Registering the spelling globally would read
 * "Whenever a creature dies, its controller loses 1 life" as a sentence about *you* — reversible,
 * byte-perfect and wrong, the exact class the differential exists to catch. Reachable only from a
 * grant, it can only mean what it means here.
 */
object Granted {

    /**
     * The clauses whose subject is the third-person "its controller".
     *
     * Three rules for three cards, and the module's rule says a rule that unlocks one card needs a
     * stated reason. The reason is the one above: each is the third-person spelling of a clause the
     * ordinary vocabulary already reads, and the alternative is not a smaller rule but a wrong one —
     * a global alternate that would misread every "its controller" outside a grant.
     */
    private val thirdPersonClauses: List<Phrase<CardScript>> = listOf(
        // Essence Sliver. "That much" is the damage the trigger reported; [Amounts] reads the same
        // value in the first person.
        constantClause(
            "its controller gains that much life.",
            "its controller gains that much life",
            Effects.GainLife(
                DynamicAmount.ContextProperty(ContextPropertyKey.TRIGGER_DAMAGE_AMOUNT),
                EffectTarget.Controller,
            ),
        ),
        // Synapse Sliver.
        constantClause(
            "its controller may draw a card.",
            "its controller may draw a card",
            MayEffect(Effects.DrawCards(1)),
        ),
    )

    private fun constantClause(
        template: String,
        name: String,
        effect: com.wingedsheep.sdk.scripting.effects.Effect,
    ): Phrase<CardScript> {
        val script = CardScript(spellEffect = effect)
        return phrase(template, name = name) {
            build { script }
            match { if (it == script) bind() else null }
        }
    }

    /**
     * "Its controller may create a 1/1 colorless Sliver creature token." — Brood Sliver.
     *
     * The only third-person clause with a slot in it, because the token phrase varies: [Tokens]
     * spells the whole "a 1/1 colorless Sliver creature token" and this rule contributes the subject
     * and the "may".
     */
    private val thirdPersonMayCreateToken: Phrase<CardScript> =
        phrase("its controller may {token}.", name = "its controller may create a token") {
            slot("token", Tokens.clause)
            build { bindings ->
                val inner = bindings.value<CardScript>("token").spellEffect ?: return@build null
                CardScript(spellEffect = MayEffect(inner))
            }
            match { script ->
                val gated = script.spellEffect as? GatedEffect ?: return@match null
                if (gated.gate !is Gate.MayDecide || gated != MayEffect(gated.then)) return@match null
                val inner = CardScript(spellEffect = gated.then)
                if (script != CardScript(spellEffect = MayEffect(gated.then))) return@match null
                bind("token" to inner)
            }
        }

    /** What can follow the comma in a granted trigger: a subjectless clause, or a third-person one. */
    private val effectClause: Phrase<CardScript> =
        oneOf("a granted effect clause", thirdPersonClauses + thirdPersonMayCreateToken + Steps.step)

    /**
     * The id every granted ability carries.
     *
     * One constant, for the reason [Triggers.ID] is one: the printed text does not determine it, and
     * the differential renames both sides by position before comparing.
     */
    private val ID = AbilityId("granted")

    /**
     * Build the granted ability. A "may" needs no lowering — `TriggeredAbility.optional` is gone and
     * the consent gate is the model, so a granted clause is the same value the ungranted one is
     * (see [Triggers.abilityFor], which lost the same three lines).
     */
    private fun abilityFor(spec: TriggerSpec, script: CardScript): TriggeredAbility? {
        val effect = script.spellEffect ?: return null
        if (script.targetRequirements.size > 1) return null
        return TriggeredAbility(
            id = ID,
            trigger = spec.event,
            binding = spec.binding,
            effect = effect,
            targetRequirement = script.targetRequirements.singleOrNull(),
        )
    }

    private fun scriptFor(ability: TriggeredAbility): CardScript = CardScript(
        spellEffect = ability.effect,
        targetRequirements = listOfNotNull(ability.targetRequirement),
    )

    /**
     * The shape: a noun phrase, a trigger surface, and the effect the group gains.
     *
     * `match` reconstructs the whole static and compares, so a grant carrying anything the sentence
     * does not spell — a scoped `GroupFilter`, an ability with an intervening-if, a non-battlefield
     * `activeZones` — refuses to print rather than dropping it silently.
     */
    private fun grantedTrigger(surface: String, name: String, spec: TriggerSpec): Phrase<StaticAbility> {
        fun abilityFor(filter: GameObjectFilter, script: CardScript): StaticAbility? =
            abilityFor(spec, script)?.let { GrantTriggeredAbility(it, GroupFilter(filter)) }
        return phrase("whenever {filter} $surface, {effect}", name = name) {
            slot("filter", Filters.indefinite)
            slot("effect", effectClause)
            build { abilityFor(it.value("filter"), it.value("effect")) }
            match { value ->
                val grant = value as? GrantTriggeredAbility ?: return@match null
                val script = scriptFor(grant.ability)
                val rebuilt = abilityFor(grant.filter.baseFilter, script) as? GrantTriggeredAbility
                    ?: return@match null
                if (rebuilt.copy(ability = rebuilt.ability.copy(id = grant.ability.id)) != value) {
                    return@match null
                }
                bind("filter" to grant.filter.baseFilter, "effect" to script)
            }
        }
    }

    val statics: List<Phrase<StaticAbility>> = listOf(
        grantedTrigger("deals damage", "a group's damage trigger", SdkTriggers.DealsDamage),
        grantedTrigger(
            "deals combat damage to a player",
            "a group's combat-damage-to-a-player trigger",
            SdkTriggers.DealsCombatDamageToPlayer,
        ),
        grantedTrigger(
            "deals combat damage to a creature",
            "a group's combat-damage-to-a-creature trigger",
            SdkTriggers.DealsCombatDamageToCreature,
        ),
    )
}
