package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Sylvan Caryatid
 * {1}{G}
 * Creature — Plant
 * 0 / 3
 *
 * Defender, hexproof
 * {T}: Add one mana of any color.
 */
val SylvanCaryatid = card("Sylvan Caryatid") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Plant"
    power = 0
    toughness = 3
    oracleText = "Defender, hexproof\n{T}: Add one mana of any color."

    keywords(Keyword.DEFENDER, Keyword.HEXPROOF)

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddAnyColorMana()
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "180"
        artist = "Chase Stone"
        flavorText = "Those who enter the copse never leave. They find peace there and take root, becoming part of the ever-growing grove."
        imageUri = "https://cards.scryfall.io/normal/front/d/4/d40b65c1-b24d-492d-81b9-d8474ebdc08c.jpg"
    }
}
