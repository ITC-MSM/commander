package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Blighted Cataract
 * Land
 * {T}: Add {C}.
 * {5}{U}, {T}, Sacrifice this land: Draw two cards.
 */
val BlightedCataract = card("Blighted Cataract") {
    manaCost = ""
    colorIdentity = "U"
    typeLine = "Land"
    oracleText = "{T}: Add {C}.\n" +
        "{5}{U}, {T}, Sacrifice this land: Draw two cards."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{5}{U}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.DrawCards(2)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "229"
        artist = "Vincent Proce"
        flavorText = "Once, water ran here. Now only dust and ash fall from the clifftops."
        imageUri = "https://cards.scryfall.io/normal/front/8/9/8908b6b0-a488-426c-954d-458456be0651.jpg?1783938175"
    }
}
