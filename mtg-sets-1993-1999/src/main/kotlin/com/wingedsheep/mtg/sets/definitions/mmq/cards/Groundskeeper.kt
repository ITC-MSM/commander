package com.wingedsheep.mtg.sets.definitions.mmq.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Groundskeeper
 * {G}
 * Creature — Human Druid
 * 1/1
 *
 * {1}{G}: Return target basic land card from your graveyard to your hand.
 */
val Groundskeeper = card("Groundskeeper") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Druid"
    power = 1
    toughness = 1
    oracleText = "{1}{G}: Return target basic land card from your graveyard to your hand."

    activatedAbility {
        cost = Costs.Mana("{1}{G}")
        val t = target(
            "target basic land card",
            TargetObject(
                filter = TargetFilter(
                    baseFilter = GameObjectFilter.BasicLand.ownedByYou(),
                    zone = Zone.GRAVEYARD
                )
            )
        )
        effect = Effects.ReturnToHand(t)
        description = "{1}{G}: Return target basic land card from your graveyard to your hand."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "250"
        artist = "Alan Rabinowitz"
        flavorText = "Whereas the rebels fight to defend the land, they work to renew it."
        imageUri = "https://cards.scryfall.io/normal/front/3/1/31d9fe16-562a-4a86-84ed-15cd90b8afc0.jpg?1782720265"
    }
}
