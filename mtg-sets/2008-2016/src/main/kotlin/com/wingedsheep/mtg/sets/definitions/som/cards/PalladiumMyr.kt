package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Palladium Myr
 * {3}
 * Artifact Creature — Myr
 * 2/2
 *
 * {T}: Add {C}{C}.
 */
val PalladiumMyr = card("Palladium Myr") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Myr"
    power = 2
    toughness = 2
    oracleText = "{T}: Add {C}{C}."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(2)
        manaAbility = true
        timing = TimingRule.ManaAbility
        description = "{T}: Add {C}{C}."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "190"
        artist = "Alan Pollack"
        flavorText = "The myr are like the Glimmervoid: blank canvases on which to build grand creations."
        imageUri = "https://cards.scryfall.io/normal/front/1/8/18c016ad-bb82-4944-8c06-ab180b808041.jpg?1783941700"
    }
}
