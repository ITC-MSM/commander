package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Void Rend
 * {W}{U}{B}
 * Instant
 * This spell can't be countered.
 * Destroy target nonland permanent.
 *
 * "This spell can't be countered" is the card-level [cantBeCountered] flag (the Long Goodbye
 * idiom) — it stamps `CantBeCounteredComponent` on the spell, which every counter path checks.
 */
val VoidRend = card("Void Rend") {
    manaCost = "{W}{U}{B}"
    colorIdentity = "BUW"
    typeLine = "Instant"
    oracleText = "This spell can't be countered.\nDestroy target nonland permanent."

    cantBeCountered = true

    spell {
        val t = target("target", TargetPermanent(filter = TargetFilter.NonlandPermanent))
        effect = Effects.Destroy(t)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "230"
        artist = "Rovina Cai"
        flavorText = "Bits of the missing agent were discovered in various alleys across all three boroughs."
        imageUri = "https://cards.scryfall.io/normal/front/2/d/2daab74d-d66b-4164-aa19-24e8d5536f7d.jpg?1783923066"
    }
}
