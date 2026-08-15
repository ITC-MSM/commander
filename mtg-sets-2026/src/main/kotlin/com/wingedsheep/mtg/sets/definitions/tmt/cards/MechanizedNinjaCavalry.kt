package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect

/**
 * Mechanized Ninja Cavalry
 * {1}{R/W}
 * Artifact Creature — Robot Ninja
 * 1/1
 *
 * When this creature enters, create a 1/1 colorless Robot artifact
 * creature token.
 */
val MechanizedNinjaCavalry = card("Mechanized Ninja Cavalry") {
    manaCost = "{1}{R/W}"
    colorIdentity = "RW"
    typeLine = "Artifact Creature — Robot Ninja"
    oracleText = "When this creature enters, create a 1/1 colorless Robot artifact creature token."
    power = 1
    toughness = 1

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = CreateTokenEffect(
            power = 1,
            toughness = 1,
            colors = setOf(),
            creatureTypes = setOf("Robot"),
            artifactToken = true,
            imageUri = "https://cards.scryfall.io/normal/front/0/8/08497fc5-1c0e-4c3c-a356-bf4b34bd4c45.jpg?1771590585"
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "156"
        artist = "Michele Giorgi"
        flavorText = "\"This is absolutely ridiculous, Saki! They aren't meant for riding! What happened to the ninja's famous subtlety?\"\n—Baxter Stockman"
        imageUri = "https://cards.scryfall.io/normal/front/2/c/2cb65388-dc6c-4e2a-93ac-49ea484849e9.jpg?1771502774"
    }
}
