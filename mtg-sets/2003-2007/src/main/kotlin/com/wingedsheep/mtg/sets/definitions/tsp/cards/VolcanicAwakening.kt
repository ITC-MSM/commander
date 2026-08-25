package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Volcanic Awakening
 * {4}{R}{R}
 * Sorcery
 * Destroy target land.
 * Storm (When you cast this spell, copy it for each spell cast before it this turn. You may
 * choose new targets for the copies.)
 *
 * Storm copies the spell off `script.spellEffect`, so this stays a plain `spell { effect = … }`
 * — a modal or replacement shape would make the trigger resolve into zero copies.
 */
val VolcanicAwakening = card("Volcanic Awakening") {
    manaCost = "{4}{R}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Destroy target land.\n" +
        "Storm (When you cast this spell, copy it for each spell cast before it this turn. You may choose new targets for the copies.)"

    spell {
        val t = target("target", TargetObject(filter = TargetFilter.Land))
        effect = Effects.Destroy(t)
    }

    keywords(Keyword.STORM)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "186"
        artist = "Dan Murayama Scott"
        flavorText = "With a great roar, the land opened like a titan's yawn, with teeth of blackened rock and a lolling tongue of magma."
        imageUri = "https://cards.scryfall.io/normal/front/a/e/aebd5c57-cfc8-4a3c-b4a2-0cd64a5e3575.jpg"
    }
}
