package com.wingedsheep.mtg.sets.definitions.m10.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Solemn Offering
 * {2}{W}
 * Sorcery
 * Destroy target artifact or enchantment. You gain 4 life.
 *
 * Canonical printing: Magic 2010, the card's earliest real-expansion printing. Reprinted in M11,
 * M14 and M15 as `Printing` rows.
 */
val SolemnOffering = card("Solemn Offering") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Destroy target artifact or enchantment. You gain 4 life."

    spell {
        val t = target("target artifact or enchantment", Targets.ArtifactOrEnchantment)
        effect = Effects.Destroy(t)
            .then(Effects.GainLife(4))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "33"
        artist = "Sam Wood"
        flavorText = "\"A relic donation is suggested.\"\n\"The suggestion is mandatory.\"\n—Temple signs"
        imageUri = "https://cards.scryfall.io/normal/front/6/7/67aafbcc-113e-4816-95d2-a192f32ea9ea.jpg?1783942398"
    }
}
