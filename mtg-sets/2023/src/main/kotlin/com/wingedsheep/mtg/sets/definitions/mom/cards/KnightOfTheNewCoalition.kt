package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Knight of the New Coalition
 * {3}{W}
 * Creature — Human Knight
 * 2/2
 * Vigilance
 * When this creature enters, create a 2/2 white and blue Knight creature token with vigilance.
 */
val KnightOfTheNewCoalition = card("Knight of the New Coalition") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Knight"
    oracleText = "Vigilance\n" +
        "When this creature enters, create a 2/2 white and blue Knight creature token with vigilance."
    power = 2
    toughness = 2

    keywords(Keyword.VIGILANCE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 2,
            toughness = 2,
            colors = setOf(Color.WHITE, Color.BLUE),
            creatureTypes = setOf("Knight"),
            keywords = setOf(Keyword.VIGILANCE),
            imageUri = "https://cards.scryfall.io/normal/front/8/8/88439bfc-8942-473b-9e4f-863017788476.jpg?1783916669"
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "25"
        artist = "Jake Murray"
        flavorText = "Raised in a long tradition of heroes, every Benalish knight knows the day " +
            "might come when they will be called to face Phyrexia."
        imageUri = "https://cards.scryfall.io/normal/front/5/6/56a3108b-c33d-47c5-984b-01fa257fbd79.jpg?1783917059"
    }
}
