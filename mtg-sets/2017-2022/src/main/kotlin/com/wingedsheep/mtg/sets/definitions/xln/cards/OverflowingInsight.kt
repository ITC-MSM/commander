package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Overflowing Insight
 * {4}{U}{U}{U}
 * Sorcery
 *
 * Target player draws seven cards.
 */
val OverflowingInsight = card("Overflowing Insight") {
    manaCost = "{4}{U}{U}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Target player draws seven cards."

    spell {
        val player = target("target", Targets.Player)
        effect = Effects.DrawCards(7, player)
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "66"
        artist = "Lucas Graciano"
        flavorText = "The truth came to Kumena like the Great River's torrent: the only way to keep his enemies away from the hidden city was to claim its power for himself."
        imageUri = "https://cards.scryfall.io/normal/front/f/6/f6b3dd8f-902e-45b8-a422-370705621294.jpg"
    }
}
