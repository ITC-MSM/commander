package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Akki Scrapchomper
 * {R}
 * Creature — Phyrexian Goblin
 * 1/1
 * {1}{R}, {T}, Sacrifice an artifact or land: Draw a card.
 */
val AkkiScrapchomper = card("Akki Scrapchomper") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Creature — Phyrexian Goblin"
    oracleText = "{1}{R}, {T}, Sacrifice an artifact or land: Draw a card."
    power = 1
    toughness = 1

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{1}{R}"),
            Costs.Tap,
            Costs.Sacrifice(GameObjectFilter.ArtifactOrLand)
        )
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "130"
        artist = "Wisnu Tan"
        flavorText = "The Furnace Host found great value in the chaotic ingenuity of Kamigawa's " +
            "akki and allowed willing converts to \"experiment\" however they saw fit."
        imageUri = "https://cards.scryfall.io/normal/front/b/0/b0e02737-0193-46e4-a506-cec86a44dc99.jpg?1783916998"
    }
}
