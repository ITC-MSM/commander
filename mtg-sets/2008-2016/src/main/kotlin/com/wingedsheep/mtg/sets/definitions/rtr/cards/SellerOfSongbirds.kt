package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Seller of Songbirds
 * {2}{W}
 * Creature — Human
 * 1/2
 * When this creature enters, create a 1/1 white Bird creature token with flying.
 */
val SellerOfSongbirds = card("Seller of Songbirds") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human"
    power = 1
    toughness = 2
    oracleText = "When this creature enters, create a 1/1 white Bird creature token with flying."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Bird"),
            keywords = setOf(Keyword.FLYING),
            imageUri = "https://cards.scryfall.io/normal/front/4/b/4b0c59f8-b407-47e7-8885-8c968f4ceecf.jpg?1783914993"
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "22"
        artist = "Christopher Moeller"
        flavorText = "\"Lady Wren is the one merchant in Keyhole Downs who isn't running a scam.\" —Mirela, Azorius hussar"
        imageUri = "https://cards.scryfall.io/normal/front/2/a/2a41edbe-4c5a-4535-a082-235dc3ffe60a.jpg?1783940373"
    }
}
