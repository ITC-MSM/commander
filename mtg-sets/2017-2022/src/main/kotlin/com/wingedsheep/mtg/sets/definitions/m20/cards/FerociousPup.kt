package com.wingedsheep.mtg.sets.definitions.m20.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ferocious Pup
 * {2}{G}
 * Creature — Wolf
 * 0/1
 * When this creature enters, create a 2/2 green Wolf creature token.
 */
val FerociousPup = card("Ferocious Pup") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Wolf"
    power = 0
    toughness = 1
    oracleText = "When this creature enters, create a 2/2 green Wolf creature token."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 2,
            toughness = 2,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Wolf"),
            imageUri = "https://cards.scryfall.io/normal/front/d/5/d5f1e139-3054-4273-8a4d-faaaa9c383a8.jpg?1783924694",
        )
        description = "When this creature enters, create a 2/2 green Wolf creature token."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "171"
        artist = "Rudy Siswanto"
        flavorText = "The strongest pack has the fiercest pups."
        imageUri = "https://cards.scryfall.io/normal/front/2/3/2354cb24-5c70-4aaa-8636-46866f0950c1.jpg?1783932968"
    }
}
