package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Gibbering Barricade
 * {2}{B}
 * Creature — Nightmare Wall
 * 2/4
 * Defender
 * {2}{B}, Sacrifice a creature: You gain 1 life and draw a card.
 */
val GibberingBarricade = card("Gibbering Barricade") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Nightmare Wall"
    oracleText = "Defender\n{2}{B}, Sacrifice a creature: You gain 1 life and draw a card."
    power = 2
    toughness = 4

    keywords(Keyword.DEFENDER)

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{B}"), Costs.Sacrifice(GameObjectFilter.Creature))
        effect = Effects.Composite(
            Effects.GainLife(1),
            Effects.DrawCards(1)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "95"
        artist = "Drew Tucker"
        flavorText = "\"Who needs stone walls when you can hide behind an impenetrable barrier of screaming nightmares?\"\n—Braids"
        imageUri = "https://cards.scryfall.io/normal/front/0/6/0674b340-420d-4864-92fe-7b268fe0874c.jpg?1783921331"
    }
}
