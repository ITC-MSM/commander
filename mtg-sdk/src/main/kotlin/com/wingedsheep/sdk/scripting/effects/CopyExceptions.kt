package com.wingedsheep.sdk.scripting.effects

import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Supertype
import kotlinx.serialization.Serializable

/**
 * The "**except** …" half of a copy effect (CR 707.9) — the modifications a copy effect may specify
 * on top of the copiable values it copies.
 *
 * One vocabulary shared across the copy paths that can carry characteristic exceptions, rather than
 * a different ad-hoc set of riders per effect. The engine applies it in exactly one place
 * (`rules-engine/.../handlers/effects/copy/CopyExceptionApplier.kt`), so "except it's a legendary
 * Human Villain creature in addition to its other types" means the same thing whether the copy
 * lands on an existing permanent ([EachPermanentBecomesCopyOfTargetEffect]), mints a token from a
 * target or from the ability's own source ([CreateTokenCopyOfTargetEffect] /
 * [CreateTokenCopyOfSourceEffect], which map their historical flat fields onto this type through
 * their `copyExceptions` views), or enters as a copy (`EntersAsCopy`, Clone/Sakashima).
 *
 * Everything here is a *copiable* value (CR 707.2): anything that later copies the copy sees these
 * modifications too. Riders that are **not** characteristics — "and this ability", "it enters
 * tapped", "it enters with a +1/+1 counter" — deliberately stay on the individual effects.
 *
 * Add/override pairs follow Magic's own templating, which the rules make load-bearing: a stated
 * card type or subtype *replaces* by default (CR 205.1a) — [overrideCardTypes] /
 * [overrideSubtypes] / [overrideColors] — unless the clause says "**in addition to** its other
 * types" or "still a [type]", which retains the prior types (CR 205.1b) — [addedCardTypes] /
 * [addedSubtypes] / [addedColors]. A real card only prints one of the two on a given
 * characteristic; when both are set anyway the override wins and the addition is ignored, on every
 * axis alike.
 *
 * @property nameOverride "except its name is …" — the copy keeps this name instead of the copied
 *   object's (Absorbing Man, Taskmaster, Sakashima). The name is a copiable value, so the legend
 *   rule and every name-matching effect see the override.
 * @property addedKeywords Keywords the copy has *in addition* to the ones it copied — "except it
 *   has flying" (Likeness Looter), "and he has vigilance" (Absorbing Man).
 * @property addedSupertypes Supertypes unioned onto the copied type line — "except it's legendary"
 *   (Adagia, Windswept Bastion).
 * @property removedSupertypes Supertypes stripped from the copied type line — "except it isn't
 *   legendary" (Shuri, Wakandan Inventor; Impostor Syndrome). Without this direction the copy of a
 *   legendary permanent is binned by the legend rule (CR 704.5j) the moment it resolves. Applied
 *   after [addedSupertypes], so a supertype named in both is removed.
 * @property addedCardTypes Card types unioned onto the copied type line — "except he's a … creature
 *   **in addition to** his other types" (Absorbing Man copying a land stays a land and becomes a
 *   creature as well).
 * @property overrideCardTypes Card types that *replace* the copied ones — "it's a Food artifact …
 *   and it loses all other card types" (Shelob, Child of Ungoliant). Note that dropping CREATURE
 *   this way means the copy is no longer a creature and copies no P/T meaning.
 * @property addedSubtypes Subtypes unioned onto the copied type line — "a 3/3 Golem artifact
 *   creature in addition to its other types" (Nexus of Becoming).
 * @property overrideSubtypes Subtypes that *replace* the copied ones — "a 5/5 black Demon"
 *   (Ardyn, the Usurper).
 * @property addedColors Colors unioned onto the copied colors — "a 1/1 red Balloon creature in
 *   addition to its other colors and types" (The Jolly Balloon Man).
 * @property overrideColors Colors that *replace* the copied ones (Ardyn, the Usurper's black).
 * @property powerOverride Replaces the copied base power — the "he's a 4/4" half of a copy
 *   exception. Applied even when the copied object had no P/T at all (Absorbing Man copying a
 *   land), in which case base P/T are created from scratch.
 * @property toughnessOverride Replaces the copied base toughness.
 * @property noManaCost "except it … has no mana cost" — the copy has no mana cost and so mana
 *   value 0 (Embalm / Eternalize, CR 702.128a).
 */
