package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CanOnlyBlockCreaturesWith
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Long-Finned Skywhale
 * {2}{U}{U}
 * Creature — Whale
 * 4/3
 * Flying
 * This creature can block only creatures with flying.
 *
 * "Can block only creatures with flying" is the generalized [CanOnlyBlockCreaturesWith]
 * restriction; its `filter` defaults to the source, so the restriction applies to this creature
 * alone.
 */
val LongFinnedSkywhale = card("Long-Finned Skywhale") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Whale"
    oracleText = "Flying\n" +
        "This creature can block only creatures with flying."
    power = 4
    toughness = 3

    keywords(Keyword.FLYING)

    staticAbility {
        ability = CanOnlyBlockCreaturesWith(
            blockerFilter = GameObjectFilter.Creature.withKeyword(Keyword.FLYING)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "54"
        artist = "Cliff Childs"
        flavorText = "The aethersphere is home to the most wondrous beings on all of Kaladesh, although the dangers of traversing it mean that not much is known of them."
        imageUri = "https://cards.scryfall.io/normal/front/7/7/772e9472-c710-474e-b8e9-54662330a592.jpg?1783937217"
    }
}
