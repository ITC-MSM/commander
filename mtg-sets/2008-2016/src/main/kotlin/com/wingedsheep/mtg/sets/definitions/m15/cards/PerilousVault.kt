package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Perilous Vault
 * {4}
 * Artifact
 * {5}, {T}, Exile this artifact: Exile all nonland permanents.
 *
 * A group exile, not a destroy — indestructible and regeneration do nothing about it. The Vault
 * exiles itself as a *cost*, so it is already gone before the group is gathered.
 */
val PerilousVault = card("Perilous Vault") {
    manaCost = "{4}"
    typeLine = "Artifact"
    oracleText = "{5}, {T}, Exile this artifact: Exile all nonland permanents."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{5}"), Costs.Tap, Costs.ExileSelf)
        effect = Effects.ForEachInGroup(
            GroupFilter.AllNonlandPermanents,
            Effects.Exile(EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "224"
        artist = "Sam Burley"
        flavorText = "The spirit dragon Ugin arranged the hedrons of Zendikar to direct leylines of energy. To disrupt one is to unleash devastation and chaos."
        imageUri = "https://cards.scryfall.io/normal/front/9/a/9a5f1810-a852-454f-b1d6-eb40ea4e0148.jpg?1783939156"
    }
}
