package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Sunastian Falconer
 * {3}{R}{G}
 * Legendary Creature — Human Shaman
 * 4/4
 *
 * {T}: Add {C}{C}.
 */
val SunastianFalconer = card("Sunastian Falconer") {
    manaCost = "{3}{R}{G}"
    colorIdentity = "GR"
    typeLine = "Legendary Creature — Human Shaman"
    power = 4
    toughness = 4
    oracleText = "{T}: Add {C}{C}."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(2)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "261"
        artist = "Christopher Rush"
        flavorText = "Sunastian has roots in both sorcery and swordplay; he has learned never to depend too " +
            "heavily on the latter."
        imageUri = "https://cards.scryfall.io/normal/front/5/8/587075f3-a568-4089-83ca-fe1e473c025d.jpg?1783948033"
    }
}
