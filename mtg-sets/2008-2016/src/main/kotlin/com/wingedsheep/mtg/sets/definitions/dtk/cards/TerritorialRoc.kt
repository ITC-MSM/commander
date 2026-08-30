package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Territorial Roc
 * {1}{W}
 * Creature — Bird
 * 1 / 3
 *
 * Flying
 *
 * Evasion only — one keyword, no script.
 */
val TerritorialRoc = card("Territorial Roc") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Bird"
    power = 1
    toughness = 3
    oracleText = "Flying"

    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "43"
        artist = "YW Tang"
        flavorText = "\"Such lesser creatures must be purged from the sky. What use do they have but to help channel the lightning of our mighty dragonlord?\"\n—Gvar Barzeel, Kolaghan warrior"
        imageUri = "https://cards.scryfall.io/normal/front/0/c/0c83aa5e-b607-4c4f-a5f6-db61c93a1152.jpg?1783938611"
    }
}
