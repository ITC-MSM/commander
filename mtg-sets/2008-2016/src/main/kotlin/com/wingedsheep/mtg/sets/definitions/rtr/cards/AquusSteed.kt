package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Aquus Steed
 * {3}{U}
 * Creature — Beast
 * 1/3
 *
 * {2}{U}, {T}: Target creature gets -2/-0 until end of turn.
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * A mana-plus-tap [Costs.Composite] over a plain [Effects.ModifyStats]. "-2/-0" is a negative
 * power modifier and a zero toughness one, defaulting to end of turn.
 */
val AquusSteed = card("Aquus Steed") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Beast"
    oracleText = "{2}{U}, {T}: Target creature gets -2/-0 until end of turn."
    power = 1
    toughness = 3

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{U}"), Costs.Tap)
        val t = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(-2, 0, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "29"
        artist = "Warren Mahy"
        flavorText = "In water, it's as graceful as a dolphin. On land, it darts and jerks so unpredictably that few can ride it for long."
        imageUri = "https://cards.scryfall.io/normal/front/a/f/af643949-7a9b-4195-8ab8-d43b1928b85a.jpg?1783940371"
    }
}
