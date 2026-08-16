package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Gallant Cavalry
 * {3}{W}
 * Creature — Human Knight
 * 2/2
 * Vigilance (Attacking doesn't cause this creature to tap.)
 * When this creature enters, create a 2/2 white Knight creature token with vigilance.
 */
val GallantCavalry = card("Gallant Cavalry") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Knight"
    power = 2
    toughness = 2
    oracleText = "Vigilance (Attacking doesn't cause this creature to tap.)\nWhen this creature enters, create a 2/2 white Knight creature token with vigilance."

    keywords(Keyword.VIGILANCE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 2,
            toughness = 2,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Knight"),
            keywords = setOf(Keyword.VIGILANCE)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "12"
        artist = "Craig J Spearing"
        flavorText = "\"Our duty does not stop at our borders.\""
        imageUri = "https://cards.scryfall.io/normal/front/e/3/e388c433-3a37-45f6-825a-d13d2223b6f7.jpg?1783934607"
    }
}
