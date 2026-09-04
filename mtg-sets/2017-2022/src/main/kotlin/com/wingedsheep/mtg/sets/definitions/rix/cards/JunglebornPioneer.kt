package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Jungleborn Pioneer
 * {2}{G}
 * Creature — Merfolk Scout
 * 2/2
 * When this creature enters, create a 1/1 blue Merfolk creature token with hexproof.
 */
val JunglebornPioneer = card("Jungleborn Pioneer") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Merfolk Scout"
    oracleText = "When this creature enters, create a 1/1 blue Merfolk creature token with " +
        "hexproof. (It can't be the target of spells or abilities your opponents control.)"
    power = 2
    toughness = 2

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.BLUE),
            creatureTypes = setOf("Merfolk"),
            keywords = setOf(Keyword.HEXPROOF),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "137"
        artist = "Scott Murphy"
        flavorText = "\"We fought so long to hide this place. Let us be first to learn its wonders!\""
        imageUri = "https://cards.scryfall.io/normal/front/9/f/9f01ae0d-db1e-4912-b8ad-3069f6938e04.jpg?1783935285"
    }
}
