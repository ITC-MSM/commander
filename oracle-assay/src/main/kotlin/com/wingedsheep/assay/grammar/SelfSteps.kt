package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.Phrase
import com.wingedsheep.assay.syntax.bind
import com.wingedsheep.assay.syntax.phrase
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.costs.CostAtom
import com.wingedsheep.sdk.scripting.costs.PayCost
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.effects.Effect
import com.wingedsheep.sdk.scripting.effects.PayOrSufferEffect
import com.wingedsheep.sdk.scripting.effects.SacrificeSelfEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Clauses about the **source** — "it gets +2/+0 until end of turn.", "put it on top of its owner's
 * library.", "sacrifice it unless you discard a land card."
 *
 * Oracle's "it" here is the permanent whose ability this is, which is [EffectTarget.Self] and needs
 * no earlier sentence to introduce it — so unlike [Continuations] these are ordinary clauses that
 * can stand alone. The two anaphors are kept in separate vocabularies precisely because they point
 * at different things: "that creature" is the target the spell already chose, "it" is the source.
 *
 * Almost every card that prints one of these prints it inside a triggered ability ("When this
 * creature dies, put it on top of its owner's library"), and none of these rules knows that:
 * [Triggers] slots [Steps.step] whole, so the clause is the same clause wherever it lands.
 */
object SelfSteps {

    /** "It gets +2/+0 until end of turn." — Charging Bandits' attack trigger. */
    private val itGets: Phrase<CardScript> = run {
        fun scriptFor(modifiers: Pair<Int, Int>) = CardScript(
            spellEffect = Effects.ModifyStats(modifiers.first, modifiers.second, EffectTarget.Self)
        )
        phrase("it gets {mod} until end of turn", name = "the source gets") {
            slot("mod", Primitives.statModifiers)
            build { scriptFor(it.value("mod")) }
            match { script ->
                val modifiers = Steps.fixedModifiers(script.spellEffect) ?: return@match null
                if (script != scriptFor(modifiers)) return@match null
                bind("mod" to modifiers)
            }
        }
    }

    /** "Put it on top of its owner's library." — Undying Beast's death trigger. */
    private val putOnTop: Phrase<CardScript> = run {
        val script = CardScript(spellEffect = Effects.PutOnTopOfLibrary(EffectTarget.Self))
        phrase("put it on top of its owner's library", name = "put the source on top of its library") {
            build { script }
            match { if (it == script) bind() else null }
        }
    }

    /**
     * "Sacrifice it unless you discard a land card." — the Portal drawback, and one shape with two
     * costs.
     *
     * `PayOrSufferEffect` is the SDK's name for the whole sentence: a cost the controller may pay
     * and the thing that happens if they do not. The two members differ only in the [PayCost], which
     * is why the rule is a function of the cost's surface and both halves of the cost — the printed
     * noun phrase goes through [Filters.indefinite] so the article comes out right, and the cost is
     * reconstructed from the filter for the comparison.
     */
    private fun sacrificeUnless(
        template: String,
        name: String,
        cost: (GameObjectFilter) -> PayCost,
    ): Phrase<CardScript> {
        fun scriptFor(filter: GameObjectFilter) = CardScript(
            spellEffect = PayOrSufferEffect(cost = cost(filter), suffer = SacrificeSelfEffect)
        )
        return phrase(template, name = name) {
            slot("filter", Filters.indefinite)
            build { scriptFor(it.value("filter")) }
            match { script ->
                val effect = script.spellEffect as? PayOrSufferEffect ?: return@match null
                val filter = paidFilter(effect.cost) ?: return@match null
                if (script != scriptFor(filter)) return@match null
                bind("filter" to filter)
            }
        }
    }

    /** The filter a one-atom pay cost names, or null when the cost is anything more complicated. */
    private fun paidFilter(cost: PayCost): GameObjectFilter? {
        val atom = (cost as? PayCost.Atom)?.atom ?: return null
        return when (atom) {
            is CostAtom.Discard -> atom.filter
            is CostAtom.Sacrifice -> atom.filter
            else -> null
        }
    }

    /** The moves whose object is the source and whose destination the template names. */
    private fun moveSelf(template: String, name: String, effect: Effect): Phrase<CardScript> {
        val script = CardScript(spellEffect = effect)
        return phrase(template, name = name) {
            build { script }
            match { if (it == script) bind() else null }
        }
    }

    /**
     * "Sacrifice it unless you sacrifice three Forests." — the counted cost, Primeval Force's.
     *
     * A row of the [sacrificeUnless] shape would need the count in the cost *and* a plural noun in
     * the text, which changes both slots; the singular rules keep the noun singular and this one
     * carries the number, which is the same singular/plural split every counting rule here makes.
     */
    private val sacrificeUnlessCounted: Phrase<CardScript> = run {
        fun scriptFor(count: Int, filter: GameObjectFilter) = CardScript(
            spellEffect = PayOrSufferEffect(
                cost = Costs.pay.Sacrifice(filter, count = count),
                suffer = SacrificeSelfEffect,
            )
        )
        phrase(
            "sacrifice it unless you sacrifice {n} {filter}",
            name = "sacrifice the source unless you sacrifice several",
        ) {
            slot("n", Cardinals.word)
            slot("filter", Filters.plural)
            build { scriptFor(it.int("n"), it.value("filter")) }
            match { script ->
                val effect = script.spellEffect as? PayOrSufferEffect ?: return@match null
                val atom = (effect.cost as? PayCost.Atom)?.atom as? CostAtom.Sacrifice ?: return@match null
                if (!Cardinals.spellable(atom.count)) return@match null
                if (script != scriptFor(atom.count, atom.filter)) return@match null
                bind("n" to atom.count, "filter" to atom.filter)
            }
        }
    }

    /** "Sacrifice it unless you discard a card at random." — Pillaging Horde. */
    private val sacrificeUnlessRandomDiscard: Phrase<CardScript> = run {
        val script = CardScript(
            spellEffect = PayOrSufferEffect(
                cost = Costs.pay.Discard(random = true),
                suffer = SacrificeSelfEffect,
            )
        )
        phrase(
            "sacrifice it unless you discard a card at random",
            name = "sacrifice the source unless you discard at random",
        ) {
            build { script }
            match { if (it == script) bind() else null }
        }
    }

    /**
     * The clauses whose "it" is the **source**.
     *
     * Kept apart from the rest because English resolves an anaphor to the most recently mentioned
     * object: in "Whenever this creature attacks, it gets +2/+0" the only mention is the source, but
     * in "Untap target creature. It gets +2/+4 until end of turn." it is the target the first clause
     * introduced. So these rules are clauses in their own right and are *not* offered in a later
     * position of a sequence — [Continuations] owns "it" there. Registering them in both places
     * would be two readings of one text, which is ambiguity rather than a choice.
     */
    val anaphoric: List<Phrase<CardScript>> = listOf(
        itGets,
        putOnTop,
        sacrificeUnlessCounted,
        sacrificeUnlessRandomDiscard,
        moveSelf(
            "shuffle it into its owner's library",
            "shuffle the source into its library",
            Effects.Move(EffectTarget.Self, Zone.LIBRARY, ZonePlacement.Shuffled),
        ),
        moveSelf(
            "return it to its owner's hand",
            "return the source to its owner's hand",
            Effects.Move(EffectTarget.Self, Zone.HAND),
        ),
        sacrificeUnless(
            "sacrifice it unless you discard {filter} card",
            "sacrifice the source unless you discard",
        ) { Costs.pay.Discard(filter = it) },
        sacrificeUnless(
            "sacrifice it unless you sacrifice {filter}",
            "sacrifice the source unless you sacrifice",
        ) { Costs.pay.Sacrifice(it) },
    )

    /** Everything in this file that does not turn on the pronoun. Empty for now; the family is "it". */
    val clauses: List<Phrase<CardScript>> = emptyList()
}
