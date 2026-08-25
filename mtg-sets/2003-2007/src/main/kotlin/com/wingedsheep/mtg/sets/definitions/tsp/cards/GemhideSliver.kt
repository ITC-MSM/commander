package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Gemhide Sliver
 * {1}{G}
 * Creature — Sliver
 * 1/1
 * All Slivers have "{T}: Add one mana of any color."
 */
val GemhideSliver = card("Gemhide Sliver") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Sliver"
    power = 1
    toughness = 1
    oracleText = "All Slivers have \"{T}: Add one mana of any color.\""

    staticAbility {
        ability = GrantActivatedAbility(
            ability = ActivatedAbility(
                id = AbilityId.generate(),
                cost = Costs.Tap,
                effect = Effects.AddManaOfChoice(),
                timing = TimingRule.ManaAbility,
                isManaAbility = true
            ),
            filter = GroupFilter(GameObjectFilter.Permanent.withSubtype("Sliver"))
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "196"
        artist = "John Matson"
        flavorText = "\"The land is weary. Even Skyshroud is depleted. We must find another source of mana—one that is growing despite our withering world.\"\n—Freyalise"
        imageUri = "https://cards.scryfall.io/normal/front/f/0/f09135b0-fd57-4205-aa74-c9869946c264.jpg"
    }
}
