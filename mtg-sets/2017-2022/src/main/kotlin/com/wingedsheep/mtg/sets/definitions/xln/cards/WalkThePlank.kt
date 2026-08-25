package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Walk the Plank
 * {B}{B}
 * Sorcery
 *
 * Destroy target non-Merfolk creature.
 */
val WalkThePlank = card("Walk the Plank") {
    manaCost = "{B}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Destroy target non-Merfolk creature."

    spell {
        val victim = target(
            "target",
            TargetObject(filter = TargetFilter(GameObjectFilter.Creature.notSubtype(Subtype.MERFOLK)))
        )
        effect = Effects.Destroy(victim)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "130"
        artist = "Kieran Yanner"
        flavorText = "When Captain Thorn adds a new ship to his fleet, he gives the crew a simple choice: follow me, or fall in the sea."
        imageUri = "https://cards.scryfall.io/normal/front/0/0/0038ac6a-318f-44fb-bb64-7ae172c4aca3.jpg"
    }
}
