package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Mesa Cavalier
 * {2}{W}
 * Creature — Human Knight
 * 2/1
 * Flying
 * When this creature enters, you gain 2 life.
 */
val MesaCavalier = card("Mesa Cavalier") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Knight"
    oracleText = "Flying\nWhen this creature enters, you gain 2 life."
    power = 2
    toughness = 1

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "26"
        artist = "David Palumbo"
        flavorText = "In the fight to defeat Phyrexia, the courage of Benalish knights is matched only by that of their pegasus steeds."
        imageUri = "https://cards.scryfall.io/normal/front/f/e/feeec740-7ffc-4f57-b52c-92209da91d69.jpg?1783921362"
    }
}
