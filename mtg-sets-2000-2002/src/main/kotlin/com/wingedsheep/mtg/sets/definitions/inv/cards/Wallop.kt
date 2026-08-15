package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Wallop
 * {1}{G}
 * Sorcery
 * Destroy target blue or black creature with flying.
 */
val Wallop = card("Wallop") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Destroy target blue or black creature with flying."

    spell {
        val t = target(
            "creature",
            TargetCreature(
                filter = TargetFilter(
                    GameObjectFilter.Creature
                        .withAnyColor(Color.BLUE, Color.BLACK)
                        .withKeyword(Keyword.FLYING)
                )
            )
        )
        effect = Effects.Destroy(t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "223"
        artist = "Mike Ploog"
        flavorText = "In Yavimaya, flying low to join a battle can be a costly mistake."
        imageUri = "https://cards.scryfall.io/normal/front/4/5/45ce5126-e7b1-41ab-9e56-1e12927c4d27.jpg?1562909144"
    }
}
