package com.wingedsheep.engine.view

import com.wingedsheep.engine.core.AlternativeCostType
import com.wingedsheep.sdk.core.Zone

/**
 * How a spell got onto the stack — which alternative cost paid for it, and which zone it came from.
 *
 * Nothing in the client view used to carry either fact, so every cast rendered identically to a
 * plain cast out of hand. That is worst for the graveyard casts that also change the card's face:
 * a disturb spell (CR 702.146a) arrives under a name the opponent has never seen, with no printed
 * mana cost of its own, and its graveyard card silently disappears — so "Opponent cast Ghostly
 * Castigator" reads as though the card materialised in their hand. Flashback, harmonize, mayhem and
 * commander casts have the same blind spot in milder form.
 *
 * Both renderings live here so the naming table exists once: [logPhrase] for the game log line and
 * [badgeLabel] for the badge on the stack card. A plain cast from hand has no provenance worth
 * showing and every entry point returns null for it.
 */
object CastProvenance {

    /**
     * The game log's phrase, e.g. `"disturb, from graveyard"`, or null when the cast was an
     * ordinary one from hand. The caller parenthesises it, so the cast line still reads normally
     * on its own.
     */
    fun logPhrase(alternativeCost: AlternativeCostType?, castFromZone: Zone?): String? {
        val method = alternativeCost?.let(::methodName)
        val origin = castFromZone?.let(::originName)?.let { "from $it" }
        val parts = listOfNotNull(method, origin)
        return parts.ifEmpty { null }?.joinToString(", ")
    }

    /**
     * The stack card's badge, e.g. `"Disturb · Graveyard"`, or null for an ordinary cast from hand.
     * Rendered verbatim by the client — like `ClientCard.optionalCostLabel`, the wording stays
     * server-side so the client never maps enum names to words itself.
     */
    fun badgeLabel(alternativeCost: AlternativeCostType?, castFromZone: Zone?): String? {
        val method = alternativeCost?.let(::methodName)
        val origin = castFromZone?.let(::originName)
        val parts = listOfNotNull(method, origin).map { it.replaceFirstChar(Char::uppercase) }
        return parts.ifEmpty { null }?.joinToString(" · ")
    }

    /**
     * The zone worth naming in a cast description, or null when it isn't. Hand is the assumed
     * default and would only add noise; the battlefield, stack and sideboard are never a cast's
     * origin zone (a wish moves the card to hand first), so they fall through to null too.
     */
    private fun originName(zone: Zone): String? = when (zone) {
        Zone.GRAVEYARD -> "graveyard"
        Zone.EXILE -> "exile"
        Zone.COMMAND -> "command zone"
        Zone.LIBRARY -> "library"
        Zone.HAND, Zone.BATTLEFIELD, Zone.STACK, Zone.SIDEBOARD -> null
    }

    /**
     * The player-facing name of an alternative casting cost. Exhaustive on purpose: a new
     * [AlternativeCostType] must decide how it reads to the opponent rather than silently
     * inheriting a generic label.
     */
    private fun methodName(type: AlternativeCostType): String = when (type) {
        AlternativeCostType.FLASHBACK -> "flashback"
        AlternativeCostType.HARMONIZE -> "harmonize"
        AlternativeCostType.MAYHEM -> "mayhem"
        AlternativeCostType.DISTURB -> "disturb"
        AlternativeCostType.WARP -> "warp"
        AlternativeCostType.DASH -> "dash"
        AlternativeCostType.EVOKE -> "evoke"
        AlternativeCostType.EMERGE -> "emerge"
        AlternativeCostType.SNEAK -> "sneak"
        AlternativeCostType.WEB_SLINGING -> "web-slinging"
        AlternativeCostType.IMPENDING -> "impending"
        AlternativeCostType.CLEAVE -> "cleave"
        AlternativeCostType.MIRACLE -> "miracle"
        // Neither of these names a printed keyword the opponent could look up, so they read as the
        // generic fact: this spell was not paid for with its mana cost.
        AlternativeCostType.SELF_ALTERNATIVE, AlternativeCostType.GRANTED -> "alternative cost"
    }
}
