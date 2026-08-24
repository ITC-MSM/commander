package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Crush Contraband
 * {3}{W}
 * Instant
 * Choose one or both —
 * • Exile target artifact.
 * • Exile target enchantment.
 */
val CrushContraband = card("Crush Contraband") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Choose one or both —\n" +
        "• Exile target artifact.\n" +
        "• Exile target enchantment."

    spell {
        modal(chooseCount = 2, minChooseCount = 1) {
            mode("Exile target artifact") {
                val artifact = target("target", Targets.Artifact)
                effect = Effects.Exile(artifact)
            }
            mode("Exile target enchantment") {
                val enchantment = target("target", Targets.Enchantment)
                effect = Effects.Exile(enchantment)
            }
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "7"
        artist = "Jason A. Engle"
        flavorText = "The Izzet mage knew she would neither get her thermoinverter back nor have the satisfaction of exploding it herself."
        imageUri = "https://cards.scryfall.io/normal/front/1/3/13e162b3-2e5a-4235-a2a7-1c8e3e9f2c19.jpg?1783934202"
    }
}
