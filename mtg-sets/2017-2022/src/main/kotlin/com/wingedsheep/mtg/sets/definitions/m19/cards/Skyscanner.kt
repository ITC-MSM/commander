package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Skyscanner
 * {3}
 * Artifact Creature — Thopter
 * 1/1
 * Flying
 * When this creature enters, draw a card.
 *
 * A colourless flier with a cantrip ETB — [Triggers.EntersBattlefield] (SELF binding) into
 * [Effects.DrawCards]`(1)`.
 */
val Skyscanner = card("Skyscanner") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Thopter"
    power = 1
    toughness = 1
    oracleText = "Flying\n" +
        "When this creature enters, draw a card."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(1)
        description = "When this creature enters, draw a card."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "245"
        artist = "Adam Paquette"
        flavorText = "The municipal senate makes extensive use of the thopters, mostly to gather dirt on rival senators."
        imageUri = "https://cards.scryfall.io/normal/front/b/d/bd2c1fb7-3c1d-49a9-b2c2-78ba2264df38.jpg"
    }
}
