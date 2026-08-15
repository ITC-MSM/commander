package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Molimo, Maro-Sorcerer
 * {4}{G}{G}{G}
 * Legendary Creature — Elemental Sorcerer
 * Power/toughness: star/star
 * Trample
 * Molimo's power and toughness are each equal to the number of lands you control.
 *
 * Characteristic-defining ability (cf. [com.wingedsheep.mtg.sets.definitions.ons.cards.HeedlessOne]):
 * `dynamicStats` sets base power and toughness to the same dynamic value (lands you control), so
 * the printed star/star values are replaced in Layer 7b.
 */
val MolimoMaroSorcerer = card("Molimo, Maro-Sorcerer") {
    manaCost = "{4}{G}{G}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Creature — Elemental Sorcerer"
    oracleText = "Trample\nMolimo's power and toughness are each equal to the number of lands you control."

    dynamicStats(DynamicAmounts.landsYouControl())

    keywords(Keyword.TRAMPLE)

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "199"
        artist = "Mark Zug"
        imageUri = "https://cards.scryfall.io/normal/front/7/5/750d3475-ae72-42c1-ae4d-638f8e7c6d1a.jpg?1562918463"
    }
}
