package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Aven Trailblazer
 * {2}{W}
 * Creature — Bird Soldier
 * 2 / *
 * Flying
 * Domain — Aven Trailblazer's toughness is equal to the number of basic land types among lands you control.
 *
 * Domain is an ability word (CR 207.2c) with no rules meaning of its own; the count is
 * [DynamicAmounts.domain] — the distinct basic land subtypes among the lands you control, capped
 * at five by there being only five. Only the toughness is characteristic-defining here, so power
 * stays a printed literal and the toughness goes through [dynamicToughness] rather than
 * `dynamicStats`.
 */
val AvenTrailblazer = card("Aven Trailblazer") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Bird Soldier"
    oracleText = "Flying\n" +
        "Domain — Aven Trailblazer's toughness is equal to the number of basic land types among lands you control."

    power = 2
    dynamicToughness(DynamicAmounts.domain())

    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "4"
        artist = "Chris Rahn"
        flavorText = "\"The bird wore the form of a man, bereft of filigree. Why do the Texts not speak of it?\" —Belator of Esper"
        imageUri = "https://cards.scryfall.io/normal/front/c/b/cb032cd3-96a4-4cef-bb89-0843f2ed8189.jpg"
    }
}
