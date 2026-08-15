package com.wingedsheep.mtg.sets.definitions.otj.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Holy Cow
 * {2}{W}
 * Creature — Ox Angel
 * 2/2
 *
 * Flash
 * Flying
 * When this creature enters, you gain 2 life and scry 1.
 */
val HolyCow = card("Holy Cow") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Ox Angel"
    power = 2
    toughness = 2
    oracleText = "Flash\nFlying\nWhen this creature enters, you gain 2 life and scry 1."

    keywords(Keyword.FLASH, Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(2).then(Patterns.Library.scry(1))
        description = "you gain 2 life and scry 1."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "16"
        artist = "Justyna Dura"
        imageUri = "https://cards.scryfall.io/normal/front/9/0/90de84c9-941b-4056-8501-ce8a948b9643.jpg?1712355286"
    }
}
