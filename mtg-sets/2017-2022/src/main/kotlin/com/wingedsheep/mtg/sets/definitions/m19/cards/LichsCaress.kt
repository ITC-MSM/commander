package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Lich's Caress
 * {3}{B}{B}
 * Sorcery
 * Destroy target creature. You gain 3 life.
 *
 * Two ordered clauses over one target: [Effects.Destroy] on the chosen creature, then a flat
 * [Effects.GainLife] for the controller. The life gain is part of the same resolution, so an
 * illegal target on resolution fizzles the whole spell and no life is gained.
 */
val LichsCaress = card("Lich's Caress") {
    manaCost = "{3}{B}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Destroy target creature. You gain 3 life."

    spell {
        val creature = target("target", Targets.Creature)
        effect = Effects.Composite(
            Effects.Destroy(creature),
            Effects.GainLife(3)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "105"
        artist = "Josh Hass"
        flavorText = "A lich must consume mortal souls to feed its eternal life."
        imageUri = "https://cards.scryfall.io/normal/front/3/2/32bd3acd-aa62-4708-9336-e3430fd0e541.jpg"
    }
}
