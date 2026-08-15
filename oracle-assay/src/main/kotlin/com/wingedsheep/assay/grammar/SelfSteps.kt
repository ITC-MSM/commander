package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.Phrase
import com.wingedsheep.assay.syntax.alternate
import com.wingedsheep.assay.syntax.bind
import com.wingedsheep.assay.syntax.phrase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.costs.CostAtom
import com.wingedsheep.sdk.scripting.costs.PayCost
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.effects.Effect
import com.wingedsheep.sdk.scripting.effects.CompositeEffect
import com.wingedsheep.sdk.scripting.effects.PayOrSufferEffect
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.effects.RemoveKeywordEffect
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

    /**
     * "This creature gets +1/+1 until end of turn." — firebreathing's effect clause, and Charging
     * Bandits' attack trigger spelled with the pronoun.
     *
     * The subject is [Primitives.self], so both of Oracle's spellings read and the noun is what
     * prints. That ordering is the corpus's: a card *naming* itself is how nearly every activated
     * pump is templated ("{R}: This creature gets +1/+0 until end of turn."), while the pronoun only
     * appears where an earlier clause in the same ability already named the source. Cards printing
     * the pronoun come back as a [com.wingedsheep.assay.gate.LineVerdict.VARIANT], which says the
     * reading was right and only the spelling moved.
     */
    /**
     * "Put a +1/+1 counter on ~.", "Put two +1/+1 counters on it." — the counter verb aimed at the
     * source, and the single commonest effect shape in the whole hand-written corpus: 363 of the
     * 951 `AddCounters` a golden carries are exactly this one.
     *
     * The subject is [Primitives.self], so both spellings read and the name is what prints, the same
     * treatment [selfGets] gets. Being in [anaphoric] is what makes the pronoun safe: [Steps] drops
     * this whole list from every position after the first in a sequence, so once a clause has
     * introduced a target, "on it" is [Continuations]' to read and means that target. Registering the
     * pronoun in both places would be two readings of one text — the bug the differential caught on
     * "Untap target creature. It gets +2/+4", in a sentence where it would be just as invisible.
     *
     * Singular and plural are two rules over disjoint quantities for [Steps]' reason; everything
     * about why is written there, on the targeted twin of this pair.
     */
    private val putCountersOnSelf: List<Phrase<CardScript>> = run {
        fun scriptFor(kind: String, count: Int) =
            CardScript(spellEffect = Effects.AddCounters(kind, count, EffectTarget.Self))
        fun rule(template: String, name: String, quantity: Phrase<*>?) =
            phrase(template, name = name) {
                slot("kind", if (quantity == null) Primitives.singularCounterKind else Primitives.counterKind)
                if (quantity != null) slot("n", quantity)
                slot("self", Primitives.self)
                build { scriptFor(it.value("kind"), if (quantity == null) 1 else it.int("n")) }
                match { script ->
                    val (kind, count) =
                        Steps.countersAdded(script.spellEffect, EffectTarget.Self) ?: return@match null
                    if (quantity == null && count != 1) return@match null
                    if (quantity != null && !(count >= 2 && Cardinals.spellable(count))) return@match null
                    if (script != scriptFor(kind, count)) return@match null
                    bind("kind" to kind, "n" to count, "self" to Unit)
                }
            }
        listOf(
            rule("put {kind} counter on {self}", "put a counter on the source", null),
            rule("put {n} {kind} counters on {self}", "put counters on the source", Cardinals.word),
        )
    }

    private val selfGets: Phrase<CardScript> = run {
        fun scriptFor(modifiers: Pair<Int, Int>) = CardScript(
            spellEffect = Effects.ModifyStats(modifiers.first, modifiers.second, EffectTarget.Self)
        )
        phrase("{self} gets {mod} until end of turn", name = "the source gets") {
            slot("self", Primitives.self)
            slot("mod", Primitives.statModifiers)
            build { scriptFor(it.value("mod")) }
            match { script ->
                val modifiers = Steps.fixedModifiers(script.spellEffect) ?: return@match null
                if (script != scriptFor(modifiers)) return@match null
                bind("self" to Unit, "mod" to modifiers)
            }
        }
    }

    /**
     * "This creature gets +2/+2 and gains trample until end of turn." — Clickslither, Glintwing
     * Invoker, Unstable Hulk.
     *
     * [Steps.pumpAndGrantTarget]'s source-side twin, and one rule for the same reason: the second
     * clause has no subject of its own in the text, and the model is a two-element composite over
     * one object. A [Steps.sequence] would need the second clause to name what it acts on.
     */
    private val selfGetsAndGains: Phrase<CardScript> = run {
        fun scriptFor(modifiers: Pair<Int, Int>, keyword: Keyword) = CardScript(
            spellEffect = Effects.Composite(
                listOf(
                    Effects.ModifyStats(modifiers.first, modifiers.second, EffectTarget.Self),
                    Effects.GrantKeyword(keyword, EffectTarget.Self),
                )
            )
        )
        phrase("{self} gets {mod} and gains {kw} until end of turn", name = "the source gets and gains") {
            slot("self", Primitives.self)
            slot("mod", Primitives.statModifiers)
            slot("kw", Keywords.keyword)
            build { scriptFor(it.value("mod"), it.value("kw")) }
            match { script ->
                val effects = (script.spellEffect as? CompositeEffect)?.effects ?: return@match null
                val modifiers = Steps.fixedModifiers(effects.firstOrNull()) ?: return@match null
                val keyword = Steps.grantedKeyword(effects.getOrNull(1)) ?: return@match null
                if (script != scriptFor(modifiers, keyword)) return@match null
                bind("self" to Unit, "mod" to modifiers, "kw" to keyword)
            }
        }
    }

    /** "~ gains flying and shroud until end of turn." — Warped Researcher. Two grants, one sentence. */
    private val selfGainsTwoKeywords: Phrase<CardScript> = run {
        fun scriptFor(first: Keyword, second: Keyword) = CardScript(
            spellEffect = Effects.Composite(
                listOf(
                    Effects.GrantKeyword(first, EffectTarget.Self),
                    Effects.GrantKeyword(second, EffectTarget.Self),
                )
            )
        )
        phrase("{self} gains {kw} and {kw2} until end of turn", name = "the source gains two keywords") {
            slot("self", Primitives.self)
            slot("kw", Keywords.keyword)
            slot("kw2", Keywords.keyword)
            build { scriptFor(it.value("kw"), it.value("kw2")) }
            match { script ->
                val effects = (script.spellEffect as? CompositeEffect)?.effects ?: return@match null
                val first = Steps.grantedKeyword(effects.firstOrNull()) ?: return@match null
                val second = Steps.grantedKeyword(effects.getOrNull(1)) ?: return@match null
                if (script != scriptFor(first, second)) return@match null
                bind("self" to Unit, "kw" to first, "kw2" to second)
            }
        }
    }

    /** "~ loses flying until end of turn." — Swooping Talon, the grant rules' negation. */
    private val selfLosesKeyword: Phrase<CardScript> = run {
        fun scriptFor(keyword: Keyword) =
            CardScript(spellEffect = Effects.RemoveKeyword(keyword, EffectTarget.Self))
        phrase("{self} loses {kw} until end of turn", name = "the source loses a keyword") {
            slot("self", Primitives.self)
            slot("kw", Keywords.keyword)
            build { scriptFor(it.value("kw")) }
            match { script ->
                val removal = script.spellEffect as? RemoveKeywordEffect ?: return@match null
                val keyword = Keyword.entries.firstOrNull { it.name == removal.keyword } ?: return@match null
                if (script != scriptFor(keyword)) return@match null
                bind("self" to Unit, "kw" to keyword)
            }
        }
    }

    /**
     * "Sacrifice ~ unless you pay {G}{G}." — Krosan Cloudscraper's upkeep tax.
     *
     * A row of the [sacrificeUnless] shape over a *mana* cost rather than a permanent one, which is
     * why it is written out: the cost has no noun phrase and therefore no article, so
     * [Filters.indefinite] has nothing to do and the slot is a bare mana symbol run.
     */
    private val sacrificeUnlessPay: Phrase<CardScript> = run {
        fun scriptFor(cost: ManaCost) = CardScript(
            spellEffect = PayOrSufferEffect(cost = Costs.pay.Mana(cost), suffer = SacrificeSelfEffect)
        )
        phrase("sacrifice {self} unless you pay {cost}", name = "sacrifice the source unless you pay") {
            slot("self", Primitives.self)
            slot("cost", Primitives.manaCost)
            build { scriptFor(it.value("cost")) }
            match { script ->
                val effect = script.spellEffect as? PayOrSufferEffect ?: return@match null
                val cost = ((effect.cost as? PayCost.Atom)?.atom as? CostAtom.Mana)?.cost ?: return@match null
                if (script != scriptFor(cost)) return@match null
                bind("self" to Unit, "cost" to cost)
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

    /**
     * The verbs whose object is the source and which carry nothing else — a move to a named zone,
     * an untap, a regeneration.
     *
     * The subject slot is optional in the template because the older members spell the pronoun as a
     * literal ("return **it** to its owner's hand"), while the ones a card names itself in take
     * [Primitives.self]. Both are the same rule shape; only the printed subject differs.
     */
    private fun moveSelf(template: String, name: String, effect: Effect): Phrase<CardScript> {
        val script = CardScript(spellEffect = effect)
        return phrase(template, name = name) {
            if (template.contains("{self}")) slot("self", Primitives.self)
            build { script }
            match { if (it == script) bind("self" to Unit) else null }
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
        selfGets,
        selfGetsAndGains,
        selfGainsTwoKeywords,
        selfLosesKeyword,
        sacrificeUnlessPay,
        putOnTop,
        // The two bare verbs whose object is the source. "Untap it." after a morph trigger and
        // "Regenerate ~." as an activated ability's whole effect are the same shape as the moves
        // below, differing only in that the effect takes no destination.
        moveSelf("untap {self}", "untap the source", Effects.Untap(EffectTarget.Self)),
        moveSelf("regenerate {self}", "regenerate the source", RegenerateEffect(EffectTarget.Self)),
        sacrificeUnlessCounted,
        sacrificeUnlessRandomDiscard,
        moveSelf(
            "shuffle it into its owner's library",
            "shuffle the source into its library",
            Effects.Move(EffectTarget.Self, Zone.LIBRARY, ZonePlacement.Shuffled),
        ),
        moveSelf("exile it", "exile the source", Effects.Move(EffectTarget.Self, Zone.EXILE)),
        moveSelf(
            "return it to its owner's hand",
            "return the source to its owner's hand",
            Effects.Move(EffectTarget.Self, Zone.HAND),
        ),
        // "…return it to your hand." — Ghastly Remains. The same move: a card returning itself goes
        // to its owner's hand, and the owner of a card you are returning from your own graveyard is
        // you. Two printed forms, one model, so this one parses and never prints.
        alternate(
            moveSelf(
                "return it to your hand",
                "return the source to your hand",
                Effects.Move(EffectTarget.Self, Zone.HAND),
            )
        ),
        sacrificeUnless(
            "sacrifice it unless you discard {filter} card",
            "sacrifice the source unless you discard",
        ) { Costs.pay.Discard(filter = it) },
        sacrificeUnless(
            "sacrifice it unless you sacrifice {filter}",
            "sacrifice the source unless you sacrifice",
        ) { Costs.pay.Sacrifice(it) },
    ) + putCountersOnSelf

    /** Everything in this file that does not turn on the pronoun. Empty for now; the family is "it". */
    val clauses: List<Phrase<CardScript>> = emptyList()
}
