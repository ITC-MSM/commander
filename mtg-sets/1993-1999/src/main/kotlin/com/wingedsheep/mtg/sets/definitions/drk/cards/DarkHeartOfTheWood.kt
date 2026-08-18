package com.wingedsheep.mtg.sets.definitions.drk.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Dark Heart of the Wood
 * {B}{G}
 * Enchantment
 * Sacrifice a Forest: You gain 3 life.
 *
 * The sacrifice is the whole cost — no tap, no mana — so the ability can be activated as often
 * as there are Forests to feed it.
 */
val DarkHeartOfTheWood = card("Dark Heart of the Wood") {
    manaCost = "{B}{G}"
    colorIdentity = "BG"
    typeLine = "Enchantment"
    oracleText = "Sacrifice a Forest: You gain 3 life."

    activatedAbility {
        cost = Costs.Sacrifice(GameObjectFilter.Land.withSubtype(Subtype.FOREST))
        effect = Effects.GainLife(3)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "95"
        artist = "Christopher Rush"
        flavorText = "Even the Goblins shun this haunted place, where the tree limbs twist in agony and the ground seems to scuttle under your feet."
        imageUri = "https://cards.scryfall.io/normal/front/e/3/e3d3df64-1e90-4aef-86ae-0062aa23ff30.jpg?1783947928"
    }
}
