package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Kozilek's Channeler
 * {5}
 * Creature — Eldrazi
 * 4/4
 * {T}: Add {C}{C}.
 */
val KozileksChanneler = card("Kozilek's Channeler") {
    manaCost = "{5}"
    colorIdentity = ""
    typeLine = "Creature — Eldrazi"
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
        rarity = Rarity.COMMON
        collectorNumber = "10"
        artist = "Jason Felix"
        flavorText = "\"In the dark places of our world, something horrible is growing. I fear our foes may be more " +
            "numerous than we had imagined.\"\n" +
            "—Nissa Revane"
        imageUri = "https://cards.scryfall.io/normal/front/c/5/c550d179-32ec-4ad8-91c2-d79320a21cba.jpg?1783938223"
    }
}
