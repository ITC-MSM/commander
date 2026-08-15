package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Lost to Legend
 * {W}{W}
 * Instant
 *
 * Put target nonland historic permanent into its owner's library fourth from the top.
 * (Artifacts, legendaries, and Sagas are historic.)
 */
val LostToLegend = card("Lost to Legend") {
    manaCost = "{W}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Put target nonland historic permanent into its owner's library fourth from the top. " +
        "(Artifacts, legendaries, and Sagas are historic.)"

    spell {
        val permanent = target(
            "nonland historic permanent",
            TargetPermanent(
                filter = TargetFilter(GameObjectFilter.NonlandPermanent and GameObjectFilter.Historic)
            )
        )
        // "fourth from the top" → 0-indexed position 3
        effect = Effects.PutIntoLibraryNthFromTop(permanent, positionFromTop = 3)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "22"
        artist = "Kasia 'Kafis' Zielińska"
        flavorText = "\"In the days of Isildur, the Ruling Ring passed out of all knowledge.\"\n—Elrond"
        imageUri = "https://cards.scryfall.io/normal/front/c/4/c4e40481-aeaa-4b3d-a020-e9b6d7c11992.jpg?1686967846"
    }
}
