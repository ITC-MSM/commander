package com.wingedsheep.mtg.sets.definitions.dka.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Nephalia Seakite
 * {3}{U}
 * Creature — Bird
 * 2 / 3
 * Flash (You may cast this spell any time you could cast an instant.)
 * Flying
 *
 * Canonical printing: Dark Ascension, the card's earliest real printing. Reprinted in Magic 2014.
 */
val NephaliaSeakite = card("Nephalia Seakite") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Bird"
    power = 2
    toughness = 3
    oracleText = "Flash (You may cast this spell any time you could cast an instant.)\n" +
            "Flying"
    keywords(Keyword.FLASH, Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "43"
        artist = "Wayne England"
        flavorText = "\"Keep one eye on the cliff road or you may fall to your death. Keep one eye on the sky or your death may fall on you.\"\n" +
            "—Manfried Ulmach, Elgaud Master-at-Arms"
        imageUri = "https://cards.scryfall.io/normal/front/1/7/174a1d08-cd79-43d6-897f-3ee9a682d15e.jpg"
    }
}
