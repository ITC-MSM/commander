package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Basilica Bell-Haunt — Ravnica Allegiance #156
 * {W}{W}{B}{B} · Creature — Spirit · 3 / 4
 *
 * [Patterns.Hand] `eachOpponentDiscards` is the per-opponent gather → select → discard pipeline;
 * each opponent chooses their own card. The life gain is flat, not per opponent.
 */
val BasilicaBellHaunt = card("Basilica Bell-Haunt") {
    manaCost = "{W}{W}{B}{B}"
    colorIdentity = "BW"
    typeLine = "Creature — Spirit"
    power = 3
    toughness = 4
    oracleText = "When this creature enters, each opponent discards a card and you gain 3 life."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Composite(listOf(
            Patterns.Hand.eachOpponentDiscards(1),
            Effects.GainLife(3)
        ))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "156"
        artist = "Yeong-Hao Han"
        flavorText = "You can hear their tolling only when your debt is due."
        imageUri = "https://cards.scryfall.io/normal/front/e/7/e72f4329-db6f-4284-b63e-55f22a0a0f6e.jpg"
    }
}
