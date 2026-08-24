package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Giant Crab
 * {4}{U}
 * Creature — Crab
 * 3/3
 * {U}: This creature gains shroud until end of turn. (It can't be the target of spells or abilities.)
 */
val GiantCrab = card("Giant Crab") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Crab"
    power = 3
    toughness = 3
    oracleText = "{U}: This creature gains shroud until end of turn. (It can't be the target of spells or abilities.)"

    activatedAbility {
        cost = Costs.Mana("{U}")
        effect = Effects.GrantKeyword(Keyword.SHROUD, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "66"
        artist = "Tom Kyffin"
        flavorText = "During the giant crabs' mating season, Skyshroud nights are filled with the clatter of their skirmishes."
        imageUri = "https://cards.scryfall.io/normal/front/1/1/11c65a35-e219-4b60-ab95-ce7eff67d646.jpg"
    }
}
