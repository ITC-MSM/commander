package com.wingedsheep.sdk.model

import com.wingedsheep.sdk.core.Color

/**
 * One token printing a set contributes, so a token minted by that set's cards shows *that set's*
 * art instead of the engine-wide generic fallback.
 *
 * A token is not a card: it has no [CardDefinition] and no [Printing] row, so the per-printing
 * art override that [Printing] gives a real card has nothing to hang on. Sets therefore declare
 * their token art here, on [MtgSet.tokenArt], and the engine resolves it at creation time from
 * the printing of the card doing the creating. That keeps art out of the card script — a card
 * that hardcodes `imageUri` on its `CreateToken` effect mints the same art from every printing,
 * which is wrong the moment the card is reprinted in a set with its own token.
 *
 * ## Matching
 * [name] must match; [power], [toughness] and [colors] are optional discriminators that only
 * participate when non-null. Leave them out unless the set prints two different tokens sharing a
 * name — e.g. a white 1/1 Cat and a green 2/2 Cat — in which case spell out enough to separate
 * them. See [matches].
 *
 * ## Image form
 * Use the Scryfall **`art_crop`** URL, not `normal`. The client renders a token as a generated
 * frame (name bar / art box / type bar) and drops this image into the art box, so a full-card
 * `normal` image arrives pre-framed and gets cropped to its middle band.
 *
 * @property name Token name as printed — the creature type for a vanilla token ("Cat"), or the
 *   full name for a named one ("Marit Lage", "Zombie Druid").
 * @property imageUri Scryfall `art_crop` URL for this set's printing of the token.
 * @property power Printed power, when needed to disambiguate. Null matches any.
 * @property toughness Printed toughness, when needed to disambiguate. Null matches any.
 * @property colors Printed colors, when needed to disambiguate. Null matches any; an empty set
 *   means *colorless* and only matches a colorless token.
 */
data class TokenPrinting(
    val name: String,
    val imageUri: String,
    val power: Int? = null,
    val toughness: Int? = null,
    val colors: Set<Color>? = null,
) {
    /**
     * Whether this printing describes the token being created. [name] is compared
     * case-insensitively; each of [power], [toughness] and [colors] is checked only when this
     * printing pins it, so a bare `TokenPrinting("Cat", art)` matches every Cat the set mints.
     */
    fun matches(
        name: String,
        power: Int? = null,
        toughness: Int? = null,
        colors: Set<Color> = emptySet(),
    ): Boolean =
        this.name.equals(name, ignoreCase = true) &&
            (this.power == null || this.power == power) &&
            (this.toughness == null || this.toughness == toughness) &&
            (this.colors == null || this.colors == colors)
}
