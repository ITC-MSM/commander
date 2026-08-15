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

    /** One keyword ability, lifted into the one-element group most phrases denote. */
    private val singleKeyword: Phrase<List<KeywordAbility>> = phrase("{one}", name = "a keyword ability") {
        slot("one", keywordAbility)
        build { listOf(it.value<KeywordAbility>("one")) }
        match { it.singleOrNull()?.let { ability -> bind("one" to ability) } }
    }

    /**
     * What one comma-separated element of a keyword line denotes: usually one ability, and for the
     * multi-quality forms of protection and hexproof (CR 702.16g / 702.11f) several.
     *
     * Grouping is only a *printing* concern in one direction and a parsing convenience in the other,
     * but it has to exist in the grammar rather than in the gate, because the same flat model has to
     * come back as the joined text the card prints.
     */
    private val keywordGroup: Phrase<List<KeywordAbility>> =
        oneOf("a keyword ability", Keywords.runs + singleKeyword)

    /**
     * "Flying, first strike, protection from black and from red" — the comma-joined keyword line.
     *
     * The model is flat, so printing has to decide where the joins go. Consecutive abilities that a
     * run rule can express are joined maximally, which is what every printed card does; a card that
     * spells two protections as separate comma-separated abilities therefore prints back joined and
     * reports as a [com.wingedsheep.assay.gate.LineVerdict.VARIANT] — reparsed to the identical
     * model, only the spelling normalized. No card in the corpus is in that class today.
     */
    private val keywordList: Phrase<List<KeywordAbility>> = phrase("{groups}", name = "keyword abilities") {
        slot("groups", separated("keyword abilities", keywordGroup, ", "))
        build { it.value<List<List<KeywordAbility>>>("groups").flatten() }
        match { bind("groups" to groupForPrinting(it)) }
    }

    /**
     * Split a line's abilities into the groups it prints as: maximal runs a [Keywords.runs] rule can
     * express, everything else on its own.
     *
     * Maximal-and-greedy is a choice, and it is the one that matches printed text: cards print
     * "protection from black and from red", never the two spelled out separately. It is also the
     * only choice that is deterministic — anything subtler would need to know which grouping the
     * card *printed*, which is exactly the information the model does not carry.
     */
    private fun groupForPrinting(abilities: List<KeywordAbility>): List<List<KeywordAbility>> {
        val groups = mutableListOf<List<KeywordAbility>>()
        var index = 0
        while (index < abilities.size) {
            val run = Keywords.runs.firstNotNullOfOrNull { rule -> longestRun(rule, abilities, index) }
            if (run == null) {
                groups.add(listOf(abilities[index]))
                index++
            } else {
                groups.add(run)
                index += run.size
            }
        }
        return groups
    }

    /** The longest prefix from [index] that [rule] can print, or null if it cannot print any. */
    private fun longestRun(
        rule: Phrase<List<KeywordAbility>>,
        abilities: List<KeywordAbility>,
        index: Int,
    ): List<KeywordAbility>? =
        (abilities.size downTo index + 2)
            .asSequence()
            .map { end -> abilities.subList(index, end).toList() }
            .firstOrNull { rule.unparse(it) != null }

    /**
     * "Flying; banding" — the semicolon-joined form, which ~31 mostly-older cards still print.
     *
     * It is an [alternate]: the separator is a property of the printed line that the model does not
     * carry, since a flat `List<KeywordAbility>` has no room for it. Something has to be picked, so
     * the comma is picked, and those cards report as [com.wingedsheep.assay.gate.LineVerdict.VARIANT]
     * — parsed correctly, printed canonically. That is the honest verdict and it is worth more than
     * the decline it replaces: it says the reading is right and only the spelling was normalized.
     */
    private val semicolonKeywordList: Phrase<List<List<KeywordAbility>>> =
        alternate(separated("keyword abilities", keywordGroup, "; ", min = 2))

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
                build { CardFragment.of(it.value<List<List<KeywordAbility>>>("keywords").flatten()) }
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
     * A line that is one triggered ability — "When ~ enters, draw a card."
     *
     * Wrapped here rather than in [Triggers] for the same reason [spellLine] is: the rules stay
     * about abilities and know nothing about which of a card's slots they land in.
     */
    private val triggerLine: Phrase<CardFragment> = phrase("{trigger}", name = "a triggered ability line") {
        slot("trigger", Triggers.trigger)
        build { CardFragment.of(CardScript(triggeredAbilities = listOf(it.value("trigger")))) }
        match { fragment ->
            val ability = fragment.script.triggeredAbilities.singleOrNull() ?: return@match null
            if (fragment.keywordAbilities.isNotEmpty()) return@match null
            if (fragment.script != CardScript(triggeredAbilities = listOf(ability))) return@match null
            bind("trigger" to ability)
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
        oneOf("an ability line", emptyLine, keywordLine, semicolonKeywordLine, spellLine, triggerLine)
}
