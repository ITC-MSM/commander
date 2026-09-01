package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Eerie Procession
 * {2}{U}
 * Sorcery — Arcane
 *
 * Search your library for an Arcane card, reveal that card, put it into your hand, then shuffle.
 *
 * `assay compile` declines this line — its grammar has no rule for the noun phrase "an Arcane
 * card" — so it is authored straight from the printed text. The tutor is the stock
 * [Patterns.Library.searchLibrary] facade with `destination = HAND` and `reveal = true`
 * (`shuffleAfter` already defaults to true, which is the printed "then shuffle").
 *
 * The filter is [Subtype.ARCANE] alone, with no card-type predicate beside it. Every Arcane card
 * ever printed happens to be an instant or a sorcery, so narrowing to `InstantOrSorcery` would
 * select the same cards today — but the printed text restricts by subtype and nothing else, and
 * the extra `Or(IsInstant, IsSorcery)` is a divergence the Assay differential flags against the
 * model it builds from that same text. Say what the card says.
 */
val EerieProcession = card("Eerie Procession") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery — Arcane"
    oracleText = "Search your library for an Arcane card, reveal that card, put it into your hand, then shuffle."

    spell {
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Any.withSubtype(Subtype.ARCANE),
            destination = SearchDestination.HAND,
            reveal = true
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "58"
        artist = "Jim Murray"
        flavorText = "\"Though in years past speculation was not encouraged about the strange ways " +
            "of kami, now we must understand their motivations, if such is even possible to the " +
            "mortal mind.\"\n—Lady Azami"
        imageUri = "https://cards.scryfall.io/normal/front/3/a/3af326c1-fcc8-45c3-b75e-ae4dbbd59ced.jpg?1783944329"
    }
}
