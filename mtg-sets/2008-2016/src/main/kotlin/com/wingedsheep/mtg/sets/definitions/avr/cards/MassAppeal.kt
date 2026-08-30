package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Mass Appeal
 * {2}{U}
 * Sorcery
 *
 * Draw a card for each Human you control.
 *
 * "each Human" is a bare tribal noun, so it counts every *permanent* with the subtype — a Human
 * artifact or enchantment is included — hence [GameObjectFilter.Permanent] rather than
 * `Creature`. The "you control" half lives on the query's [Player.You], not in the filter.
 */
val MassAppeal = card("Mass Appeal") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Draw a card for each Human you control."

    spell {
        effect = Effects.DrawCards(
            DynamicAmounts.battlefield(
                Player.You,
                GameObjectFilter.Permanent.withSubtype("Human"),
            ).count()
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "66"
        artist = "Christopher Moeller"
        flavorText = "\"We have emerged triumphant from the darkness. Let our hard-won wisdom guide us to prosperity.\""
        imageUri = "https://cards.scryfall.io/normal/front/d/f/dfe9ae51-fd2b-45ca-a780-725f51f897b2.jpg?1783940716"
    }
}
