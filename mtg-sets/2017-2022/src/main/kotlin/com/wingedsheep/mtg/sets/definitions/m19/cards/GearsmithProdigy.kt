package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Gearsmith Prodigy
 * {U}
 * Creature — Human Artificer
 * 1/2
 * This creature gets +1/+0 as long as you control an artifact.
 */
val GearsmithProdigy = card("Gearsmith Prodigy") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Artificer"
    power = 1
    toughness = 2
    oracleText = "This creature gets +1/+0 as long as you control an artifact."

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(1, 0, Filters.Self),
            condition = Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Artifact),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "57"
        artist = "Deruchenko Alexander"
        flavorText = "Young artificers on Kaladesh let their imaginations run wild."
        imageUri = "https://cards.scryfall.io/normal/front/7/7/77d9e666-d9c9-4ccd-89a5-83de79677fa6.jpg?1783934589"
    }
}
