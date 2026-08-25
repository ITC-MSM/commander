package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Fire Sprites
 * {1}{G}
 * Creature — Faerie
 * 1/1
 *
 * Flying
 * {G}, {T}: Add {R}.
 */
val FireSprites = card("Fire Sprites") {
    manaCost = "{1}{G}"
    colorIdentity = "GR"
    typeLine = "Creature — Faerie"
    power = 1
    toughness = 1
    oracleText = "Flying\n{G}, {T}: Add {R}."

    keywords(Keyword.FLYING)
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{G}"), Costs.Tap)
        effect = Effects.AddMana(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "186"
        artist = "Julie Baroh"
        imageUri = "https://cards.scryfall.io/normal/front/d/2/d26fa79a-ede8-4c80-98d5-f49696f8104d.jpg?1783948048"
    }
}
