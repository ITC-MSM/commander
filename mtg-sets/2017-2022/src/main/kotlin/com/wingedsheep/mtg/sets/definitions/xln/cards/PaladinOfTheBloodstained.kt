package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Paladin of the Bloodstained
 * {3}{W}
 * Creature — Vampire Knight
 * 3/2
 *
 * When this creature enters, create a 1/1 white Vampire creature token with lifelink.
 */
val PaladinOfTheBloodstained = card("Paladin of the Bloodstained") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Vampire Knight"
    oracleText = "When this creature enters, create a 1/1 white Vampire creature token with lifelink."
    power = 3
    toughness = 2

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
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
        collectorNumber = "25"
        artist = "Bastien L. Deharme"
        flavorText = "Closely linked to the Church of Dusk, the paladins of the Bloodstained order are devout to the point of fanaticism."
        imageUri = "https://cards.scryfall.io/normal/front/5/a/5a0385d5-d0f4-40b8-af28-6557ffdfb625.jpg"
    }
}
