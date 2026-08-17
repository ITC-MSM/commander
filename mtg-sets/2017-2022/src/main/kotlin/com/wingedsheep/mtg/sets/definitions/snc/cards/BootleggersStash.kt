package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Bootleggers' Stash
 * {5}{G}
 * Artifact
 *
 * Lands you control have "{T}: Create a Treasure token."
 */
val BootleggersStash = card("Bootleggers' Stash") {
    manaCost = "{5}{G}"
    colorIdentity = "G"
    typeLine = "Artifact"
    oracleText = "Lands you control have \"{T}: Create a Treasure token.\""

    staticAbility {
        ability = GrantActivatedAbility(
            ability = ActivatedAbility(
                id = AbilityId.generate(),
                cost = Costs.Tap,
                effect = Effects.CreateTreasure()
            ),
            filter = GroupFilter(GameObjectFilter.Land.youControl())
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "134"
        artist = "Anastasia Ovchinnikova"
        flavorText = "Labyrinths of tunnels beneath the streets of the Caldaia offer an ample array of discreet routes for enterprising smugglers."
        imageUri = "https://cards.scryfall.io/normal/front/8/0/80b5b7e1-52c2-4453-b3c0-efe2cebad6ce.jpg?1783923109"
    }
}
