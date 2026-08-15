package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.normalize.Normalizer
import com.wingedsheep.assay.syntax.Phrase
import com.wingedsheep.assay.syntax.alternate
import com.wingedsheep.assay.syntax.bind
import com.wingedsheep.assay.syntax.constant
import com.wingedsheep.assay.syntax.oneOf
import com.wingedsheep.assay.syntax.phrase
import com.wingedsheep.assay.syntax.separated
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.costs.CostAtom
import com.wingedsheep.sdk.dsl.Costs as SdkCosts

/**
 * What an activated ability costs — the clause before the colon.
 *
 * ### An atom, and a comma-joined run of them
 *
 * `{T}` is one cost, `{2}{B}, {T}, Sacrifice a Goblin` is three, and the SDK spells the second as
 * `AbilityCost.Composite` of the first kind. So this file is an **atom vocabulary** plus one run
 * rule, rather than a rule per whole cost shape: `{1}, {T}` stopped being a rule of its own the
 * moment a third atom appeared, and every future atom (a discard, a counter removal, an exile from
 * a graveyard) is a row in [atoms] that every multi-atom cost gets for free.
 *
 * The single-atom case is the atom itself and **not** a one-element `Composite`, because that is
 * what hand-written cards carry — `AbilityCost.Composite` with one member is a value no card in the
 * corpus has. [cost] therefore takes disjoint models on its two alternatives and printing is decided
 * by the value rather than by the alternation's order.
 *
 * ### The ordering inside a composite is the printed one
 *
 * `{2}, {T}` is `Composite([Mana, Tap])` and not `Composite([Tap, Mana])`, because that is what
 * Cabal Coffers and every other hand-written card carries, and a `Composite` is a list rather than
 * a set. The run rule preserves printed order in both directions, which is the property that makes
 * it safe to generalize away from the enumerated pairs — reading it the other way round would
 * round-trip and disagree with every card, the reversible-but-wrong class.
 *
 * ### A cost is the one clause Oracle capitalizes that is not a sentence start
 *
 * Every other template in this grammar is written mid-sentence, because
 * [com.wingedsheep.assay.syntax.SentenceCase] owns the capital at each sentence start and a clause
 * can appear at one or not. A cost atom is the opposite: Oracle capitalizes it *everywhere*
 * ("Sacrifice a Goblin: …", "{T}, Sacrifice a Forest: …"), and `SentenceCase` lowercases only the
 * one at the line's start. So the capitalized spelling is canonical, the lowercase one is an
 * [alternate] reachable only where the pass put it, and both print back byte-exactly — the line
 * start because `capitalize` restores it, and every other position because it was never touched.
 * [bothCases] is that pairing, and it is why a cost rule is a function of its verb's spelling.
 *
 * ### `{T}` is not a mana cost, and the SDK is what says so
 *
 * The tap rule and the mana rule can both be offered at the same offset without ambiguity because
 * `ManaCost.parse("{T}")` throws — a symbol the SDK's mana vocabulary has no place for makes
 * [Primitives.manaCost] decline rather than invent a reading. The two rules are therefore disjoint
 * by the SDK's own type rather than by an ordering in the alternation.
 */
object Costs {

    /**
     * A cost rule in both the spelling Oracle prints and the one the line-start pass leaves behind.
     *
     * [rule] is a function of its leading word so that the two halves cannot drift: there is one
     * template, instantiated twice, and the lowercase instance can never print.
     */
    private fun bothCases(word: String, name: String, rule: (String) -> Phrase<AbilityCost>): Phrase<AbilityCost> =
        oneOf(name, rule(word.replaceFirstChar { it.uppercaseChar() }), alternate(rule(word)))

    private val tap: Phrase<AbilityCost> = constant("{T}", AbilityCost.Tap)

    private val mana: Phrase<AbilityCost> = phrase("{cost}", name = "a mana cost") {
        slot("cost", Primitives.manaCost)
        build { SdkCosts.Mana(it.value<ManaCost>("cost")) }
        match { cost -> manaCostOf(cost)?.let { bind("cost" to it) } }
    }

    /**
     * "Sacrifice this creature", "Exile this creature" — the source paying with itself.
     *
     * Their own `AbilityCost` cases rather than a `Sacrifice` over a source-scoped filter, which is
     * why they are constants here and not rows of [sacrificeFiltered].
     */
    private val sacrificeSelf: Phrase<AbilityCost> =
        bothCases("sacrifice", "sacrifice this permanent") { verb ->
            constant("$verb ${Normalizer.SELF}", AbilityCost.SacrificeSelf)
        }

    private val exileSelf: Phrase<AbilityCost> =
        bothCases("exile", "exile this permanent") { verb ->
            constant("$verb ${Normalizer.SELF}", AbilityCost.ExileSelf)
        }

