package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Dark Nourishment
 * {4}{B}
 * Instant
 *
 * Dark Nourishment deals 3 damage to any target. You gain 3 life.
 */
val DarkNourishment = card("Dark Nourishment") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Dark Nourishment deals 3 damage to any target. You gain 3 life."

    spell {
        val victim = target("target", Targets.Any)
        effect = Effects.DealDamage(3, victim) then Effects.GainLife(3)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "97"
        artist = "Josh Hass"
        flavorText = "Demons lurk in the shadows of ancient ruins, spreading plague and corruption across the land."
        imageUri = "https://cards.scryfall.io/normal/front/0/5/053c4cf0-992b-4f76-b8d1-cd67f894172c.jpg"
    }
}
