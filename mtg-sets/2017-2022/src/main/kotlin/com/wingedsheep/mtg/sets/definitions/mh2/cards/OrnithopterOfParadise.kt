package com.wingedsheep.mtg.sets.definitions.mh2.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Ornithopter of Paradise
 * {2}
 * Artifact Creature — Thopter
 * 0/2
 *
 * Flying
 * {T}: Add one mana of any color.
 */
val OrnithopterOfParadise = card("Ornithopter of Paradise") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Thopter"
    oracleText = "Flying\n{T}: Add one mana of any color."
    power = 0
    toughness = 2

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddManaOfChoice()
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "232"
        artist = "Raoul Vitale"
        flavorText = "\"Through even our darkest days, we must never cease creating. Each new invention brings value to the world, be it beauty, utility, or both.\"\n—Rashmi, aether-seer"
        imageUri = "https://cards.scryfall.io/normal/front/0/2/025b0f0f-daf6-4071-82e7-39c015447ce4.jpg?1783926803"
    }
}
