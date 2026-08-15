package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.Phrase
import com.wingedsheep.assay.syntax.bind
import com.wingedsheep.assay.syntax.constant
import com.wingedsheep.assay.syntax.oneOf
import com.wingedsheep.assay.syntax.phrase
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.costs.CostAtom
import com.wingedsheep.sdk.dsl.Costs as SdkCosts

/**
 * What an activated ability costs — the clause before the colon.
 *
 * A layered family in [Filters]' sense rather than a combinator: each rule spells one whole cost
 * shape and hands back the `AbilityCost` a card would carry, so printing is determined by the model
 * and there is nothing for an alternation to choose. The three shapes here are the ones the corpus
 * puts in front of a mana ability — `{T}` (653 declined lines over cards that already have a
 * golden), `{1}, {T}` (33) and a bare mana cost (12).
 *
 * ### The ordering inside a composite is the printed one
 *
 * `{2}, {T}` is `Composite([Mana, Tap])` and not `Composite([Tap, Mana])`, because that is what
 * Cabal Coffers and every other hand-written card carries, and a `Composite` is a list rather than
 * a set. Reading it the other way round would round-trip and disagree with every card — the
 * reversible-but-wrong class, caught here by writing the rule from the goldens rather than from the
 * sentence.
 *
 * ### `{T}` is not a mana cost, and the SDK is what says so
 *
 * The tap rule and the mana rule can both be offered at the same offset without ambiguity because
 * `ManaCost.parse("{T}")` throws — a symbol the SDK's mana vocabulary has no place for makes
 * [Primitives.manaCost] decline rather than invent a reading. The two rules are therefore disjoint
 * by the SDK's own type rather than by an ordering in the alternation.
 */
object Costs {

    private val tap: Phrase<AbilityCost> = constant("{T}", AbilityCost.Tap)

    private val mana: Phrase<AbilityCost> = phrase("{cost}", name = "a mana cost") {
        slot("cost", Primitives.manaCost)
        build { SdkCosts.Mana(it.value<ManaCost>("cost")) }
        match { cost -> manaCostOf(cost)?.let { bind("cost" to it) } }
    }

    private val manaThenTap: Phrase<AbilityCost> = phrase("{cost}, {T}", name = "a mana cost and a tap") {
        slot("cost", Primitives.manaCost)
        build { SdkCosts.Composite(SdkCosts.Mana(it.value<ManaCost>("cost")), AbilityCost.Tap) }
        match { cost ->
            val parts = (cost as? AbilityCost.Composite)?.costs ?: return@match null
            if (parts.size != 2 || parts[1] != AbilityCost.Tap) return@match null
            manaCostOf(parts[0])?.let { bind("cost" to it) }
        }
    }

    val cost: Phrase<AbilityCost> = oneOf("an activation cost", tap, mana, manaThenTap)

    private fun manaCostOf(cost: AbilityCost): ManaCost? =
        ((cost as? AbilityCost.Atom)?.atom as? CostAtom.Mana)?.cost
}
