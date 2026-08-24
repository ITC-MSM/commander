package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Research Assistant
 * {1}{U}
 * Creature — Human Wizard
 * 1/3
 * {3}{U}, {T}: Draw a card, then discard a card.
 */
val ResearchAssistant = card("Research Assistant") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    power = 1
    toughness = 3
    oracleText = "{3}{U}, {T}: Draw a card, then discard a card."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}{U}"), Costs.Tap)
        effect = Patterns.Hand.loot()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "77"
        artist = "Svetlin Velinov"
        flavorText = "There are many words and phrases that can cause an experienced wizard to tremble in fear. Chief among them is \"oops.\""
        imageUri = "https://cards.scryfall.io/normal/front/4/a/4ac8a7c0-d935-4a60-ac32-dde73f5c75da.jpg?1783939188"
    }
}
