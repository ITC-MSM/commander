package com.wingedsheep.mtg.sets.definitions.m20.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Dawning Angel
 * {4}{W}
 * Creature — Angel
 * 3/2
 * Flying
 * When this creature enters, you gain 4 life.
 */
val DawningAngel = card("Dawning Angel") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Angel"
    power = 3
    toughness = 2
    oracleText = "Flying\nWhen this creature enters, you gain 4 life."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(4)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "11"
        artist = "Yongjae Choi"
        flavorText = "\"As the sun rose behind the Bone Spire, an angel appeared over the charnel fields, bringing a surge of new hope.\" —Krinnea, *Siege of the Bone Spire*"
        imageUri = "https://cards.scryfall.io/normal/front/4/f/4f3d90ef-6f70-4897-85c1-4e1beeb33363.jpg?1783933032"
    }
}
