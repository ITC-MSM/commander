package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Angel of Mercy
 * {4}{W}
 * Creature — Angel
 * 3/3
 * Flying
 * When this creature enters, you gain 3 life.
 *
 * Portal Second Age is the card's earliest real-expansion printing, so the canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives here. Later sets (Invasion, etc.)
 * contribute reprint [com.wingedsheep.sdk.model.Printing] rows.
 */
val AngelOfMercy = card("Angel of Mercy") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Angel"
    power = 3
    toughness = 3
    oracleText = "Flying\nWhen this creature enters, you gain 3 life."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(3)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "8"
        artist = "Melissa A. Benson"
        imageUri = "https://cards.scryfall.io/normal/front/d/a/dac5c913-4eb5-4cfb-9c24-223f14f07064.jpg?1562947099"
    }
}
