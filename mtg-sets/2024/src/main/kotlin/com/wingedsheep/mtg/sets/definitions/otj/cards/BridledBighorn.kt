package com.wingedsheep.mtg.sets.definitions.otj.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Bridled Bighorn
 * {3}{W}
 * Creature — Sheep Mount
 * 3/4
 * Vigilance
 * Whenever this creature attacks while saddled, create a 1/1 white Sheep creature token.
 * Saddle 2 (Tap any number of other creatures you control with total power 2 or more:
 * This Mount becomes saddled until end of turn. Saddle only as a sorcery.)
 */
val BridledBighorn = card("Bridled Bighorn") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Sheep Mount"
    power = 3
    toughness = 4
    oracleText = "Vigilance\n" +
        "Whenever this creature attacks while saddled, create a 1/1 white Sheep creature token.\n" +
        "Saddle 2 (Tap any number of other creatures you control with total power 2 or more: This Mount becomes saddled until end of turn. Saddle only as a sorcery.)"

    keywords(Keyword.VIGILANCE)
    keywordAbility(KeywordAbility.saddle(2))

    triggeredAbility {
        trigger = Triggers.Attacks
        triggerCondition = Conditions.SourceIsSaddled
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Sheep"),
            imageUri = "https://cards.scryfall.io/normal/front/d/c/dccb30d0-b491-43be-8aa0-2ee7da86343e.jpg?1712469939"
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "7"
        artist = "Edgar Sánchez Hidalgo"
        imageUri = "https://cards.scryfall.io/normal/front/f/a/fa7bf089-fa9b-4ffc-bf84-45cd51c76463.jpg?1712355251"

        ruling("2024-04-12", "A Mount's saddled status lasts until end of turn, but its abilities that care about it being saddled work only while it remains saddled.")
        ruling("2024-04-12", "Saddle doesn't change whether the Mount can attack. It's saddled the same way whether or not it attacks.")
        ruling("2024-04-12", "You can saddle a Mount even if it's already saddled, but doing so has no additional effect.")
    }
}
