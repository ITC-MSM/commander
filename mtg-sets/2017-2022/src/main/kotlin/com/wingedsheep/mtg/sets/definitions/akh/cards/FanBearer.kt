package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Fan Bearer
 * {W}
 * Creature — Zombie
 * 1/2
 * {2}, {T}: Tap target creature.
 */
val FanBearer = card("Fan Bearer") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Zombie"
    oracleText = "{2}, {T}: Tap target creature."
    power = 1
    toughness = 2

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.Tap)
        val creature = target("target creature", Targets.Creature)
        effect = Effects.Tap(creature)
        description = "{2}, {T}: Tap target creature."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "12"
        artist = "Anthony Palumbo"
        flavorText = "Rest sometimes requires the right prompting."
        imageUri = "https://cards.scryfall.io/normal/front/f/6/f6cec477-f86f-4ccf-aa57-b6b7ade46eed.jpg?1783936543"
    }
}
