package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Shore Keeper
 * {U}
 * Creature — Trilobite
 * 0/3
 *
 * {7}{U}, {T}, Sacrifice this creature: Draw three cards.
 */
val ShoreKeeper = card("Shore Keeper") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Creature — Trilobite"
    oracleText = "{7}{U}, {T}, Sacrifice this creature: Draw three cards."
    power = 0
    toughness = 3

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{7}{U}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.DrawCards(3)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "77"
        artist = "YW Tang"
        flavorText = "Over their long life spans, the larger trilobites accumulate vast treasure troves in their guts."
        imageUri = "https://cards.scryfall.io/normal/front/1/3/135d1145-8640-44e1-8079-45832fa2556d.jpg"
    }
}
