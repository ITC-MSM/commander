package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Calciderm
 * {2}{W}{W}
 * Creature — Beast
 * 5/5
 * Shroud
 * Vanishing 4
 *
 * Vanishing is declared, not spelled out: the engine supplies all three of CR 702.62's abilities
 * from the keyword.
 */
val Calciderm = card("Calciderm") {
    manaCost = "{2}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Beast"
    power = 5
    toughness = 5
    oracleText = "Shroud (This creature can't be the target of spells or abilities.)\n" +
        "Vanishing 4 (This creature enters with four time counters on it. At the beginning of your upkeep, remove a time counter from it. When the last is removed, sacrifice it.)"

    keywords(Keyword.SHROUD)
    keywordAbility(KeywordAbility.vanishing(4))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "23"
        artist = "Dave Kendall"
        imageUri = "https://cards.scryfall.io/normal/front/f/0/f0159719-ccf4-4798-a394-b01f5e422a27.jpg"
    }
}
