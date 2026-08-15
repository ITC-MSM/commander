package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Moriok Reaver
 * {2}{B}
 * Creature — Human Warrior
 * 3/2
 *
 * Vanilla — no rules text.
 */
val MoriokReaver = card("Moriok Reaver") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Warrior"
    power = 3
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "70"
        artist = "Marc Simonetti"
        flavorText = "\"The Moriok are fools. They try so hard to gain my favor. All I want is for them to die quickly, to join the ranks of the nim.\"\n—Geth, Lord of the Vault"
        imageUri = "https://cards.scryfall.io/normal/front/e/2/e2a0410f-95c5-49bf-856d-dea796c96e3b.jpg?1783941730"
    }
}
