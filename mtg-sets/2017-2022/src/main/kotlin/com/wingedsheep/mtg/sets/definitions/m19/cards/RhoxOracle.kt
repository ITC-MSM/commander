package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Rhox Oracle
 * {4}{G}
 * Creature — Rhino Monk
 * 4/2
 * When this creature enters, draw a card.
 */
val RhoxOracle = card("Rhox Oracle") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Rhino Monk"
    power = 4
    toughness = 2
    oracleText = "When this creature enters, draw a card."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "198"
        artist = "Dan Murayama Scott"
        flavorText = "\"The further into the future I look, the less certain my vision. Even now, the middle distance is obscured by fire.\""
        imageUri = "https://cards.scryfall.io/normal/front/2/8/281f04d5-af45-4494-ac11-a605d3a06643.jpg"
    }
}
