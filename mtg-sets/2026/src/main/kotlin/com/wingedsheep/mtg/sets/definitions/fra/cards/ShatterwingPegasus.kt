package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

val ShatterwingPegasus = card("Shatterwing Pegasus") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Pegasus"
    oracleText = "Flying\n{4}{W}: Creatures you control get +1/+1 until end of turn."
    power = 2
    toughness = 3

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{4}{W}")
        effect = Effects.ForEachInGroup(GroupFilter.AllCreaturesYouControl, Effects.ModifyStats(1, 1, EffectTarget.Self))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "21"
        artist = "Antonio José Manzanedo"
        flavorText = "Winged with the shards of shattered star arches, it circles above the Eradia. A rare glimmer of hope for students caught in the perilous wasteland that surrounds Jace's tower."
        imageUri = "https://cards.scryfall.io/normal/front/e/2/e29095de-59ec-4562-ba8e-73f952e457ae.jpg?1789385580"
        inBooster = false
    }
}
