package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.Bindings
import com.wingedsheep.assay.syntax.Phrase
import com.wingedsheep.assay.syntax.alternate
import com.wingedsheep.assay.syntax.bind
import com.wingedsheep.assay.syntax.oneOf
import com.wingedsheep.assay.syntax.phrase
import com.wingedsheep.assay.syntax.separated
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * The entry point: one normalized ability line ⇄ the [CardFragment] it denotes.
 *
 * The unit is the **line**, not the card. Line grouping is a property of the printed text, owned by
 * [com.wingedsheep.assay.normalize.Normalizer]; the card-level model is the fold of its lines'
 * fragments. Keeping the grouping out of the model is what lets "Flying, vigilance" and
 * "Flying\nVigilance" produce the same card while each still round-trips to its own printed shape.
 *
 * A fragment rather than a bare `List<KeywordAbility>` because a line can now fill either of the two
 * behavioural slots a card has — its keywords or its script — and the grammar has started reaching
 * the second one.
 */
object Grammar {

    /** One keyword ability: everything in [Keywords.all], tried in parallel. */
    val keywordAbility: Phrase<KeywordAbility> = oneOf("a keyword ability", Keywords.all)

    /** "Flying, first strike, protection from black" — the comma-joined keyword line. */
    private val keywordList: Phrase<List<KeywordAbility>> =
        separated("keyword abilities", keywordAbility, ", ")

    /**
     * "Flying; banding" — the semicolon-joined form, which ~31 mostly-older cards still print.
     *
     * It is an [alternate]: the separator is a property of the printed line that the model does not
     * carry, since a flat `List<KeywordAbility>` has no room for it. Something has to be picked, so
     * the comma is picked, and those cards report as [com.wingedsheep.assay.gate.LineVerdict.VARIANT]
     * — parsed correctly, printed canonically. That is the honest verdict and it is worth more than
     * the decline it replaces: it says the reading is right and only the spelling was normalized.
     */
    private val semicolonKeywordList: Phrase<List<KeywordAbility>> =
        alternate(separated("keyword abilities", keywordAbility, "; ", min = 2))

    private val keywordLine: Phrase<CardFragment> = phrase("{keywords}", name = "a keyword line") {
        slot("keywords", keywordList)
        build { CardFragment.of(it.value<List<KeywordAbility>>("keywords")) }
        match { fragment ->
            if (fragment.keywordAbilities.isNotEmpty() && fragment.script == CardScript.EMPTY) {
                bind("keywords" to fragment.keywordAbilities)
            } else {
                null
            }
        }
    }

    private val semicolonKeywordLine: Phrase<CardFragment> =
        alternate(
            phrase("{keywords}", name = "a keyword line") {
                slot("keywords", semicolonKeywordList)
                build { CardFragment.of(it.value<List<KeywordAbility>>("keywords")) }
                canonical = false
            }
        )

    /**
     * A line that is one spell effect — "Draw two cards.", "Target player draws a card."
     *
     * Wrapped into a fragment here rather than in [Steps] so the step rules stay about effects and
     * know nothing about where in a card they land.
     */
    private val spellLine: Phrase<CardFragment> = phrase("{step}", name = "a spell effect line") {
        slot("step", Steps.step)
        build { CardFragment.of(it.value<CardScript>("step")) }
        match { fragment ->
            if (fragment.keywordAbilities.isEmpty() && fragment.script != CardScript.EMPTY) {
                bind("step" to fragment.script)
            } else {
                null
            }
        }
    }

    /**
     * The empty line. It exists as a rule rather than as a special case in the gate because a
     * reminder-only line normalizes to "" and must still print back to "" — and because a vanilla
     * card, the easy quarter of the corpus, is exactly a face with no lines at all.
     */
    private val emptyLine: Phrase<CardFragment> = phrase("", name = "an empty line") {
        build { CardFragment.EMPTY }
        match { if (it.isEmpty) Bindings.EMPTY else null }
    }

    val abilityLine: Phrase<CardFragment> =
        oneOf("an ability line", emptyLine, keywordLine, semicolonKeywordLine, spellLine)
}
