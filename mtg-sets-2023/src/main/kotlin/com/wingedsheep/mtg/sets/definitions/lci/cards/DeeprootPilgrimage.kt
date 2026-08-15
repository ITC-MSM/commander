package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Deeproot Pilgrimage
 * {1}{U}
 * Enchantment
 * Whenever one or more nontoken Merfolk you control become tapped, create a 1/1 blue Merfolk
 * creature token with hexproof.
 */
val DeeprootPilgrimage = card("Deeproot Pilgrimage") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment"
    oracleText = "Whenever one or more nontoken Merfolk you control become tapped, create a 1/1 blue " +
        "Merfolk creature token with hexproof."

    triggeredAbility {
        // Batch trigger (CR 603.2c): tapping several Merfolk at once (attacking, convoke) makes one
        // token, not one per Merfolk.
        trigger = Triggers.OneOrMoreBecomeTapped(
            GameObjectFilter.Creature.withSubtype("Merfolk").youControl().nontoken()
        )
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.BLUE),
            creatureTypes = setOf("Merfolk"),
            keywords = setOf(Keyword.HEXPROOF),
            imageUri = "https://cards.scryfall.io/normal/front/f/5/f5d353ad-7160-41fa-809c-d76b36478a2a.jpg?1783913608"
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "52"
        artist = "Rémi Jacquot"
        flavorText = "\"These glyphs are more than trail markings! They tell a story as they go. " +
            "But does it end in celebration or tragedy?\""
        imageUri = "https://cards.scryfall.io/normal/front/e/2/e2449311-a705-4a31-a345-a36d436ae561.jpg?1782694567"
    }
}
