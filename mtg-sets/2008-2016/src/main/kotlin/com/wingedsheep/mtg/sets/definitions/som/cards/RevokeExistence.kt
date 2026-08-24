package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Revoke Existence
 * {1}{W}
 * Sorcery
 *
 * Exile target artifact or enchantment.
 */
val RevokeExistence = card("Revoke Existence") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Exile target artifact or enchantment."

    spell {
        val t = target("target", Targets.ArtifactOrEnchantment)
        effect = Effects.Exile(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "18"
        artist = "Allen Williams"
        flavorText = "\"No half measures, no regrets. We'll tell no stories of this day. It will be as if it never existed at all.\"\n—Ganedor, loxodon mystic"
        imageUri = "https://cards.scryfall.io/normal/front/1/8/18ae62f9-361c-4849-b0af-2b08fc0421c8.jpg?1783941744"
    }
}
