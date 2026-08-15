package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Protector of Gondor
 * {3}{W}
 * Creature — Human Soldier
 * 3/3
 *
 * When this creature enters, create a 1/1 white Human Soldier creature token.
 */
val ProtectorOfGondor = card("Protector of Gondor") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    power = 3
    toughness = 3
    oracleText = "When this creature enters, create a 1/1 white Human Soldier creature token."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Human", "Soldier"),
            imageUri = "https://cards.scryfall.io/normal/front/a/6/a6181330-7521-4ec6-be6c-b35487c2d2d4.jpg?1699974464"
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "25"
        artist = "Ilker Yildiz"
        flavorText = "Orcs were digging deep trenches in a huge ring, just out of bowshot of the walls, while the Men of Minas Tirith looked on, unable to hinder them."
        imageUri = "https://cards.scryfall.io/normal/front/8/5/85708748-40ca-4066-a287-7a6a189ff3df.jpg?1686967875"
    }
}
