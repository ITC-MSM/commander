package com.wingedsheep.mtg.sets.definitions.mir.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter


/**
 * Serene Heart
 * {1}{G}
 * Instant
 * Destroy all Auras.
 */
val SereneHeart = card("Serene Heart") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Destroy all Auras."
    spell {
        effect = Effects.DestroyAll(GameObjectFilter.Enchantment.withSubtype(Subtype.AURA))
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "242"
        artist = "D. Alexander Gregory"
        flavorText = "\"If magic is your crutch, cast it aside and learn to walk without it.\"\n—Teferi"
        imageUri = "https://cards.scryfall.io/normal/front/a/f/aff19d9d-8069-4f8d-a81b-e2fcd94c13b3.jpg"
    }
}
