package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Darkheart Sliver
 * {B}{G}
 * Creature — Sliver
 * 2/2
 * All Slivers have "Sacrifice this permanent: You gain 3 life."
 *
 * The bare tribal noun — "All **Slivers**", not "All Sliver **creatures**" — names every Sliver
 * *permanent*, so the group is [GameObjectFilter.Permanent] with the subtype. "Sacrifice this
 * permanent" inside the granted quote is the grantee sacrificing itself.
 */
val DarkheartSliver = card("Darkheart Sliver") {
    manaCost = "{B}{G}"
    colorIdentity = "BG"
    typeLine = "Creature — Sliver"
    power = 2
    toughness = 2
    oracleText = "All Slivers have \"Sacrifice this permanent: You gain 3 life.\""

    staticAbility {
        ability = GrantActivatedAbility(
            ability = ActivatedAbility(
                cost = Costs.SacrificeSelf,
                effect = Effects.GainLife(3)
            ),
            filter = GroupFilter(GameObjectFilter.Permanent.withSubtype(Subtype.SLIVER))
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "155"
        artist = "rk post"
        flavorText = "\"At first we thought we were in some haunted wood. Then the branches twisted and scuttled toward us.\"\n—Merrik Aidar, Benalish patrol"
        imageUri = "https://cards.scryfall.io/normal/front/3/5/3541ff31-0043-49a9-abac-77369f95b942.jpg"
    }
}
