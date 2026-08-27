package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Reflex Sliver
 * {3}{G}
 * Creature — Sliver
 * 2/2
 * All Sliver creatures have haste.
 */
val ReflexSliver = card("Reflex Sliver") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Sliver"
    power = 2
    toughness = 2
    oracleText = "All Sliver creatures have haste."

    staticAbility {
        ability = GrantKeyword(
            Keyword.HASTE,
            GroupFilter(GameObjectFilter.Creature.withSubtype(Subtype.SLIVER))
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "138"
        artist = "Luca Zontini"
        flavorText = "\"This sliver comes into the world a perfect predator. It's ready to hunt and devour its first meal within seconds of hatching.\"\n—Rukarumel, field journal"
        imageUri = "https://cards.scryfall.io/normal/front/9/0/9017d37d-f47d-405e-95d8-78a8eec8addc.jpg"
    }
}
