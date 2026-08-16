package com.wingedsheep.mtg.sets.definitions.m21.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Valorous Steed
 * {4}{W}
 * Creature — Unicorn
 * 3/3
 * Vigilance (Attacking doesn't cause this creature to tap.)
 * When this creature enters, create a 2/2 white Knight creature token with vigilance.
 */
val ValorousSteed = card("Valorous Steed") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Unicorn"
    power = 3
    toughness = 3
    oracleText = "Vigilance (Attacking doesn't cause this creature to tap.)\nWhen this creature enters, create a 2/2 white Knight creature token with vigilance."

    keywords(Keyword.VIGILANCE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 2,
            toughness = 2,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Knight"),
            keywords = setOf(Keyword.VIGILANCE)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "42"
        artist = "Donato Giancola"
        flavorText = "A unicorn chooses only the most virtuous and noble of knights to be its companion."
        imageUri = "https://cards.scryfall.io/normal/front/a/a/aa01cb8c-f080-456b-a91a-f1d7943a70b2.jpg?1783930732"
    }
}
