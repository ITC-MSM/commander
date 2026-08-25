package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Color
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
 * Basal Sliver
 * {2}{B}
 * Creature — Sliver
 * 2/2
 * All Slivers have "Sacrifice this permanent: Add {B}{B}."
 *
 * "All Slivers" is the bare tribal noun — every Sliver *permanent*, not just the creatures.
 */
val BasalSliver = card("Basal Sliver") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Sliver"
    power = 2
    toughness = 2
    oracleText = "All Slivers have \"Sacrifice this permanent: Add {B}{B}.\""

    staticAbility {
        ability = GrantActivatedAbility(
            ability = ActivatedAbility(
                id = AbilityId.generate(),
                cost = Costs.SacrificeSelf,
                effect = Effects.AddMana(Color.BLACK, 2),
                timing = TimingRule.ManaAbility,
                isManaAbility = true
            ),
            filter = GroupFilter(GameObjectFilter.Permanent.withSubtype("Sliver"))
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "96"
        artist = "Drew Tucker"
        flavorText = "\"Fascinating . . . These creatures display the paradox of tenacity and purposed self-destruction I have sought to breed into my thrulls.\"\n—Endrek Sahr, master breeder"
        imageUri = "https://cards.scryfall.io/normal/front/4/5/4564e9df-bfa3-48e5-a12e-f7e96a504cb1.jpg"
    }
}
