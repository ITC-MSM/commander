package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Goblin Picker
 * {1}{R}
 * Creature — Goblin
 * 2/2
 * {R}, {T}, Discard a card: Draw a card.
 */
val GoblinPicker = card("Goblin Picker") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin"
    oracleText = "{R}, {T}, Discard a card: Draw a card."
    power = 2
    toughness = 2

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{R}"), Costs.Tap, Costs.DiscardCard)
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "128"
        artist = "Vladimir Krisetskiy"
        flavorText = "Countless generations of Shivan goblins have worked on the Mana Rig, giving them an unparalleled eye for useful relics."
        imageUri = "https://cards.scryfall.io/normal/front/6/d/6d8f1f06-dde5-41f2-923c-67d1d4d13fab.jpg?1783921317"
    }
}
