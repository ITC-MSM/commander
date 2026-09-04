package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Stormrider Spirit (Shadows over Innistrad #91)
 * {4}{U}
 * Creature — Spirit
 * 3 / 3
 *
 * Flash
 * Flying
 */
val StormriderSpirit = card("Stormrider Spirit") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Spirit"
    power = 3
    toughness = 3
    oracleText = "Flash\n" +
        "Flying"

    keywords(Keyword.FLASH, Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "91"
        artist = "Lake Hurwitz"
        flavorText = "Thunder isn't all that follows lightning."
        imageUri = "https://cards.scryfall.io/normal/front/9/c/9c0fb4be-df73-43a9-bd48-fd47a4190fc1.jpg?1783937784"
    }
}
