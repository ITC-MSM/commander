package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.GainLifeEffect


/**
 * Adventurer's Inn
 * Land — Town
 * When this land enters, you gain 2 life.
 * {T}: Add {C}.
 */
val AdventurersInn = card("Adventurer's Inn") {
    manaCost = ""
    colorIdentity = ""
    typeLine = "Land — Town"
    oracleText = "When this land enters, you gain 2 life.\n{T}: Add {C}."
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = GainLifeEffect(2)
    }
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "271"
        artist = "Allen Morris"
        flavorText = "\"Welcome! We have rooms for 200 gil a night. Would you like to stay and rest your body and mind?\"\n—Innkeep"
        imageUri = "https://cards.scryfall.io/normal/front/f/0/f0da2ee1-986e-4cbf-92eb-d96fdb572ca5.jpg"
    }
}
