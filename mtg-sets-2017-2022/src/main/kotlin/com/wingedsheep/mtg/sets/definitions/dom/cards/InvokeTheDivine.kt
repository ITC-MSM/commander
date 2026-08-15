package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Invoke the Divine
 * {2}{W}
 * Instant
 * Destroy target artifact or enchantment. You gain 4 life.
 */
val InvokeTheDivine = card("Invoke the Divine") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Destroy target artifact or enchantment. You gain 4 life."

    spell {
        val t = target("target", Targets.ArtifactOrEnchantment)
        effect = Effects.Destroy(t)
            .then(Effects.GainLife(4))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "22"
        artist = "Magali Villeneuve"
        flavorText = "\"Let go of all that harms you. Cast your burdens into the darkness, and build for the faithful a house of light.\" —Song of All, canto 1008"
        imageUri = "https://cards.scryfall.io/normal/front/7/f/7ffe72ae-ea5c-4065-9f5b-3d931f72952d.jpg?1595274650"
    }
}
