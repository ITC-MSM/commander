package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Sun-Collared Raptor
 * {1}{R}
 * Creature — Dinosaur
 * 1/2
 * Trample
 * {2}{R}: This creature gets +3/+0 until end of turn.
 */
val SunCollaredRaptor = card("Sun-Collared Raptor") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dinosaur"
    oracleText = "Trample\n{2}{R}: This creature gets +3/+0 until end of turn."
    power = 1
    toughness = 2

    keywords(Keyword.TRAMPLE)

    activatedAbility {
        cost = Costs.Mana("{2}{R}")
        effect = Effects.ModifyStats(3, 0, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "118"
        artist = "Zoltan Boros"
        flavorText = "\"With Tilonalli's gifts, even the smallest of us can become great.\"\n—Huatli"
        imageUri = "https://cards.scryfall.io/normal/front/6/2/62fbd1bc-3e57-43d5-ad54-443ca740fcc4.jpg?1783935291"
    }
}
