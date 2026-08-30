package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Blood Bairn
 * {2}{B}
 * Creature — Vampire
 * 2 / 2
 * Sacrifice another creature: This creature gets +2/+2 until end of turn.
 */
val BloodBairn = card("Blood Bairn") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire"
    power = 2
    toughness = 2
    oracleText = "Sacrifice another creature: This creature gets +2/+2 until end of turn."

    activatedAbility {
        cost = Costs.SacrificeAnother(GameObjectFilter.Creature)
        effect = Effects.ModifyStats(2, 2, EffectTarget.Self)
        description = "Sacrifice another creature: This creature gets +2/+2 until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "87"
        artist = "Ryan Yee"
        flavorText = "The travelers were warned to watch out for children on the road."
        imageUri = "https://cards.scryfall.io/normal/front/a/3/a3fcbbd1-ee51-42a3-ad11-2fd41728c35d.jpg"
    }
}
