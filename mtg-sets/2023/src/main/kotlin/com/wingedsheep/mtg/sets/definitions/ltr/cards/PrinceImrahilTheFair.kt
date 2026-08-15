package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Prince Imrahil the Fair
 * {W}{U}
 * Legendary Creature — Human Noble
 * 2/2
 *
 * Whenever you draw your second card each turn, create a 1/1 white Human Soldier creature token.
 */
val PrinceImrahilTheFair = card("Prince Imrahil the Fair") {
    manaCost = "{W}{U}"
    colorIdentity = "WU"
    typeLine = "Legendary Creature — Human Noble"
    power = 2
    toughness = 2
    oracleText = "Whenever you draw your second card each turn, create a 1/1 white Human Soldier creature token."

    triggeredAbility {
        trigger = Triggers.NthCardDrawn(2)
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Human", "Soldier"),
            imageUri = "https://cards.scryfall.io/normal/front/a/6/a6181330-7521-4ec6-be6c-b35487c2d2d4.jpg?1699974464"
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "219"
        artist = "Justyna Dura"
        flavorText = "\"The Lord Aragorn I hold to be my liege lord, whether he claim it or no. His wish is to me a command.\""
        imageUri = "https://cards.scryfall.io/normal/front/7/d/7d486def-7c3f-41e9-bb28-4582450a7b9e.jpg?1686969940"
    }
}
