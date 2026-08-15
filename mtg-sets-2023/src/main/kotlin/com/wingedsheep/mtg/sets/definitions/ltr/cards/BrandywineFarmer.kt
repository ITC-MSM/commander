package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Brandywine Farmer
 * {2}{G}
 * Creature — Halfling Peasant
 * 1/1
 * When this creature enters or leaves the battlefield, create a Food token.
 */
val BrandywineFarmer = card("Brandywine Farmer") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Halfling Peasant"
    power = 1
    toughness = 1
    oracleText = "When this creature enters or leaves the battlefield, create a Food token. (It's an artifact with \"{2}, {T}, Sacrifice this token: You gain 3 life.\")"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateFood()
    }

    triggeredAbility {
        trigger = Triggers.LeavesBattlefield
        effect = Effects.CreateFood()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "155"
        artist = "Yuriy Chemezov"
        flavorText = "\"Your land must be a realm of peace and content, and there must gardeners be in high honor.\"\n—Faramir"
        imageUri = "https://cards.scryfall.io/normal/front/1/c/1c24b5fa-506d-4eaf-881b-bc282c74a16c.jpg?1686969248"
    }
}
