package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Thraben Valiant
 * {1}{W}
 * Creature — Human Soldier
 * 2 / 1
 *
 * Vigilance
 */
val ThrabenValiant = card("Thraben Valiant") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    power = 2
    toughness = 1
    oracleText = "Vigilance"

    keywords(Keyword.VIGILANCE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "39"
        artist = "Jason Chan"
        flavorText = "\"Once more into Devil's Breach, soldiers. I want another devil tail for my collection.\""
        imageUri = "https://cards.scryfall.io/normal/front/2/0/20558f69-9240-49b9-9695-caf75ee2db1b.jpg?1783940728"
    }
}
