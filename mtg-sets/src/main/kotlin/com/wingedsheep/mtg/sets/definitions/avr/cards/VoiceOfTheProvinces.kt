package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Voice of the Provinces
 * {4}{W}{W}
 * Creature — Angel
 * 3/3
 * Flying
 * When this creature enters, create a 1/1 white Human creature token.
 */
val VoiceOfTheProvinces = card("Voice of the Provinces") {
    manaCost = "{4}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Angel"
    oracleText = "Flying\nWhen this creature enters, create a 1/1 white Human creature token."
    power = 3
    toughness = 3

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Human"),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "40"
        artist = "Igor Kieryluk"
        flavorText = "Her horn is heard across Innistrad, lifting the hearts of the righteous."
        imageUri =
            "https://cards.scryfall.io/normal/front/b/7/b785276b-3778-49f3-b46f-a1f3d91db097.jpg?1783940726"
    }
}
