package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Color
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
 * Gearsmith Guardian
 * {5}
 * Artifact Creature — Construct
 * 3/5
 * This creature gets +2/+0 as long as you control a blue creature.
 */
val GearsmithGuardian = card("Gearsmith Guardian") {
    manaCost = "{5}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Construct"
    power = 3
    toughness = 5
    oracleText = "This creature gets +2/+0 as long as you control a blue creature."

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(2, 0, Filters.Self),
            condition = Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Creature.withColor(Color.BLUE)),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "237"
        artist = "Deruchenko Alexander"
        flavorText = "Made in its creator's image, though slightly more clangy."
        imageUri = "https://cards.scryfall.io/normal/front/1/d/1d532b01-8bf7-4a27-a438-db03bcd00694.jpg?1783934512"
    }
}
