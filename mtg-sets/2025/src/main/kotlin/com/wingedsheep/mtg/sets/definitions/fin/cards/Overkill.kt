package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature


/**
 * Overkill
 * {2}{B}
 * Instant
 * Target creature gets -0/-9999 until end of turn.
 */
val Overkill = card("Overkill") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Target creature gets -0/-9999 until end of turn."
    spell {
        val t = target("target", TargetCreature(filter = TargetFilter.Creature))
        effect = Effects.ModifyStats(0, -9999, t)
    }
    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "109"
        artist = "Bachzim"
        flavorText = "\"Hey why don't ya try out that sword I gave you!\"\n—Wakka"
        imageUri = "https://cards.scryfall.io/normal/front/a/e/ae075e71-d33d-4d6c-b4a5-0b47dd6fd196.jpg"
    }
}
