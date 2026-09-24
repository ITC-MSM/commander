package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Verdant Kraken — Reality Fracture #118
 * {4}{G}{G}{G} · Creature — Plant Kraken · 6/6
 *
 * At the beginning of each player's upkeep, you create a 3/3 green Forest Tentacle land creature
 * token.
 *
 * "You" is the Kraken's controller on every player's upkeep, not the active player. The token is
 * the predefined `Forest Tentacle` ([Effects.CreateForestTentacle]); its mana ability is the
 * Forest type's intrinsic one.
 */
val VerdantKraken = card("Verdant Kraken") {
    manaCost = "{4}{G}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Plant Kraken"
    power = 6
    toughness = 6
    oracleText = "At the beginning of each player's upkeep, you create a 3/3 green Forest Tentacle " +
        "land creature token. (It has \"{T}: Add {G}.\" It's affected by summoning sickness until " +
        "your next turn.)"

    triggeredAbility {
        trigger = Triggers.EachUpkeep
        effect = Effects.CreateForestTentacle()
        description = "At the beginning of each player's upkeep, you create a 3/3 green Forest " +
            "Tentacle land creature token."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "118"
        artist = "Wayne Reynolds"
        flavorText = "It specializes in natural disorder."
        imageUri = "https://cards.scryfall.io/normal/front/2/b/2bb7a8eb-227f-410b-859f-750ef0aea2f0.jpg?1789644841"
        inBooster = false
    }
}
