package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Sea Gate Loremaster
 * {4}{U}
 * Creature — Merfolk Wizard Ally
 * 1/3
 * {T}: Draw a card for each Ally you control.
 */
val SeaGateLoremaster = card("Sea Gate Loremaster") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Wizard Ally"
    power = 1
    toughness = 3
    oracleText = "{T}: Draw a card for each Ally you control."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.DrawCards(
            DynamicAmounts.battlefield(Player.You, GameObjectFilter.Permanent.withSubtype("Ally")).count()
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "63"
        artist = "Dave Kendall"
        flavorText = "\"He's a living library. He remembers everything our band of explorers has seen, and we can use that to our advantage.\"\n—Zahr Gada, Halimar expedition leader"
        imageUri = "https://cards.scryfall.io/normal/front/5/c/5cd723c8-4b3d-4fbb-a825-79934279382d.jpg"
    }
}
