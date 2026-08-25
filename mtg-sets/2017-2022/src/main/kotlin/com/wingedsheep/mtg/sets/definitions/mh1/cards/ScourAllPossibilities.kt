package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Scour All Possibilities — Modern Horizons #67
 * {1}{U} · Sorcery
 *
 * Scry 2, then draw a card.
 * Flashback {4}{U}
 *
 * The "then" is ordering, not a gate: the scry fully resolves before the draw, which is the whole
 * point of the card — you get to decide what the drawn card will be. [Effects.Scry] is the compact
 * macro node the engine expands into the shared Gather → Select → Move pipeline at resolution, so
 * it stays one step inside [Effects.Composite].
 *
 * Flashback is declared as the parameterized [KeywordAbility.Flashback]; the display-only
 * `Keyword.FLASHBACK` is derived from it by `CardBuilder.build()`. The engine's graveyard-cast path
 * and the exile-on-resolution rider (CR 702.34a) both hang off that ability, so no extra wiring is
 * needed here.
 */
val ScourAllPossibilities = card("Scour All Possibilities") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Scry 2, then draw a card.\n" +
        "Flashback {4}{U} (You may cast this card from your graveyard for its flashback cost. Then exile it.)"

    keywordAbility(KeywordAbility.flashback("{4}{U}"))

    spell {
        effect = Effects.Composite(
            Effects.Scry(2),
            Effects.DrawCards(1)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "67"
        artist = "Mitchell Malloy"
        flavorText = "Searching the future for answers often leads to further questions."
        imageUri = "https://cards.scryfall.io/normal/front/7/c/7c6466cb-d1d0-4461-b48d-7497bdc9c474.jpg?1783933138"
    }
}