@Serializable
data class CopyExceptions(
    val nameOverride: String? = null,
    val addedKeywords: Set<Keyword> = emptySet(),
    val addedSupertypes: Set<Supertype> = emptySet(),
    val removedSupertypes: Set<Supertype> = emptySet(),
    val addedCardTypes: Set<CardType> = emptySet(),
    val overrideCardTypes: Set<CardType>? = null,
    val addedSubtypes: Set<Subtype> = emptySet(),
    val overrideSubtypes: Set<Subtype>? = null,
    val addedColors: Set<Color> = emptySet(),
    val overrideColors: Set<Color>? = null,
    val powerOverride: Int? = null,
    val toughnessOverride: Int? = null,
    val noManaCost: Boolean = false,
) {
    /** True when nothing is modified — a plain copy with no "except" clause. */
    val isEmpty: Boolean get() = this == None

    /**
     * The "except …" clause fragments, in printed order, for rendering an effect's description.
     * Joined by the caller (normally with " and "), so an effect can splice them into its own
     * sentence. Empty when [isEmpty].
     */
    fun clauses(): List<String> = buildList {
        if (nameOverride != null) add("its name is $nameOverride")
        if (powerOverride != null || toughnessOverride != null) {
            add("it's ${powerOverride ?: "*"}/${toughnessOverride ?: "*"}")
        }
        overrideColors?.let { add("it's ${it.joinToString(" ") { c -> c.displayName.lowercase() }}") }
        if (addedColors.isNotEmpty()) {
            add(
                "it's ${addedColors.joinToString(" ") { c -> c.displayName.lowercase() }} " +
                    "in addition to its other colors"
            )
        }
        if (overrideCardTypes != null || overrideSubtypes != null) {
            // A clause that states a whole type line replaces: "it's a 5/5 black Demon". An axis
            // with no override of its own still renders its addition, which is what resolves.
            typeWords(
                addedSupertypes,
                overrideCardTypes ?: addedCardTypes,
                overrideSubtypes ?: addedSubtypes,
            )?.let { add("it's $it") }
        } else {
            typeWords(addedSupertypes, addedCardTypes, addedSubtypes)?.let { words ->
                // Supertypes alone read as a bare statement ("except it's legendary"); anything
                // that touches card types or subtypes carries the "in addition" tail.
                if (addedCardTypes.isEmpty() && addedSubtypes.isEmpty()) add("it's $words")
                else add("it's $words in addition to its other types")
            }
        }
        if (removedSupertypes.isNotEmpty()) {
            add("it isn't ${removedSupertypes.joinToString(" or ") { it.displayName.lowercase() }}")
        }
        if (addedKeywords.isNotEmpty()) {
            add("it has ${addedKeywords.joinToString(", ") { it.name.lowercase().replace('_', ' ') }}")
        }
        if (noManaCost) add("it has no mana cost")
    }

    private fun typeWords(
        supertypes: Set<Supertype>,
        cardTypes: Set<CardType>?,
        subtypes: Set<Subtype>?,
    ): String? {
        val words = supertypes.map { it.displayName.lowercase() } +
            (subtypes ?: emptySet()).map { it.value } +
            (cardTypes ?: emptySet()).map { it.displayName.lowercase() }
        return if (words.isEmpty()) null else words.joinToString(" ")
    }

    companion object {
        /** A plain copy — no "except" clause at all. */
        val None = CopyExceptions()
    }
}
