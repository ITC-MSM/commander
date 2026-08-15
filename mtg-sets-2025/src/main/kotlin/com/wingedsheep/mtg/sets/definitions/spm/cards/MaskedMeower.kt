package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.DrawCardsEffect

/**
 * Masked Meower
 * {R}
 * Creature — Spider Cat Hero
 * 1/1
 * Haste
 * Discard a card, Sacrifice this creature: Draw a card.
 */
val MaskedMeower = card("Masked Meower") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Creature — Spider Cat Hero"
    oracleText = "Haste\nDiscard a card, Sacrifice this creature: Draw a card."
    power = 1
    toughness = 1
    keywords(Keyword.HASTE)
    activatedAbility {
        cost = Costs.Composite(Costs.DiscardCard, Costs.SacrificeSelf)
        effect = DrawCardsEffect(1)
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "82"
        artist = "Narendra Bintara Adi"
        flavorText = "Neither criminals nor veterinarians had much luck pinning down the uncatchable Spider-Cat."
        imageUri = "https://cards.scryfall.io/normal/front/6/a/6aa0dc1f-6c83-4ac9-b4f2-428e0e0bbf88.jpg?1757377294"
    }
}
