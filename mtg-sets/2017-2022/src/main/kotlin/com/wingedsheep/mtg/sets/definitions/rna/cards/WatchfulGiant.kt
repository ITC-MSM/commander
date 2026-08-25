package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Watchful Giant — Ravnica Allegiance #30
 * {5}{W} · Creature — Giant Soldier · 3 / 6
 *
 * An enters-trigger token maker. Token art resolves through `TokenArtData` from the set code,
 * so no `imageUri` is authored on the [Effects.CreateToken] call.
 */
val WatchfulGiant = card("Watchful Giant") {
    manaCost = "{5}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Giant Soldier"
    power = 3
    toughness = 6
    oracleText = "When this creature enters, create a 1/1 white Human creature token."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Human")
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "30"
        artist = "Grzegorz Rutkowski"
        flavorText = "Loitering is not only illegal but unwise, since those who stay too long in one place are apt to be stepped on."
        imageUri = "https://cards.scryfall.io/normal/front/6/1/61a38f24-1eb3-4914-be1f-0b5f6d4b09d5.jpg"
    }
}
