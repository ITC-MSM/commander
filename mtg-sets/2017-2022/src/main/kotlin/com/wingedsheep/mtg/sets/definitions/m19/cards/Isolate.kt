package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Isolate
 * {W}
 * Instant
 * Exile target permanent with mana value 1.
 */
val Isolate = card("Isolate") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Exile target permanent with mana value 1."

    spell {
        val t = target("target", TargetPermanent(filter = TargetFilter.Permanent.manaValue(1)))
        effect = Effects.Move(t, Zone.EXILE)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "17"
        artist = "Victor Adame Minguez"
        flavorText = "Threefold were his crimes, doubled were his pleas, singular was his fate."
        imageUri = "https://cards.scryfall.io/normal/front/3/d/3d9ce5eb-eaeb-4c93-8d31-4aeb8fcc4cce.jpg"
    }
}
