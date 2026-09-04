package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Silent Observer (Shadows over Innistrad #86)
 * {3}{U}
 * Creature — Spirit
 * 1 / 5
 *
 * Flying
 */
val SilentObserver = card("Silent Observer") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Spirit"
    power = 1
    toughness = 5
    oracleText = "Flying"

    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "86"
        artist = "Lake Hurwitz"
        flavorText = "It's not just a feeling—you *are* being watched."
        imageUri = "https://cards.scryfall.io/normal/front/5/3/535e3d1b-b71b-406a-bec6-73b2cb45f6c8.jpg?1783937787"
    }
}
