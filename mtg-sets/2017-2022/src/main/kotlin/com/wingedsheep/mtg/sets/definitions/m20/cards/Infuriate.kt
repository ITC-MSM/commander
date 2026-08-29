package com.wingedsheep.mtg.sets.definitions.m20.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Infuriate
 * {R}
 * Instant
 *
 * Target creature gets +3/+2 until end of turn.
 *
 * Canonical printing lives in Core Set 2020 (the card's earliest real printing); later sets —
 * including Theros Beyond Death — contribute only a [com.wingedsheep.sdk.model.Printing] row.
 * "Until end of turn" is [Effects.ModifyStats]' default duration, so it is left unwritten.
 */
val Infuriate = card("Infuriate") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Target creature gets +3/+2 until end of turn."

    spell {
        val creature = target("target", Targets.Creature)
        effect = Effects.ModifyStats(3, 2, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "145"
        artist = "Caio Monteiro"
        flavorText = "\"No shirt, no shoes, no service.\"\n" +
            "—Marketplace sign"
        imageUri = "https://cards.scryfall.io/normal/front/7/a/7ac778f6-8997-47ee-a676-3eb9f8d1592f.jpg"
    }
}
