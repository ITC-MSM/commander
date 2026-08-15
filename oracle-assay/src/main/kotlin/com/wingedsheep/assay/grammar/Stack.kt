package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.Phrase
import com.wingedsheep.assay.syntax.bind
import com.wingedsheep.assay.syntax.constant
import com.wingedsheep.assay.syntax.oneOf
import com.wingedsheep.assay.syntax.phrase
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.scripting.effects.CounterEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetSpell

/**
 * Clauses about the stack — countering a spell.
 *
 * The one clause family whose target is a *spell* rather than a permanent, a player or a card, which
 * is why it is its own file rather than a row in [Steps]: `TargetSpell` is a `TargetObject` scoped to
 * `Zone.STACK`, and [Targets.permanent]'s inverse refuses anything that is not on the battlefield —
 * deliberately, so that "destroy target creature" cannot print a script aimed at the stack.
 *
 * ### The spell type phrase is enumerated
 *
 * "Target creature or sorcery spell" is `GameObjectFilter.CreatureOrSorcery` — an ordered `Or` — and
 * "target creature spell" is one predicate, which is the same shape problem [Filters] states for its
 * own type list: English does not distinguish them except by the words. So the spell nouns are rows
 * here rather than [Filters.filter] slotted whole, and a form nobody wrote down declines. They are
 * *not* [Filters] rows because the noun ends in "spell" and the zone is part of the requirement
 * rather than of the filter — a permanent noun in this position would build a battlefield target.
 */
object Stack {

    private val spellFilter: Phrase<TargetFilter> = oneOf(
        "a spell on the stack",
        constant("spell", TargetFilter.SpellOnStack),
        constant("creature or sorcery spell", TargetFilter.CreatureOrSorcerySpellOnStack),
        constant("creature spell", TargetFilter(com.wingedsheep.sdk.scripting.GameObjectFilter.Creature, zone = Zone.STACK)),
        constant("instant spell", TargetFilter.InstantSpellOnStack),
    )

    /** "Counter target creature or sorcery spell." — Mystic Denial. */
    private val counter: Phrase<CardScript> = run {
        fun scriptFor(filter: TargetFilter) = CardScript(
            spellEffect = CounterEffect(),
            targetRequirements = listOf(TargetSpell(filter = filter, id = Targets.SLOT)),
        )
        phrase("counter target {filter}", name = "counter a spell") {
            slot("filter", spellFilter)
            build { scriptFor(it.value("filter")) }
            match { script ->
                val requirement = script.targetRequirements.singleOrNull()
                    as? com.wingedsheep.sdk.scripting.targets.TargetObject ?: return@match null
                if (script != scriptFor(requirement.filter)) return@match null
                bind("filter" to requirement.filter)
            }
        }
    }

    val clauses: List<Phrase<CardScript>> = listOf(counter)
}
