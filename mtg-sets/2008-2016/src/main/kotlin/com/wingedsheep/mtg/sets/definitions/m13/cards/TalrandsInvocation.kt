package com.wingedsheep.mtg.sets.definitions.m13.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Talrand's Invocation
 * {2}{U}{U}
 * Sorcery
 *
 * Create two 2/2 blue Drake creature tokens with flying.
 */
val TalrandsInvocation = card("Talrand's Invocation") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Create two 2/2 blue Drake creature tokens with flying."

    spell {
        effect = Effects.CreateToken(
            power = 2,
            toughness = 2,
            colors = setOf(Color.BLUE),
            creatureTypes = setOf("Drake"),
            keywords = setOf(Keyword.FLYING),
            count = 2
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "73"
        artist = "Svetlin Velinov"
        flavorText = "After Talrand conquered the depths of Shandalar, his ambitions drove him skyward, joined by servants whose drive and curiosity equaled his own."
        imageUri = "https://cards.scryfall.io/normal/front/c/2/c2cd809c-557a-42a5-950b-56b5b47b325b.jpg"
    }
}
