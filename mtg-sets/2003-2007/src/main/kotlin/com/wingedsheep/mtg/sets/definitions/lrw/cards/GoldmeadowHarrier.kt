package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Goldmeadow Harrier
 * {W}
 * Creature — Kithkin Soldier
 * 1/1
 * {W}, {T}: Tap target creature.
 */
val GoldmeadowHarrier = card("Goldmeadow Harrier") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Kithkin Soldier"
    power = 1
    toughness = 1
    oracleText = "{W}, {T}: Tap target creature."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{W}"), Costs.Tap)
        val creature = target("target creature", Targets.Creature)
        effect = Effects.Tap(creature)
        description = "{W}, {T}: Tap target creature."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "17"
        artist = "Steve Prescott"
        flavorText = "\"It's a proven fact that sling-stones from the dawn side of the riverbank sail the farthest and truest.\"\n—Deagan, cenn of Burrenton"
        imageUri = "https://cards.scryfall.io/normal/front/b/6/b6a7508c-2815-40eb-92f7-3e66dfb28484.jpg?1783942915"
    }
}