    /**
     * "Sacrifice a Goblin", "Sacrifice a creature" — one permanent matching a filter.
     *
     * The article comes from [Filters.indefinite], which derives it from the noun's spelling in both
     * directions; the model has nowhere to keep it. `count` is checked against 1 here and the plural
     * rule below refuses 1, so the two take disjoint models and one printed form exists per value.
     */
    private val sacrificeFiltered: Phrase<AbilityCost> =
        bothCases("sacrifice", "sacrifice a permanent") { verb ->
            phrase("$verb {filter}", name = "sacrifice a permanent") {
                slot("filter", Filters.indefinite)
                build { SdkCosts.Sacrifice(it.value("filter")) }
                match { cost ->
                    val atom = sacrificeAtom(cost) ?: return@match null
                    if (atom.count != 1 || cost != SdkCosts.Sacrifice(atom.filter)) return@match null
                    bind("filter" to atom.filter)
                }
            }
        }

    /** "Sacrifice three Clerics" — Dark Supplicant. The counted sibling, over a plural noun. */
    private val sacrificeSeveral: Phrase<AbilityCost> =
        bothCases("sacrifice", "sacrifice several permanents") { verb ->
            phrase("$verb {n} {filter}", name = "sacrifice several permanents") {
                slot("n", Cardinals.word)
                slot("filter", Filters.plural)
                build { SdkCosts.SacrificeMultiple(it.int("n"), it.value("filter")) }
                match { cost ->
                    val atom = sacrificeAtom(cost) ?: return@match null
                    if (!Cardinals.spellable(atom.count)) return@match null
                    if (cost != SdkCosts.SacrificeMultiple(atom.count, atom.filter)) return@match null
                    bind("n" to atom.count, "filter" to atom.filter)
                }
            }
        }

    /**
     * "Tap two untapped Birds you control" — Crookclaw Elder, Keeper of the Nine Gales.
     *
     * "Untapped" and "you control" are **literals** rather than parts of the noun phrase, because
     * `CostAtom.TapPermanents` carries neither: a tap cost can only tap untapped permanents you
     * control, so the words restate the cost's own rules and the filter holds only what is left.
     * Slotting them would print a filter the atom cannot hold.
     */
    private val tapPermanents: Phrase<AbilityCost> =
        bothCases("tap", "tap several permanents") { verb ->
            phrase("$verb {n} untapped {filter} you control", name = "tap several permanents") {
                slot("n", Cardinals.word)
                slot("filter", Filters.plural)
                build { SdkCosts.TapPermanents(it.int("n"), it.value("filter")) }
                match { cost ->
                    val atom = (cost as? AbilityCost.Atom)?.atom as? CostAtom.TapPermanents ?: return@match null
                    if (!Cardinals.spellable(atom.count)) return@match null
                    if (cost != SdkCosts.TapPermanents(atom.count, atom.filter)) return@match null
                    bind("n" to atom.count, "filter" to atom.filter)
                }
            }
        }

    /** "Pay 1 life" — Blood Celebrant. A numeral, per Oracle's convention for quantities of life. */
    private val payLife: Phrase<AbilityCost> =
        bothCases("pay", "pay life") { verb ->
            phrase("$verb {n} life", name = "pay life") {
                slot("n", Primitives.cardinal)
                build { SdkCosts.PayLife(it.int("n")) }
                match { cost ->
                    val atom = (cost as? AbilityCost.Atom)?.atom as? CostAtom.PayLife ?: return@match null
                    if (cost != SdkCosts.PayLife(atom.amount)) return@match null
                    bind("n" to atom.amount)
                }
            }
        }

    /** One cost atom. */
    private val atom: Phrase<AbilityCost> = oneOf(
        "a cost",
        tap,
        mana,
        sacrificeSelf,
        exileSelf,
        sacrificeFiltered,
        sacrificeSeveral,
        tapPermanents,
        payLife,
    )

    /** "{2}{B}, {T}, Sacrifice a Goblin" — two or more atoms, in the order the card prints them. */
    private val composite: Phrase<AbilityCost> = phrase("{atoms}", name = "several costs") {
        slot("atoms", separated("costs", atom, ", ", min = 2))
        build { SdkCosts.Composite(it.value<List<AbilityCost>>("atoms")) }
        match { cost ->
            val parts = (cost as? AbilityCost.Composite)?.costs ?: return@match null
            if (parts.size < 2 || cost != SdkCosts.Composite(parts)) return@match null
            bind("atoms" to parts)
        }
    }

    val cost: Phrase<AbilityCost> = oneOf("an activation cost", atom, composite)

    private fun manaCostOf(cost: AbilityCost): ManaCost? =
        ((cost as? AbilityCost.Atom)?.atom as? CostAtom.Mana)?.cost

    private fun sacrificeAtom(cost: AbilityCost): CostAtom.Sacrifice? =
        (cost as? AbilityCost.Atom)?.atom as? CostAtom.Sacrifice
}
