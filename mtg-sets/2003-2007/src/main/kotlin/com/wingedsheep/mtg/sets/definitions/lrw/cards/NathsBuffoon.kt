package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.ProtectionScope

/**
 * Nath's Buffoon
 * {1}{B}
 * Creature — Goblin Rogue
 * 1/1
 * Protection from Elves
 */
val NathsBuffoon = card("Nath's Buffoon") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Goblin Rogue"
    power = 1
    toughness = 1
    oracleText = "Protection from Elves"

    keywordAbility(KeywordAbility.Protection(ProtectionScope.Subtype("Elf")))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "129"
        artist = "Thomas Denmark"
        flavorText = "Smik learned the elvish dance quickly enough. The most difficult, yet most important step was to stay out of Nath's sight until called to perform."
        imageUri = "https://cards.scryfall.io/normal/front/6/6/66f077d2-34ab-45fa-84db-eb408c5a9996.jpg?1783942887"
    }
}
