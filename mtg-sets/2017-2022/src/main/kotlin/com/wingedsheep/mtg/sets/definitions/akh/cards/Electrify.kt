package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Electrify
 * {3}{R}
 * Instant
 * Electrify deals 4 damage to target creature.
 */
val Electrify = card("Electrify") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Electrify deals 4 damage to target creature."

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.DealDamage(4, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "129"
        artist = "Craig J Spearing"
        flavorText = "\"Some hid from the storm. I embraced it and learned its name.\""
        imageUri = "https://cards.scryfall.io/normal/front/2/1/21c67128-2e5a-4c97-b265-6f3c73b4997a.jpg?1783936490"
    }
}
