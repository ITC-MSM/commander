package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Crested Herdcaller
 * {3}{G}{G}
 * Creature — Dinosaur
 * 3/3
 * Trample
 * When this creature enters, create a 3/3 green Dinosaur creature token with trample.
 */
val CrestedHerdcaller = card("Crested Herdcaller") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Dinosaur"
    oracleText = "Trample\n" +
        "When this creature enters, create a 3/3 green Dinosaur creature token with trample."
    power = 3
    toughness = 3

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 3,
            toughness = 3,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Dinosaur"),
            keywords = setOf(Keyword.TRAMPLE),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "126"
        artist = "Lucas Graciano"
        flavorText = "\"Our survival, like theirs, depends on our ability to work together.\"\n" +
            "—Huatli, to Tishana"
        imageUri = "https://cards.scryfall.io/normal/front/8/0/80bccca0-6425-4676-a98a-e0721a6beff7.jpg?1783935290"
    }
}
