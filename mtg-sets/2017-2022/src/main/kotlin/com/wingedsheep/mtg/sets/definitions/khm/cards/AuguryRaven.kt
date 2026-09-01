package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Augury Raven
 * {3}{U}
 * Creature — Bird
 * 3/3
 * Flying
 * Foretell {1}{U} (During your turn, you may pay {2} and exile this card from your hand face down. Cast it on a later turn for its foretell cost.)
 *
 * Foretell is the card's only structural part — the printed `Keyword.FORETELL` is display-only,
 * so it is lowered into [KeywordAbility.foretell], which the engine's ForetellEnumerator reads to
 * offer the pay-{2}-and-exile special action and the later cast from exile for the foretell cost.
 * The card builder derives the printed keyword back out of that ability.
 */
val AuguryRaven = card("Augury Raven") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Bird"
    oracleText = "Flying\n" +
        "Foretell {1}{U} (During your turn, you may pay {2} and exile this card from your hand face down. Cast it on a later turn for its foretell cost.)"
    power = 3
    toughness = 3

    keywords(Keyword.FLYING)
    keywordAbility(KeywordAbility.foretell("{1}{U}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "44"
        artist = "Jesper Ejsing"
        flavorText = "Some ravens collect shiny baubles; others hoard omens and secrets."
        imageUri = "https://cards.scryfall.io/normal/front/a/9/a9947cfd-91d6-479e-a7f6-2a2050f020f3.jpg"
    }
}
