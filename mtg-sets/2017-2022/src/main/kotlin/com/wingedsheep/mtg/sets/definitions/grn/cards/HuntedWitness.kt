package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Hunted Witness
 * {W}
 * Creature — Human
 * 1/1
 * When this creature dies, create a 1/1 white Soldier creature token with lifelink.
 */
val HuntedWitness = card("Hunted Witness") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human"
    oracleText = "When this creature dies, create a 1/1 white Soldier creature token with lifelink."
    power = 1
    toughness = 1

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Soldier"),
            keywords = setOf(Keyword.LIFELINK)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "15"
        artist = "David Palumbo"
        flavorText = "He ferried weapons, spells, exotic animals—but his most dangerous cargo was the truth."
        imageUri = "https://cards.scryfall.io/normal/front/8/c/8c31b8e5-2349-4119-9dc2-3e41c5364a78.jpg?1783934200"
    }
}
