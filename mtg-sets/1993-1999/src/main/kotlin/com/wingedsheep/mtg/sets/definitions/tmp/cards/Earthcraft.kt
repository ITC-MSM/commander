package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Earthcraft
 * {1}{G}
 * Enchantment
 * Tap an untapped creature you control: Untap target basic land.
 */
val Earthcraft = card("Earthcraft") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment"
    oracleText = "Tap an untapped creature you control: Untap target basic land."

    activatedAbility {
        cost = Costs.TapPermanents(1, GameObjectFilter.Creature)
        val land = target("target", TargetPermanent(filter = TargetFilter(GameObjectFilter.BasicLand)))
        effect = Effects.Untap(land)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "222"
        artist = "Randy Gallegos"
        flavorText = "\"The land gives up little, but we are masters of persuasion.\"\n" +
            "—Eladamri, Lord of Leaves"
        imageUri = "https://cards.scryfall.io/normal/front/9/d/9dda7531-82a1-4f49-8858-601ddbc6e2bc.jpg"
    }
}
