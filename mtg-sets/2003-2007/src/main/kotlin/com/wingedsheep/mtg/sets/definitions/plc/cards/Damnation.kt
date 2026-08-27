package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Damnation
 * {2}{B}{B}
 * Sorcery
 * Destroy all creatures. They can't be regenerated.
 *
 * The Planar Chaos colour-shifted Wrath of God. "They can't be regenerated" rides the sweep as
 * `noRegenerate` rather than being a second effect.
 */
val Damnation = card("Damnation") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Destroy all creatures. They can't be regenerated."

    spell {
        effect = Effects.DestroyAll(GameObjectFilter.Creature, noRegenerate = true)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "85"
        artist = "Kev Walker"
        imageUri = "https://cards.scryfall.io/normal/front/2/6/26c68473-70ca-40ba-b5c6-71ec30f88a2c.jpg"
    }
}
