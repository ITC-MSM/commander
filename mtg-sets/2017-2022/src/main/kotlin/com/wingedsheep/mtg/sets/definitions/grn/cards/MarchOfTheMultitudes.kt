package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * March of the Multitudes
 * {X}{G}{W}{W}
 * Instant
 *
 * Convoke
 * Create X 1/1 white Soldier creature tokens with lifelink.
 */
val MarchOfTheMultitudes = card("March of the Multitudes") {
    manaCost = "{X}{G}{W}{W}"
    colorIdentity = "WG"
    typeLine = "Instant"
    oracleText = "Convoke (Your creatures can help cast this spell. Each creature you tap while " +
        "casting this spell pays for {1} or one mana of that creature's color.)\n" +
        "Create X 1/1 white Soldier creature tokens with lifelink."

    keywords(Keyword.CONVOKE)

    spell {
        effect = Effects.CreateToken(
            count = DynamicAmount.XValue,
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Soldier"),
            keywords = setOf(Keyword.LIFELINK)
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "188"
        artist = "Zack Stella"
        flavorText = "\"Our forces number more than the leaves of Vitu-Ghazi. Do not provoke us.\""
        imageUri = "https://cards.scryfall.io/normal/front/2/c/2cc2b646-0181-4f0a-a141-00ca56069a06.jpg"
    }
}
