package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Radiant Fountain
 * Land
 * When this land enters, you gain 2 life.
 * {T}: Add {C}.
 */
val RadiantFountain = card("Radiant Fountain") {
    typeLine = "Land"
    oracleText =
        "When this land enters, you gain 2 life.\n" +
        "{T}: Add {C}."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(2)
        description = "When this land enters, you gain 2 life."
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "245"
        artist = "Adam Paquette"
        flavorText = "\"All peoples treasure a place where the weary traveler may drink in peace.\"\n—Ajani Goldmane"
        imageUri = "https://cards.scryfall.io/normal/front/8/0/8090e148-2550-4156-89e3-052abda9f0e7.jpg?1783939152"
    }
}
