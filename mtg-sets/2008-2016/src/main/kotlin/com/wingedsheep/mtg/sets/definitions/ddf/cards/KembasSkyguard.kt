package com.wingedsheep.mtg.sets.definitions.ddf.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Kemba's Skyguard
 * {1}{W}{W}
 * Creature — Cat Knight
 * 2 / 2
 *
 * Flying
 * When this creature enters, you gain 2 life.
 */
val KembasSkyguard = card("Kemba's Skyguard") {
    manaCost = "{1}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Cat Knight"
    oracleText = "Flying\n" +
        "When this creature enters, you gain 2 life."
    power = 2
    toughness = 2

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(2)
        description = "When this creature enters, you gain 2 life."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "13"
        artist = "Whit Brachna"
        flavorText = "\"We're now to dispense aid to any Mirran we see battling anything . . . 'strange.' " +
            "Regent's orders.\"\n—Ranya, skyhunter captain"
        imageUri = "https://cards.scryfall.io/normal/front/6/6/66ce7c11-09bf-4884-893c-fc8bdbe776d4.jpg?1783941769"
    }
}
