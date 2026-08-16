package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Meeting of Minds
 * {3}{U}
 * Instant
 * Convoke
 * Draw two cards.
 */
val MeetingOfMinds = card("Meeting of Minds") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Convoke (Your creatures can help cast this spell. Each creature you tap while " +
        "casting this spell pays for {1} or one mana of that creature's color.)\n" +
        "Draw two cards."

    keywords(Keyword.CONVOKE)

    spell {
        effect = Effects.DrawCards(2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "66"
        artist = "Milivoj Ćeran"
        flavorText = "\"If something seems impossible, you probably haven't tried asking for help.\"\n" +
            "—Niambi, cleric of Dominaria"
        imageUri = "https://cards.scryfall.io/normal/front/5/0/508b8650-c283-4e54-abdc-32ec2fb1ee34.jpg?1783917031"
        ruling(
            "2024-01-12",
            "When calculating a spell's total cost, include any alternative costs, additional " +
                "costs, or anything else that increases or reduces the cost to cast the spell. " +
                "Convoke applies after the total cost is calculated. Convoke doesn't change a " +
                "spell's mana cost or mana value."
        )
    }
}
