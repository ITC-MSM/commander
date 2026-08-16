package com.wingedsheep.mtg.sets.definitions.eld.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Weaselback Redcap
 * {R}
 * Creature — Goblin Knight
 * 1/1
 * {1}{R}: This creature gets +2/+0 until end of turn.
 */
val WeaselbackRedcap = card("Weaselback Redcap") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Knight"
    power = 1
    toughness = 1
    oracleText = "{1}{R}: This creature gets +2/+0 until end of turn."

    activatedAbility {
        cost = Costs.Mana("{1}{R}")
        effect = Effects.ModifyStats(2, 0, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "148"
        artist = "Grzegorz Rutkowski"
        flavorText = "\"I would rather cast myself into the abyss than let my blood stain the cap of those monsters.\" —Syr Alin, the Lion's Claw"
        imageUri = "https://cards.scryfall.io/normal/front/3/3/33a78207-fd76-4112-a257-54a25da6f818.jpg?1783932614"
    }
}
