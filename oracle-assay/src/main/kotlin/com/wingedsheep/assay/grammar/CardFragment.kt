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
     * Fold two lines' contributions together, or **null** when they cannot be one card.
     *
     * Only the slots the grammar can currently produce are combined. Two lines that both claim to be
     * *the* spell effect is the collision: a `CardScript` has one `spellEffect`, and a card printing
     * two effect paragraphs means a sequence the grammar has no rule for yet. Neither keeping the
     * first nor concatenating them is honest — the first drops meaning, the second invents an order
     * nothing checked — so the fold declines and the caller counts the card.
     *
     * It used to throw, on the reading that a collision could only be a grammar bug. It stopped
     * being one the moment [Steps] could read a second kind of sentence, and a gate that crashes on
     * a card it does not model is the one behaviour "declining is success" rules out.
     *
     * Widen this as the grammar reaches new slots; the compiler will not remind you, but
     * [Companion.MODELLED_SLOTS_NOTE] says where to look.
     */
    fun merge(other: CardFragment): CardFragment? {
        if (script.spellEffect != null && other.script.spellEffect != null) return null
        return CardFragment(
            keywordAbilities = keywordAbilities + other.keywordAbilities,
            script = CardScript(
                spellEffect = script.spellEffect ?: other.script.spellEffect,
                targetRequirements = script.targetRequirements + other.script.targetRequirements,
                // Triggered abilities are a list on purpose: one card, several trigger lines, in
                // printed order. Unlike the spell effect there is nothing to collide over. The same
                // holds for activated abilities — and a *single* line can contribute several of
                // them, since "{T}: Add {B} or {G}." is two — and for replacement effects.
                triggeredAbilities = script.triggeredAbilities + other.script.triggeredAbilities,
                activatedAbilities = script.activatedAbilities + other.script.activatedAbilities,
                replacementEffects = script.replacementEffects + other.script.replacementEffects,
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
        const val MODELLED_SLOTS_NOTE =
            "spellEffect, targetRequirements, triggeredAbilities, activatedAbilities, replacementEffects"
    }
}
