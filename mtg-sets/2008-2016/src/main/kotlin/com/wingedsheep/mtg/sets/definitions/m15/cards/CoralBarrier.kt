package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Coral Barrier
 * {2}{U}
 * Creature — Wall
 * 1/3
 * Defender
 * When this creature enters, create a 1/1 blue Squid creature token with islandwalk.
 */
val CoralBarrier = card("Coral Barrier") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Wall"
    power = 1
    toughness = 3
    oracleText =
        "Defender (This creature can't attack.)\n" +
        "When this creature enters, create a 1/1 blue Squid creature token with islandwalk. (It can't be blocked as long as defending player controls an Island.)"

    keywords(Keyword.DEFENDER)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.BLUE),
            creatureTypes = setOf("Squid"),
            keywords = setOf(Keyword.ISLANDWALK),
        )
        description = "When this creature enters, create a 1/1 blue Squid creature token with islandwalk."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "49"
        artist = "Florian de Gesincourt"
        imageUri = "https://cards.scryfall.io/normal/front/f/3/f34cd305-d823-4996-9f8f-806386491f5d.jpg?1783939194"
    }
}
