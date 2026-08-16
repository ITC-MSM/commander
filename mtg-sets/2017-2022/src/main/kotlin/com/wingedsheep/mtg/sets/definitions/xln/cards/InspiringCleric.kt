package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Inspiring Cleric
 * {2}{W}
 * Creature — Vampire Cleric
 * 3/2
 * When this creature enters, you gain 4 life.
 */
val InspiringCleric = card("Inspiring Cleric") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Vampire Cleric"
    power = 3
    toughness = 2
    oracleText = "When this creature enters, you gain 4 life."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(4)
        description = "When this creature enters, you gain 4 life."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "16"
        artist = "Randy Gallegos"
        flavorText = "\"The Immortal Sun will bring us true eternal life to replace the everlasting shadow of undeath.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/1/31b8f1da-c8ea-41d5-b1ad-b714c22d3683.jpg?1783935801"
    }
}
