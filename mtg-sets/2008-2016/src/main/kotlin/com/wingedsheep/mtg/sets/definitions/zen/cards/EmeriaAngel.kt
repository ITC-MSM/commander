package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Emeria Angel
 * {2}{W}{W}
 * Creature — Angel
 * 3/3
 * Flying
 * Landfall — Whenever a land you control enters, you may create a 1/1 white Bird creature token with flying.
 *
 * Landfall is [Triggers.LandYouControlEnters] — the `ZoneChangeEvent` over
 * `GameObjectFilter.Land.youControl()` with `TriggerBinding.ANY`.
 */
val EmeriaAngel = card("Emeria Angel") {
    manaCost = "{2}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Angel"
    power = 3
    toughness = 3
    oracleText = "Flying\n" +
        "Landfall — Whenever a land you control enters, you may create a 1/1 white Bird creature token with flying."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.LandYouControlEnters
        optional = true
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Bird"),
            keywords = setOf(Keyword.FLYING),
            imageUri = "https://cards.scryfall.io/normal/front/a/b/abbb3c16-a7ef-4b83-b1a9-637a905229d7.jpg?1783942180"
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "11"
        artist = "Jim Murray"
        flavorText = "When the earth shudders, the sky overflows."
        imageUri = "https://cards.scryfall.io/normal/front/8/b/8b386d39-ba41-42f8-ba05-f9ed602ee23f.jpg"
    }
}
