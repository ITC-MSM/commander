package com.wingedsheep.assay.grammar

import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * What one ability line contributes to a card.
 *
 * A card is `CardDefinition`, whose two behavioural slots are `keywordAbilities` and `script`. A
 * *line* fills part of one of them, so this type is those two slots and nothing else — the whole
 * card is the [merge] of its lines' fragments.
 *
 * **This is not an Assay IR.** The design's rule is that the grammar parses straight into `mtg-sdk`
 * types, and it does: every value inside a fragment is an SDK `KeywordAbility` or an SDK
 * `CardScript`. What this class adds is not a representation of meaning but a statement of *where in
 * the card* a line's meaning goes — the same information `CardDefinition` already carries, narrowed
 * to the two fields a line can reach. Nothing here is ever translated; it is destructured.
 *
 * The unit stays the **line** rather than the card for the reason [Grammar] gives: line grouping is
 * a property of the printed text, which normalization owns, so the model must not encode it.
 */
data class CardFragment(
    val keywordAbilities: List<KeywordAbility> = emptyList(),
    val script: CardScript = CardScript.EMPTY,
) {

    /**
     * Fold two lines' contributions together.
     *
     * Only the slots the grammar can currently produce are combined, and a collision throws rather
     * than picking a winner: two lines that both claim to be *the* spell effect is a grammar bug —
     * a card has one — and silently keeping the first would hide it behind a plausible model.
     * Widen this as the grammar reaches new slots; the compiler will not remind you, but
     * [Companion.MODELLED_SLOTS_NOTE] says where to look.
     */
    fun merge(other: CardFragment): CardFragment {
        require(script.spellEffect == null || other.script.spellEffect == null) {
            "two lines both parsed as the spell effect; a card has one"
        }
        return CardFragment(
            keywordAbilities = keywordAbilities + other.keywordAbilities,
            script = CardScript(
                spellEffect = script.spellEffect ?: other.script.spellEffect,
                targetRequirements = script.targetRequirements + other.script.targetRequirements,
            ),
        )
    }

    val isEmpty: Boolean get() = keywordAbilities.isEmpty() && script == CardScript.EMPTY

    companion object {
        val EMPTY = CardFragment()

        fun of(keywords: List<KeywordAbility>) = CardFragment(keywordAbilities = keywords)

        fun of(script: CardScript) = CardFragment(script = script)

        /**
         * The `CardScript` slots the grammar can currently produce, and therefore the only ones the
         * differential is entitled to compare. Kept as one list so [merge] and
         * `Differential.compare`'s completeness check cannot drift apart — adding a slot to the
         * grammar means adding it in both places, and this note is the pointer between them.
         */
        const val MODELLED_SLOTS_NOTE = "spellEffect, targetRequirements"
    }
}
