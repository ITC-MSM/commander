package com.wingedsheep.mtg.sets.definitions.bro.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Third Path Savant
 * {2}{U}
 * Creature — Human Wizard
 * 2/3
 * {7}: Draw two cards.
 */
val ThirdPathSavant = card("Third Path Savant") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    power = 2
    toughness = 3
    oracleText = "{7}: Draw two cards."

    activatedAbility {
        cost = Costs.Mana("{7}")
        effect = Effects.DrawCards(2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "67"
        artist = "Artur Treffner"
        flavorText = "As Mishra's army bore down on Terisia City, Corlo felt his focus, his patience, and his willpower twine together. With a deep breath, he plucked answers from the air."
        imageUri = "https://cards.scryfall.io/normal/front/7/9/793a51ab-59fb-424f-a315-3f63e8990322.jpg?1783920104"
    }
}
