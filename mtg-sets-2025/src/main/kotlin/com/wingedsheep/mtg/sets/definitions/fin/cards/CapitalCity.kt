package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.TimingRule


/**
 * Capital City
 * Land — Town
 * {T}: Add {C}.
 * {1}, {T}: Add one mana of any color.
 * Cycling {2} ({2}, Discard this card: Draw a card.)
 */
val CapitalCity = card("Capital City") {
    manaCost = ""
    colorIdentity = ""
    typeLine = "Land — Town"
    oracleText = "{T}: Add {C}.\n{1}, {T}: Add one mana of any color.\nCycling {2} ({2}, Discard this card: Draw a card.)"
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap)
        effect = Effects.AddManaOfChoice()
        manaAbility = true
        timing = TimingRule.ManaAbility
    }
    keywordAbility(KeywordAbility.cycling("{2}"))
    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "274"
        artist = "Wei Guan"
        flavorText = "A city built upon the Isles of Ark that straddle the continents of Storm and Ash."
        imageUri = "https://cards.scryfall.io/normal/front/f/7/f73ce8ec-c916-48eb-ae20-c0d6d03d7145.jpg"
    }
}
