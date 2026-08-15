package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.dsl.DynamicAmounts

/**
 * Kheru Dreadmaw
 * {4}{B}
 * Creature — Zombie Crocodile
 * 4/4
 * Defender
 * {1}{G}, Sacrifice another creature: You gain life equal to the sacrificed creature's toughness.
 */
val KheruDreadmaw = card("Kheru Dreadmaw") {
    manaCost = "{4}{B}"
    colorIdentity = "BG"
    typeLine = "Creature — Zombie Crocodile"
    power = 4
    toughness = 4
    oracleText = "Defender\n{1}{G}, Sacrifice another creature: You gain life equal to the sacrificed creature's toughness."

    keywords(Keyword.DEFENDER)

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{1}{G}"),
            Costs.SacrificeAnother(GameObjectFilter.Creature)
        )
        effect = Effects.GainLife(DynamicAmounts.sacrificedToughness())
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "76"
        artist = "Ryan Yee"
        flavorText = "Its hunting instincts have long since rotted away. Its hunger, however, remains."
        imageUri = "https://cards.scryfall.io/normal/front/e/8/e8b10468-18b8-4321-a791-0cbd18ea9c4d.jpg?1562795323"
    }
}
