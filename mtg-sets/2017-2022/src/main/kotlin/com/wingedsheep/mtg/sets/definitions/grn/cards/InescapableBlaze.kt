package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Inescapable Blaze
 * {4}{R}{R}
 * Instant
 * This spell can't be countered.
 * Inescapable Blaze deals 6 damage to any target.
 */
val InescapableBlaze = card("Inescapable Blaze") {
    manaCost = "{4}{R}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "This spell can't be countered.\n" +
        "Inescapable Blaze deals 6 damage to any target."

    cantBeCountered = true
    spell {
        val any = target("target", Targets.Any)
        effect = Effects.DealDamage(6, any)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "107"
        artist = "Steve Argyle"
        flavorText = "\"The Izzet are blamed for every little disaster, which is unfair because we only cause most of them.\"\n—Mizzix of the Izmagnus"
        imageUri = "https://cards.scryfall.io/normal/front/4/6/46651efd-0906-4350-a1b8-52e3f8aff45d.jpg?1783934160"
    }
}
