package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Martyr of Dusk
 * {1}{W}
 * Creature — Vampire Soldier
 * 2/1
 * When this creature dies, create a 1/1 white Vampire creature token with lifelink.
 */
val MartyrOfDusk = card("Martyr of Dusk") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Vampire Soldier"
    oracleText = "When this creature dies, create a 1/1 white Vampire creature token with lifelink."
    power = 2
    toughness = 1

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Vampire"),
            keywords = setOf(Keyword.LIFELINK),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "14"
        artist = "Greg Staples"
        flavorText = "\"Should I fall, take up our standard and carry on. The Legion must always prevail.\""
        imageUri = "https://cards.scryfall.io/normal/front/0/4/04134be2-5e40-4732-832c-f616009eceff.jpg?1783935337"
    }
}
