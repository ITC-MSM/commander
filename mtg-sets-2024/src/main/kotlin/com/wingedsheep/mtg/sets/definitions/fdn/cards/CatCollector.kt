package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Cat Collector
 * {2}{W}
 * Creature — Human Citizen
 * 3/2
 * When this creature enters, create a Food token. (It's an artifact with
 * "{2}, {T}, Sacrifice this token: You gain 3 life.")
 * Whenever you gain life for the first time during each of your turns, create a
 * 1/1 white Cat creature token.
 */
val CatCollector = card("Cat Collector") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Citizen"
    oracleText = "When this creature enters, create a Food token. (It's an artifact with \"{2}, {T}, Sacrifice this token: You gain 3 life.\")\nWhenever you gain life for the first time during each of your turns, create a 1/1 white Cat creature token."
    power = 3
    toughness = 2

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateFood()
    }

    triggeredAbility {
        trigger = Triggers.YouGainLifeFirstTimeEachTurn
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Cat"),
            imageUri = "https://cards.scryfall.io/normal/front/2/8/2885d54c-9fb2-4f01-8937-54f8ac1ce5bc.jpg"
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "4"
        artist = "Chris Seaman"
        flavorText = "\"Why so many? Because they need a home, just like I once did.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/2/526fe356-bff1-4211-9e88-bf913ac76b1d.jpg"
    }
}
