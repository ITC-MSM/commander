package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Deeproot Waters
 * {2}{U}
 * Enchantment
 *
 * Whenever you cast a Merfolk spell, create a 1/1 blue Merfolk creature token with hexproof.
 */
val DeeprootWaters = card("Deeproot Waters") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment"
    oracleText = "Whenever you cast a Merfolk spell, create a 1/1 blue Merfolk creature token " +
        "with hexproof. (A creature with hexproof can't be the target of spells or abilities " +
        "your opponents control.)"

    triggeredAbility {
        trigger = Triggers.YouCastSubtype(Subtype.MERFOLK)
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.BLUE),
            creatureTypes = setOf("Merfolk"),
            keywords = setOf(Keyword.HEXPROOF),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "51"
        artist = "Zezhou Chen"
        flavorText = "A visit to the Deeproot Tree and its ancient spring replenishes a merfolk's connection to nature."
        imageUri = "https://cards.scryfall.io/normal/front/2/4/24bec3b7-5dfc-4b48-ae8f-5bf49470d030.jpg"
    }
}
