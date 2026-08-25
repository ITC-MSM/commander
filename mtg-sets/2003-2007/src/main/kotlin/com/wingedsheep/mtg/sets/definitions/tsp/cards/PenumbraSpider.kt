package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Penumbra Spider
 * {2}{G}{G}
 * Creature — Spider
 * 2 / 4
 * Reach
 * When this creature dies, create a 2/4 black Spider creature token with reach.
 */
val PenumbraSpider = card("Penumbra Spider") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Spider"
    power = 2
    toughness = 4
    oracleText = "Reach\n" +
        "When this creature dies, create a 2/4 black Spider creature token with reach."

    keywords(Keyword.REACH)

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.CreateToken(
            power = 2,
            toughness = 4,
            colors = setOf(Color.BLACK),
            creatureTypes = setOf("Spider"),
            keywords = setOf(Keyword.REACH)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "210"
        artist = "Jeff Easley"
        flavorText = "When it snared a passing cockatrice, its own soul darkly doubled."
        imageUri = "https://cards.scryfall.io/normal/front/6/a/6a989ac1-df69-45e7-98bf-564cc7c38973.jpg"
    }
}
