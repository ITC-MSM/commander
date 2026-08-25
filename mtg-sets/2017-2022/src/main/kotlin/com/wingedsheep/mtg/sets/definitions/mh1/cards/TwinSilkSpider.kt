package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Twin-Silk Spider
 * {2}{G}
 * Creature — Spider
 * 1/2
 * Reach
 * When this creature enters, create a 1/2 green Spider creature token with reach.
 *
 * The token is a copy of the printed body in everything the text names — 1/2, green, Spider, reach
 * — but it is a plain token, not a copy effect, so [Keyword.REACH] is repeated in
 * [Effects.CreateToken]'s `keywords` as its own grant.
 */
val TwinSilkSpider = card("Twin-Silk Spider") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Spider"
    power = 1
    toughness = 2
    oracleText = "Reach\n" +
        "When this creature enters, create a 1/2 green Spider creature token with reach."

    keywords(Keyword.REACH)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 1,
            toughness = 2,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Spider"),
            keywords = setOf(Keyword.REACH),
        )
        description = "When this creature enters, create a 1/2 green Spider creature token with reach."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "188"
        artist = "Ben Maier"
        flavorText = "A forest-wide network of webs brings a hungry couple to captured prey."
        imageUri = "https://cards.scryfall.io/normal/front/7/c/7cf3188c-879b-4b18-88b4-6237d7162271.jpg?1783933088"
    }
}
