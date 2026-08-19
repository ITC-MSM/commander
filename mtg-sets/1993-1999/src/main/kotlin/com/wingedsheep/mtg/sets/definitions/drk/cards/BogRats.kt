package com.wingedsheep.mtg.sets.definitions.drk.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedBy
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Bog Rats
 * {B}
 * Creature — Rat
 * 1/1
 * This creature can't be blocked by Walls.
 */
val BogRats = card("Bog Rats") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Creature — Rat"
    power = 1
    toughness = 1
    oracleText = "This creature can't be blocked by Walls."

    staticAbility {
        ability = CantBeBlockedBy(GameObjectFilter.Creature.withSubtype(Subtype.WALL))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "42"
        artist = "Ron Spencer"
        flavorText = "Their stench was vile and strong enough, but not nearly as powerful as their hunger."
        imageUri = "https://cards.scryfall.io/normal/front/d/6/d64c9153-bc6d-4a64-885f-c039a5487a31.jpg?1783947941"
    }
}
