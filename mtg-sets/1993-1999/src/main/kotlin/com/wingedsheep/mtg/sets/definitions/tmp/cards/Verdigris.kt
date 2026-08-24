package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Verdigris
 * {2}{G}
 * Instant
 * Destroy target artifact.
 */
val Verdigris = card("Verdigris") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Destroy target artifact."

    spell {
        val t = target("target", Targets.Artifact)
        effect = Effects.Destroy(t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "264"
        artist = "Zina Saunders"
        flavorText = "\"Only the most sophisticated inventions can survive nature's unsophisticated motivations.\"\n" +
            "—Hanna, *Weatherlight* navigator"
        imageUri = "https://cards.scryfall.io/normal/front/0/c/0c79664d-3461-44e7-afe6-33ec54e312ad.jpg"
    }
}
