package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Matca Rioters
 * {2}{G}
 * Creature — Human Warrior
 * * / *
 * Domain — Matca Rioters's power and toughness are each equal to the number of basic land types among lands you control.
 *
 * Domain is an ability word (CR 207.2c) with no rules meaning of its own; the count is
 * [DynamicAmounts.domain] — the distinct basic land subtypes among the lands you control. Both
 * halves read the same source, which is exactly what [dynamicStats] composes, so no literal
 * `power` / `toughness` line is emitted.
 */
val MatcaRioters = card("Matca Rioters") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Warrior"
    oracleText = "Domain — Matca Rioters's power and toughness are each equal to the number of basic land types among lands you control."

    dynamicStats(DynamicAmounts.domain())

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "84"
        artist = "Steve Argyle"
        flavorText = "When outsiders interrupted the matca championship, things got ugly."
        imageUri = "https://cards.scryfall.io/normal/front/b/3/b32dcf7d-ac19-45c5-8c3a-1155bf25b216.jpg"
    }
}
