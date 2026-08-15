package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect

/**
 * Bear's Companion
 * {2}{G}{U}{R}
 * Creature — Human Warrior
 * 2/2
 * When this creature enters, create a 4/4 green Bear creature token.
 */
val BearsCompanion = card("Bear's Companion") {
    manaCost = "{2}{G}{U}{R}"
    colorIdentity = "URG"
    typeLine = "Creature — Human Warrior"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, create a 4/4 green Bear creature token."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = CreateTokenEffect(
            count = 1,
            power = 4,
            toughness = 4,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Bear"),
            imageUri = "https://cards.scryfall.io/normal/front/8/1/8169a2e1-ed39-41cf-9a36-3c2b59499819.jpg?1675455039"
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "167"
        artist = "Winona Nelson"
        flavorText = "The Sultai came hunting for a bear hide. Now I have a belt of naga skin, and my friend has a full belly."
        imageUri = "https://cards.scryfall.io/normal/front/d/6/d6912c5b-6aff-4506-96ca-acb5ce660322.jpg?1562794223"
    }
}
