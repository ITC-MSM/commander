package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.Phrase
import com.wingedsheep.assay.syntax.bind
import com.wingedsheep.assay.syntax.oneOf
import com.wingedsheep.assay.syntax.phrase
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddColorlessManaEffect
import com.wingedsheep.sdk.scripting.effects.AddDynamicManaEffect
import com.wingedsheep.sdk.scripting.effects.AddManaOfChoiceEffect
import com.wingedsheep.sdk.scripting.effects.AddManaEffect
import com.wingedsheep.sdk.scripting.effects.CompositeEffect
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
 * That the SDK needs two fields for one fact was itself a finding, and it has since been acted on:
 * 620 hand-written mana abilities set both and 24 set `isManaAbility` while leaving `timing` at its
 * `InstantSpeed` default, which mattered because the AI's `ExpiringGrantWindow` tests
 * `timing == InstantSpeed` exactly. The differential reported every card in the second group, and
 * `CardBuilder`'s `manaAbility` flag now derives the timing, so the two spellings can no longer
 * drift apart. Deriving both here is what made that reportable in the first place.
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
    private fun abilityFor(
        cost: AbilityCost,
        script: CardScript,
        restrictions: List<ActivationRestriction> = emptyList(),
        sorcerySpeed: Boolean = false,
    ): ActivatedAbility? {
        val effect = script.spellEffect ?: return null
        val targets = script.targetRequirements
        if (script != CardScript(spellEffect = effect, targetRequirements = targets)) return null
        val manaAbility = targets.isEmpty() && producesMana(effect)
        return ActivatedAbility(
            id = ID,
            cost = cost,
            effect = effect,
            targetRequirements = targets,
            timing = when {
                sorcerySpeed -> TimingRule.SorcerySpeed
                manaAbility -> TimingRule.ManaAbility
                else -> TimingRule.InstantSpeed
            },
            isManaAbility = manaAbility,
            restrictions = restrictions,
        )
    }

    /**
     * CR 605.1a's "could add mana to a player's mana pool when it resolves".
     *
     * Every effect that adds mana counts, not only the symbol form: "Add one mana of any color" and
     * "Add three mana in any combination of {R} and/or {G}" are mana abilities by the same rule, and
     * reading only the two symbol effects made Blood Celebrant, Goblin Clearcutter and Wirewood
     * Channeler come out as instant-speed abilities that go on the stack. The differential found all
     * three the first time the grammar could read them.
     *
     * **A rider does not stop it being one.** The rule says "could add mana … when it resolves", not
     * "does nothing else", so "{1}, {T}, Sacrifice this artifact: Add one mana of any color. Draw a
     * card." is a mana ability with a draw attached — Chromatic Sphere, whose own printed ruling
     * spells that out ("This is a mana ability, which means it can be activated as part of the
     * process of casting a spell … but you don't get to look at the drawn card until you have
     * finished"). Reading only the outermost effect made the whole clause an instant-speed ability
     * that goes on the stack, which is a different card. The walk therefore descends through the one
     * shape a multi-clause line builds — a `CompositeEffect` — and no further: a mana effect buried
     * under a gate or a `ForEach` is one that *might not* happen, and CR 605.1a's "could" is about
     * the ability, not about a branch the grammar has not proved reachable.
     */
    private fun producesMana(effect: Effect): Boolean = when (effect) {
        is AddManaEffect, is AddColorlessManaEffect, is AddManaOfChoiceEffect, is AddDynamicManaEffect -> true
        is CompositeEffect -> effect.effects.any(::producesMana)
        else -> false
    }

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

    /**
     * "{cost}: {effect} Activate only during your turn, before attackers are declared." — the same
     * ability with the sentence that says when it may be activated.
     *
     * A second rule rather than an optional slot, because an optional literal would leave printing
     * underdetermined between the two forms for an ability whose restriction list is empty. The two
     * take disjoint models — this one refuses an empty list — so the model decides which prints, the
     * property every alternation in this grammar is written to have.
     */
    private val restricted: Phrase<List<ActivatedAbility>> =
        phrase("{cost}: {effect} {restrictions}", name = "an activated ability with a restriction") {
            slot("cost", Costs.cost)
            slot("effect", Steps.step)
            slot("restrictions", Restrictions.activationSentence)
            build { bindings ->
                val restrictions = bindings.value<List<ActivationRestriction>>("restrictions")
                if (restrictions.isEmpty()) return@build null
                abilityFor(bindings.value("cost"), bindings.value("effect"), restrictions)?.let { listOf(it) }
            }
            match { abilities ->
                val ability = abilities.singleOrNull() ?: return@match null
                if (ability.restrictions.isEmpty()) return@match null
                val script = CardScript(
                    spellEffect = ability.effect,
                    targetRequirements = ability.targetRequirements,
                )
                if (abilityFor(ability.cost, script, ability.restrictions)?.copy(id = ability.id) != ability) {
                    return@match null
                }
                bind("cost" to ability.cost, "effect" to script, "restrictions" to ability.restrictions)
            }
        }

    /**
     * "{2}{B}, {T}, Sacrifice a Zombie: Destroy target non-Zombie creature. It can't be regenerated.
     * Activate only as a sorcery." — Deathmark Prelate.
     *
     * The same trailing sentence [restricted] reads, and yet **not** an `ActivationRestriction`: the
     * SDK spells sorcery-speed as `TimingRule.SorcerySpeed`, a field the ability already has, so the
     * sentence sets a timing rather than adding to a list. That is why it is a rule of its own and
     * not a row in [Restrictions] — a row there would produce a restriction no hand-written card
     * carries, and it would round-trip while disagreeing with every card that prints the sentence.
     */
    private val sorcerySpeed: Phrase<List<ActivatedAbility>> =
        phrase("{cost}: {effect} activate only as a sorcery.", name = "an activated ability at sorcery speed") {
            slot("cost", Costs.cost)
            slot("effect", Steps.step)
            build { bindings ->
                abilityFor(bindings.value("cost"), bindings.value("effect"), sorcerySpeed = true)
                    ?.let { listOf(it) }
            }
            match { abilities ->
                val ability = abilities.singleOrNull() ?: return@match null
                if (ability.timing != TimingRule.SorcerySpeed) return@match null
                val script = CardScript(
                    spellEffect = ability.effect,
                    targetRequirements = ability.targetRequirements,
                )
                if (abilityFor(ability.cost, script, sorcerySpeed = true)?.copy(id = ability.id) != ability) {
                    return@match null
                }
                bind("cost" to ability.cost, "effect" to script)
            }
        }

    val abilities: Phrase<List<ActivatedAbility>> =
        oneOf("an activated ability", single, restricted, sorcerySpeed, choice)

    /**
     * `"{T}: Regenerate target Sliver."` — one activated ability inside the quotation marks a
     * *granted* ability is printed in.
     *
     * The quotes are the whole rule. Everything inside them is [abilities] unchanged, which is the
     * point: a granted ability is the same English an ability line prints, so `Statics`' lord rules
     * slot this and inherit the entire activated-ability grammar rather than restating a verb.
     *
     * Exactly one ability, because `GrantActivatedAbility` holds one. The list form the line rule
     * needs — "{T}: Add {B} or {G}." as two abilities sharing a cost — has nowhere to go in a grant,
     * so it declines here rather than being silently truncated to its first member.
     *
     * Note what the quoted text does **not** mean: `~` inside a granted ability is the creature that
     * gained it, not the card whose line this is. Normalization abstracts both to the same token and
     * records that as a known limitation; nothing here reads `~` as the source, and the effect
     * vocabulary spells it [com.wingedsheep.sdk.scripting.targets.EffectTarget.Self] either way,
     * which is the reading that stays true in both positions.
     */
    val quoted: Phrase<ActivatedAbility> = phrase("\"{ability}\"", name = "a quoted activated ability") {
        slot("ability", abilities)
        build { it.value<List<ActivatedAbility>>("ability").singleOrNull() }
        match { bind("ability" to listOf(it)) }
    }
}
