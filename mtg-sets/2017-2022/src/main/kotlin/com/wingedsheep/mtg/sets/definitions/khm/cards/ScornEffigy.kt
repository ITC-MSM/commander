package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Scorn Effigy
 * {3}
 * Artifact Creature — Scarecrow
 * 2/3
 * Foretell {0} (During your turn, you may pay {2} and exile this card from your hand face down. Cast it on a later turn for its foretell cost.)
 *
 * Foretell is the card's only structural part — the printed `Keyword.FORETELL` is display-only,
 * so it is lowered into [KeywordAbility.foretell], which the engine's ForetellEnumerator reads to
 * offer the pay-{2}-and-exile special action and the later cast from exile for the foretell cost.
 * The card builder derives the printed keyword back out of that ability.
 */
val ScornEffigy = card("Scorn Effigy") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Scarecrow"
    oracleText = "Foretell {0} (During your turn, you may pay {2} and exile this card from your hand face down. Cast it on a later turn for its foretell cost.)"
    power = 2
    toughness = 3

    keywordAbility(KeywordAbility.foretell("{0}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "246"
        artist = "Wayne Reynolds"
        flavorText = "It remembers every wound it has seen or suffered—and nothing else."
        imageUri = "https://cards.scryfall.io/normal/front/4/f/4fa13084-3e68-49f4-8cc9-6d02286fd150.jpg"
    }
}
