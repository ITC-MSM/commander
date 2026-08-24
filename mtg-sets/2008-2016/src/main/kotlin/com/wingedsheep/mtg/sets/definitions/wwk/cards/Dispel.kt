package com.wingedsheep.mtg.sets.definitions.wwk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetSpell

/**
 * Dispel
 * {U}
 * Instant
 * Counter target instant spell.
 */
val Dispel = card("Dispel") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Counter target instant spell."

    spell {
        target("target instant spell", TargetSpell(filter = TargetFilter.InstantSpellOnStack))
        effect = Effects.CounterSpell()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "26"
        artist = "Vance Kovacs"
        flavorText = "The Jwari winds undo reality as easily as they scatter a pile of leaves."
        imageUri = "https://cards.scryfall.io/normal/front/f/1/f178d0cc-5dd1-41ab-a2e8-218ece6f2a86.jpg?1783942063"
    }
}
