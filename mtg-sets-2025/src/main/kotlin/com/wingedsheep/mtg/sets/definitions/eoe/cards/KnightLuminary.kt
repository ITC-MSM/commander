package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Knight Luminary
 * {3}{W}
 * Creature — Human Knight
 * When this creature enters, create a 1/1 white Human Soldier creature token.
 * Warp {1}{W} (You may cast this card from your hand for its warp cost. Exile this creature at the beginning of the next end step, then you may cast it from exile on a later turn.)
 * 3/2
 */
val KnightLuminary = card("Knight Luminary") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Knight"
    oracleText = "When this creature enters, create a 1/1 white Human Soldier creature token.\n" +
        "Warp {1}{W} (You may cast this card from your hand for its warp cost. Exile this creature at the beginning of the next end step, then you may cast it from exile on a later turn.)"
    power = 3
    toughness = 2

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Human", "Soldier"),
            count = 1,
            imageUri = "https://cards.scryfall.io/normal/front/6/3/631c2c16-132d-4607-ab7e-207a6af188e5.jpg?1757686920"
        )
    }

    warp = "{1}{W}"

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "23"
        artist = "Aaron Miller"
        flavorText = "\"I thank you that I do not shine alone.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/4/34334971-c1b7-4506-a6dd-77f66b3ae4e7.jpg?1752946643"
    }
}
