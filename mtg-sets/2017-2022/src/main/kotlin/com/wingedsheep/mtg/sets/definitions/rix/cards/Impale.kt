package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Impale
 * {2}{B}{B}
 * Sorcery
 * Destroy target creature.
 */
val Impale = card("Impale") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Destroy target creature."

    spell {
        val victim = target("target creature", Targets.Creature)
        effect = Effects.Destroy(victim)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "76"
        artist = "Josh Hass"
        flavorText = "Never let the glitter of gold distract you from the gleam of steel in the shadows."
        imageUri = "https://cards.scryfall.io/normal/front/d/f/dfa0c4f7-3497-467d-9453-104fb4b5a0f3.jpg?1783935310"
    }
}
