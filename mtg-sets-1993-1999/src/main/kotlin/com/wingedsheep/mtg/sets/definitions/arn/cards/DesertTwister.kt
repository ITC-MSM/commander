package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Desert Twister
 * {4}{G}{G}
 * Sorcery
 * Destroy target permanent.
 */
val DesertTwister = card("Desert Twister") {
    manaCost = "{4}{G}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Destroy target permanent."

    spell {
        val t = target("target permanent", Targets.Permanent)
        effect = Effects.Destroy(t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "46"
        artist = "Susan Van Camp"
        imageUri = "https://cards.scryfall.io/normal/front/0/d/0d77c149-cca2-45c7-bc83-5ba1872ad5e0.jpg?1562897613"
    }
}
