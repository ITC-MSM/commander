package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Lotus Guardian
 * {7}
 * Artifact Creature — Dragon
 * 4/4
 * Flying
 * {T}: Add one mana of any color.
 */
val LotusGuardian = card("Lotus Guardian") {
    manaCost = "{7}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Dragon"
    power = 4
    toughness = 4
    oracleText = "Flying\n{T}: Add one mana of any color."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddAnyColorMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "305"
        artist = "Dana Knutson"
        imageUri = "https://cards.scryfall.io/normal/front/d/d/ddfc6396-5377-4ab3-9c10-8abcdeae2aa1.jpg?1562939672"
    }
}
