package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Coeurl
 * {1}{W}
 * Creature — Cat Beast
 * 2/2
 *
 * {1}{W}, {T}: Tap target nonenchantment creature.
 *
 * The "nonenchantment creature" filter composes the existing creature filter with the
 * [GameObjectFilter.Nonenchantment] predicate via the public `and` combiner — no new SDK type.
 */
val Coeurl = card("Coeurl") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Cat Beast"
    power = 2
    toughness = 2
    oracleText = "{1}{W}, {T}: Tap target nonenchantment creature."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{W}"), Costs.Tap)
        val t = target(
            "target",
            TargetPermanent(filter = TargetFilter(GameObjectFilter.Creature and GameObjectFilter.Nonenchantment)),
        )
        effect = Effects.Tap(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "12"
        artist = "Miho Midorikawa"
        flavorText = "A lean and limber predator whose whiskers acquire charge from naturally-occurring " +
            "electric particles in the atmosphere."
        imageUri = "https://cards.scryfall.io/normal/front/7/6/7604b534-5480-42fa-bc36-bbae730f8582.jpg?1748705798"
    }
}
