package com.wingedsheep.mtg.sets.definitions.m20.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Savannah Sage
 * {1}{W}
 * Creature — Cat Cleric
 * 2/2
 * When this creature enters, you gain 2 life.
 */
val SavannahSage = card("Savannah Sage") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Cat Cleric"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, you gain 2 life."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(2)
        description = "When this creature enters, you gain 2 life."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "284"
        artist = "Bayard Wu"
        flavorText = "\"Now is not the time for your light to fade.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/b/5b5fa4bb-e061-456f-808e-8d98b2c8abf5.jpg?1783932921"
    }
}
