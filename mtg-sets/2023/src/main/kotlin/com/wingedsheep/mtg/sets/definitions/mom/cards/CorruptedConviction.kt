package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Corrupted Conviction
 * {B}
 * Instant
 *
 * As an additional cost to cast this spell, sacrifice a creature.
 * Draw two cards.
 *
 * Canonical definition lives in March of the Machine (earliest real printing).
 * Reprinted in Outlaws of Thunder Junction — see OTJ `CorruptedConvictionReprint`.
 */
val CorruptedConviction = card("Corrupted Conviction") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "As an additional cost to cast this spell, sacrifice a creature.\nDraw two cards."

    additionalCost(Costs.additional.SacrificePermanent(GameObjectFilter.Creature))

    spell {
        effect = Effects.DrawCards(2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "98"
        artist = "Joseph Weston"
        flavorText = "\"For the good of all, those who stand against the harmony of Phyrexia must be cut down.\"\n—Ajani Goldmane"
        imageUri = "https://cards.scryfall.io/normal/front/c/e/ce133ad5-8748-4a3d-ae8c-7b2a5938927d.jpg?1682203658"
    }
}
