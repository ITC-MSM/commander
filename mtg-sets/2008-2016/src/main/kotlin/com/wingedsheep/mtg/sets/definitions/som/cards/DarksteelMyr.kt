package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Darksteel Myr
 * {3}
 * Artifact Creature — Myr
 * 0/1
 *
 * Indestructible (Damage and effects that say "destroy" don't destroy this creature. If its toughness is 0 or less, it still dies.)
 */
val DarksteelMyr = card("Darksteel Myr") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Myr"
    power = 0
    toughness = 1
    oracleText = "Indestructible (Damage and effects that say \"destroy\" don't destroy this creature. If its toughness is 0 or less, it still dies.)"
    keywords(Keyword.INDESTRUCTIBLE)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "151"
        artist = "Randis Albion"
        imageUri = "https://cards.scryfall.io/normal/front/0/f/0f5712cf-c6a9-4a2e-90db-8ca17c621724.jpg?1783941710"
    }
}
