package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Cirith Ungol Patrol
 * {4}{B}
 * Creature — Orc Soldier
 * 4/5
 *
 * {1}, {T}, Sacrifice another creature: Draw a card, then create a Food token.
 * (It's an artifact with "{2}, {T}, Sacrifice this token: You gain 3 life.")
 */
val CirithUngolPatrol = card("Cirith Ungol Patrol") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Orc Soldier"
    power = 4
    toughness = 5
    oracleText = "{1}, {T}, Sacrifice another creature: Draw a card, then create a Food token. (It's an artifact with \"{2}, {T}, Sacrifice this token: You gain 3 life.\")"

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap, Costs.SacrificeAnother(GameObjectFilter.Creature))
        effect = Effects.DrawCards(1) then Effects.CreateFood(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "80"
        artist = "Bruno Biazotto"
        flavorText = "\"Spies feared on Stairs. Double vigilance. Patrol to head of Stairs.\"\n—Shagrat's orders"
        imageUri = "https://cards.scryfall.io/normal/front/7/c/7cd03837-37cc-4f6c-8e22-e3b0ff635462.jpg?1686968408"
    }
}
