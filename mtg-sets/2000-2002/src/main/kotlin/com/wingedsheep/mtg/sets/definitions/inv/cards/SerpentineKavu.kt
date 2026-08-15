package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Serpentine Kavu
 * {4}{G}
 * Creature — Kavu
 * 4/4
 * {R}: This creature gains haste until end of turn.
 */
val SerpentineKavu = card("Serpentine Kavu") {
    manaCost = "{4}{G}"
    colorIdentity = "RG"
    typeLine = "Creature — Kavu"
    oracleText = "{R}: This creature gains haste until end of turn."
    power = 4
    toughness = 4

    activatedAbility {
        cost = Costs.Mana("{R}")
        effect = Effects.GrantKeyword(Keyword.HASTE, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "211"
        artist = "Heather Hudson"
        imageUri = "https://cards.scryfall.io/normal/front/6/9/699f1fe8-02c6-4d95-9231-3f8aefe603da.jpg?1562916212"
    }
}
