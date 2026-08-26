package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Cascading Cataracts
 *
 * Land
 * Indestructible
 * {T}: Add {C}.
 * {5}, {T}: Add five mana in any combination of colors.
 */
val CascadingCataracts = card("Cascading Cataracts") {
    typeLine = "Land"
    oracleText = "Indestructible\n" +
        "{T}: Add {C}.\n" +
        "{5}, {T}: Add five mana in any combination of colors."

    keywords(Keyword.INDESTRUCTIBLE)

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{5}"), Costs.Tap)
        effect = Effects.AddManaInAnyCombination(5)
        manaAbility = true
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "240"
        artist = "Noah Bradley"
        flavorText = "\"The power that flows here cannot be denied. But where is the source?\"\n—Nissa Revane"
        imageUri = "https://cards.scryfall.io/normal/front/7/7/778739db-4431-4e58-91de-d2619aeef3ce.jpg?1783936447"
    }
}
