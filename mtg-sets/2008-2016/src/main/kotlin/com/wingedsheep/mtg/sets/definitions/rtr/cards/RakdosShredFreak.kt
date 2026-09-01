package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Rakdos Shred-Freak
 * {B/R}{B/R}
 * Creature — Human Berserker
 * 2/1
 *
 * Haste
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * One evergreen keyword and nothing else. The hybrid mana cost is data on the card, not script.
 */
val RakdosShredFreak = card("Rakdos Shred-Freak") {
    manaCost = "{B/R}{B/R}"
    colorIdentity = "BR"
    typeLine = "Creature — Human Berserker"
    oracleText = "Haste"
    power = 2
    toughness = 1

    keywords(Keyword.HASTE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "221"
        artist = "Wayne Reynolds"
        flavorText = "\"If there were such a thing as a soul, I think it would be behind the gallbladder but above the kidneys.\""
        imageUri = "https://cards.scryfall.io/normal/front/0/6/06899549-5534-4d11-86c1-afd1796e18b1.jpg?1783940326"
    }
}
