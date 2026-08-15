package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.Phrase
import com.wingedsheep.assay.syntax.bind
import com.wingedsheep.assay.syntax.oneOf
import com.wingedsheep.assay.syntax.phrase
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddColorlessManaEffect
import com.wingedsheep.sdk.scripting.effects.AddManaEffect
import com.wingedsheep.sdk.scripting.effects.Effect

/**
 * "{T}: Add {G}." — the cost-colon-effect sentence, and the third `CardScript` slot the grammar
 * reaches.
 *
 * ### The clause after the colon is [Steps], not a second effect vocabulary
 *
 * The rule slots [Steps.step] whole and lifts its `CardScript` onto the ability, exactly as
 * [Triggers] does: the effect becomes the ability's effect and a target it declared becomes the
 * ability's `targetRequirements`. So `{T}: Draw a card.` and `{2}: Target creature gets +1/+1
 * until end of turn.` come for free with the mana lines, and every future step rule enriches this
 * file without touching it. The capital on "Add" is not this rule's business either — an ability's
 * effect clause is a sentence start, which [com.wingedsheep.assay.syntax.SentenceCase] owns for
 * the same reason it owns the capital on the line's first word.
 *
 * ### Mana-ability-ness is derived from the effect, because CR 605.1a derives it
 *
 * A `CardDefinition` carries the fact twice — `isManaAbility` and `timing = ManaAbility` — and no
 * printed text says either. CR 605.1a defines a mana ability as one that "doesn't require a
 * target, … could add mana to a player's mana pool when it resolves, and … isn't a loyalty
 * ability", which is a property of the ability rather than a word in the sentence. The rule
 * therefore computes both flags from the effect and the target list rather than spelling them, and
 * an ability that disagrees with the derivation refuses to print.
 *
 * That the SDK needs two fields for one fact is itself a finding: 620 hand-written mana abilities
 * set both, and 24 set `isManaAbility` while leaving `timing` at its `InstantSpeed` default. The
 * engine reads `isManaAbility` and only ever compares `timing` against `SorcerySpeed`, so nothing
 * is broken — but they are not interchangeable in general (the AI's `ExpiringGrantWindow` tests
 * `timing == InstantSpeed` exactly), so the grammar emits the majority form and lets the
 * differential report the rest rather than folding them together.
 *
 * ### One line can be several abilities
 *
 * "{T}: Add {B} or {G}." is two abilities sharing a cost — see [Mana] for why, and
 * [Keywords.qualityRun] for the shape. The rules therefore hand back a *list*, and the single case
 * is the one-element member of it.
 */
object Activated {

    /**
     * The id every parsed ability carries.
     *
     * One constant rather than a generator, for the reason [Triggers]' is one: the printed text
     * does not determine it, so any value is as right as any other, and the differential renames
     * both sides by position before comparing.
     */
    private val ID = AbilityId("activated")

    /**
     * Build the ability a cost and an effect clause denote, or null when the clause carries
     * something an activated ability has nowhere to put.
     *
     * Shared by both directions so `match` can reconstruct and compare the whole value: an ability
     * carrying a restriction, an activation zone, a `descriptionOverride`, convoke, exhaust or any
     * of the two dozen other fields on `ActivatedAbility` fails the equality and refuses to print,
     * rather than printing a sentence that quietly drops it. Only the id is exempt, because the id
     * is not in the text.
     */
    private fun abilityFor(cost: AbilityCost, script: CardScript): ActivatedAbility? {
        val effect = script.spellEffect ?: return null
        val targets = script.targetRequirements
        if (script != CardScript(spellEffect = effect, targetRequirements = targets)) return null
        val manaAbility = targets.isEmpty() && producesMana(effect)
        return ActivatedAbility(
            id = ID,
            cost = cost,
            effect = effect,
            targetRequirements = targets,
            timing = if (manaAbility) TimingRule.ManaAbility else TimingRule.InstantSpeed,
            isManaAbility = manaAbility,
        )
    }

    /** CR 605.1a's "could add mana to a player's mana pool when it resolves", as far as [Mana] reads. */
    private fun producesMana(effect: Effect): Boolean =
        effect is AddManaEffect || effect is AddColorlessManaEffect

    /** "{cost}: {effect}" — one ability, whatever [Steps] can read after the colon. */
    private val single: Phrase<List<ActivatedAbility>> =
        phrase("{cost}: {effect}", name = "an activated ability") {
            slot("cost", Costs.cost)
            slot("effect", Steps.step)
            build { bindings ->
                abilityFor(bindings.value("cost"), bindings.value("effect"))?.let { listOf(it) }
            }
            match { abilities ->
                val ability = abilities.singleOrNull() ?: return@match null
                val script = CardScript(
                    spellEffect = ability.effect,
                    targetRequirements = ability.targetRequirements,
                )
                if (abilityFor(ability.cost, script)?.copy(id = ability.id) != ability) return@match null
                bind("cost" to ability.cost, "effect" to script)
            }
        }

    /**
     * "{cost}: Add {B} or {G}." — several abilities, one per kind of mana, sharing the cost.
     *
     * Not a member of [single] over a list-valued effect slot: the abilities differ only in their
     * effect, and every other field — including the derived mana-ability flags — has to come out
     * identical for the line to be printable at all, which is what the reconstruct-and-compare in
     * `match` checks one ability at a time.
     */
    private val choice: Phrase<List<ActivatedAbility>> =
        phrase("{cost}: {alternatives}", name = "an activated mana ability with a choice") {
            slot("cost", Costs.cost)
            slot("alternatives", Mana.addedAlternatives)
            build { bindings ->
                val cost = bindings.value<AbilityCost>("cost")
                val built = bindings.value<List<Effect>>("alternatives")
                    .map { abilityFor(cost, CardScript(spellEffect = it)) }
                if (built.any { it == null }) null else built.filterNotNull()
            }
            match { abilities ->
                if (abilities.size < 2) return@match null
                val cost = abilities.first().cost
                val printable = abilities.all { ability ->
                    ability.cost == cost &&
                        abilityFor(cost, CardScript(spellEffect = ability.effect))
                            ?.copy(id = ability.id) == ability
                }
                if (!printable) return@match null
                bind("cost" to cost, "alternatives" to abilities.map { it.effect })
            }
        }

    val abilities: Phrase<List<ActivatedAbility>> = oneOf("an activated ability", single, choice)
}
