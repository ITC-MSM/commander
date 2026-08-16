package com.wingedsheep.assay.grammar

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.ManaCost
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
    /**
     * `CardDefinition.flags` — the third behavioural slot, and the only one that is not a keyword or
     * part of the script.
     *
     * It exists because "This creature can't be blocked." is `AbilityFlag.CANT_BE_BLOCKED` rather
     * than a `StaticAbility`, so a line can reach a field neither of the other two slots covers.
     * That the SDK has two places to say a combat restriction — a flag for the unconditional form
     * and a static for every filtered one — is a finding rather than something this type should
     * paper over, and holding both is what lets the differential see it.
     */
    val flags: Set<AbilityFlag> = emptySet(),
    /**
     * `CardDefinition.equipCost` — the fourth behavioural slot, and the second one outside the
     * script.
     *
     * "Equip {1}" is a keyword ability the SDK **lowers** at authoring time rather than storing: the
     * card gets this field *and* the activated ability `ActivatedAbility.equip` builds, so one
     * printed line fills two slots in two different objects. That is the same shape
     * [Grammar.amplifyLine] has — a keyword plus a replacement effect — and the same reason this type
     * grew a field for it: the fragment is the only place a line's two contributions can meet.
     *
     * The field is not redundant with the ability beside it. `CardValidator` requires an Equipment
     * type line wherever it is set and `CardLinter` reads it to decide whether a permanent can ever
     * attach, so a card carrying the ability without the cost is a different — and worse — card than
     * one carrying both. Holding it here is what lets the differential see that.
     */
    val equipCost: ManaCost? = null,
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
        // An Aura declares one attachment restriction, so two lines both spelling "Enchant …" is the
        // same collision as two spell effects: a card the grammar has misread, or a card shape it
        // has no model for. Neither line may be dropped silently.
        if (script.auraTarget != null && other.script.auraTarget != null) return null
        // …and a card declares one equip cost, for the same reason. Two "Equip" lines on one card is
        // a shape the SDK cannot hold — `CardDefinition.equipCost` is one field — so the fold
        // declines and the card is counted rather than losing the second one silently.
        if (equipCost != null && other.equipCost != null) return null
        return CardFragment(
            keywordAbilities = keywordAbilities + other.keywordAbilities,
            flags = flags + other.flags,
            equipCost = equipCost ?: other.equipCost,
            script = CardScript(
                spellEffect = script.spellEffect ?: other.script.spellEffect,
                targetRequirements = script.targetRequirements + other.script.targetRequirements,
                // A spell states its casting restrictions and its additional costs on lines of their
                // own, so both accumulate across lines exactly as the ability lists do.
                castRestrictions = script.castRestrictions + other.script.castRestrictions,
                additionalCosts = script.additionalCosts + other.script.additionalCosts,
                // Triggered abilities are a list on purpose: one card, several trigger lines, in
                // printed order. Unlike the spell effect there is nothing to collide over. The same
                // holds for activated abilities — and a *single* line can contribute several of
                // them, since "{T}: Add {B} or {G}." is two — for static abilities, which is how an
                // aura's two payoff lines fold, and for replacement effects.
                triggeredAbilities = script.triggeredAbilities + other.script.triggeredAbilities,
                activatedAbilities = script.activatedAbilities + other.script.activatedAbilities,
                staticAbilities = script.staticAbilities + other.script.staticAbilities,
                replacementEffects = script.replacementEffects + other.script.replacementEffects,
                auraTarget = script.auraTarget ?: other.script.auraTarget,
                // "This spell can't be countered." is a line of its own on Root Sliver and Vexing
                // Beetle, so it accumulates like the ability lists rather than colliding.
                cantBeCountered = script.cantBeCountered || other.script.cantBeCountered,
            ),
        )
    }

    val isEmpty: Boolean
        get() = keywordAbilities.isEmpty() && flags.isEmpty() && equipCost == null && script == CardScript.EMPTY

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
            "spellEffect, targetRequirements, triggeredAbilities, activatedAbilities, " +
                "staticAbilities, replacementEffects, auraTarget, castRestrictions, additionalCosts, " +
                "cantBeCountered"
    }
}
