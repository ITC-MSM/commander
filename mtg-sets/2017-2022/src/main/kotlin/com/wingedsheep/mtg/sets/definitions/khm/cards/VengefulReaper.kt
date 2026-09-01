package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Vengeful Reaper
 * {3}{B}
 * Creature — Angel Cleric
 * 2/3
 * Flying, deathtouch, haste
 * Foretell {1}{B} (During your turn, you may pay {2} and exile this card from your hand face down. Cast it on a later turn for its foretell cost.)
 *
 * Foretell is the card's only structural part — the printed `Keyword.FORETELL` is display-only,
 * so it is lowered into [KeywordAbility.foretell], which the engine's ForetellEnumerator reads to
 * offer the pay-{2}-and-exile special action and the later cast from exile for the foretell cost.
 * The card builder derives the printed keyword back out of that ability.
 */
val VengefulReaper = card("Vengeful Reaper") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Angel Cleric"
    oracleText = "Flying, deathtouch, haste\n" +
        "Foretell {1}{B} (During your turn, you may pay {2} and exile this card from your hand face down. Cast it on a later turn for its foretell cost.)"
    power = 2
    toughness = 3

    keywords(Keyword.FLYING, Keyword.DEATHTOUCH, Keyword.HASTE)
    keywordAbility(KeywordAbility.foretell("{1}{B}"))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "116"
        artist = "Billy Christian"
        flavorText = "\"Starnheim is closed to you, coward.\""
        imageUri = "https://cards.scryfall.io/normal/front/8/5/854c99fd-71ba-40b7-98cf-b783f01a77b4.jpg"
    }
}
