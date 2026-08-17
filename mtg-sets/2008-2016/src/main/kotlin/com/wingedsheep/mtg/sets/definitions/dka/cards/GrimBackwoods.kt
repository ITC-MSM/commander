package com.wingedsheep.mtg.sets.definitions.dka.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Grim Backwoods
 * Land
 * {T}: Add {C}.
 * {2}{B}{G}, {T}, Sacrifice a creature: Draw a card.
 */
val GrimBackwoods = card("Grim Backwoods") {
    typeLine = "Land"
    colorIdentity = "BG"
    oracleText = "{T}: Add {C}.\n{2}{B}{G}, {T}, Sacrifice a creature: Draw a card."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{2}{B}{G}"),
            Costs.Tap,
            Costs.Sacrifice(GameObjectFilter.Creature)
        )
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "156"
        artist = "Vincent Proce"
        flavorText = "\"I love what they've done with the place.\"\n—Liliana Vess"
        imageUri = "https://cards.scryfall.io/normal/front/0/4/045abeeb-f5e5-4f3f-9836-5b1553e03f11.jpg?1783940790"
    }
}
