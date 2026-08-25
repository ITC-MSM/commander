package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Unknown Shores
 * Land
 *
 * {T}: Add {C}.
 * {1}, {T}: Add one mana of any color.
 */
val UnknownShores = card("Unknown Shores") {
    typeLine = "Land"
    colorIdentity = ""
    oracleText = "{T}: Add {C}.\n" +
        "{1}, {T}: Add one mana of any color."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap)
        effect = Effects.AddAnyColorMana()
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "229"
        artist = "Seb McKinnon"
        flavorText = "Philosophers speak of a place where myths wash like tides upon the shores of the real."
        imageUri = "https://cards.scryfall.io/normal/front/f/f/fff118a5-765b-4ba1-8f12-ce6f24b2459b.jpg"
    }
}
