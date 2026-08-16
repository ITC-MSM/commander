package com.wingedsheep.mtg.sets.definitions.m13.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Thragtusk
 * {4}{G}
 * Creature — Beast
 * 5/3
 *
 * When this creature enters, you gain 5 life.
 * When this creature leaves the battlefield, create a 3/3 green Beast creature token.
 *
 * The second trigger is a *leaves-the-battlefield* trigger, not a dies trigger: exiling or
 * bouncing Thragtusk still pays out the Beast.
 */
val Thragtusk = card("Thragtusk") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beast"
    power = 5
    toughness = 3
    oracleText = "When this creature enters, you gain 5 life.\n" +
        "When this creature leaves the battlefield, create a 3/3 green Beast creature token."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(5)
    }

    triggeredAbility {
        trigger = Triggers.LeavesBattlefield
        effect = Effects.CreateToken(
            power = 3,
            toughness = 3,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Beast")
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "193"
        artist = "Nils Hamm"
        flavorText = "\"Always carry two spears.\"\n—Mokgar, Kalonian hunter"
        imageUri = "https://cards.scryfall.io/normal/front/2/8/28667c8b-d02c-4e57-a050-1549207b65d1.jpg"
    }
}
