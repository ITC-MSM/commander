package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Manakin
 * {2}
 * Artifact Creature — Construct
 * 1/1
 * {T}: Add {C}.
 */
val Manakin = card("Manakin") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Construct"
    power = 1
    toughness = 1
    oracleText = "{T}: Add {C}."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "296"
        artist = "Scott Kirschner"
        flavorText = "Hanna regarded Squee sternly. \"Because it's *not* a toy, no matter how much it may look like one,\" she said, taking the manakin from him."
        imageUri = "https://cards.scryfall.io/normal/front/3/d/3d33ce7f-f318-4161-843a-f5bb6d6e3d29.jpg?1783946602"
    }
}
