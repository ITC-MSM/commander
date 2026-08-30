package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Hive Stirrings
 * {2}{W}
 * Sorcery
 * Create two 1/1 colorless Sliver creature tokens.
 *
 * The tokens are colorless, i.e. an empty `colors` set. Their art comes from the set's token
 * registry, so no `imageUri` is baked into the token here.
 */
val HiveStirrings = card("Hive Stirrings") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Create two 1/1 colorless Sliver creature tokens."

    spell {
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = emptySet(),
            creatureTypes = setOf("Sliver"),
            count = 2
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "21"
        artist = "Maciej Kuciara"
        flavorText = "Sliver young are sorted into clutches according to their potential and their future role. Human scholars can only guess how those are determined."
        imageUri = "https://cards.scryfall.io/normal/front/e/4/e4399e19-d05d-4bb3-9aff-c4133ddd2850.jpg"
    }
}
