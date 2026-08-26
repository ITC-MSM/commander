package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Lucky Offering — Kamigawa: Neon Dynasty #27 (canonical printing)
 * {W} · Sorcery
 *
 * Destroy target artifact with mana value 3 or less. You gain 3 life.
 */
val LuckyOffering = card("Lucky Offering") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Destroy target artifact with mana value 3 or less. You gain 3 life."

    spell {
        val t = target(
            "cheap artifact",
            TargetObject(filter = TargetFilter(GameObjectFilter.Artifact.manaValueAtMost(3))),
        )
        effect = Effects.Destroy(t) then Effects.GainLife(3)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "27"
        artist = "Fiona Hsieh"
        flavorText = "The playful kami reserved its greatest blessings for those who brought not " +
            "wealth or rare delicacies, but particularly amusing trinkets."
        imageUri = "https://cards.scryfall.io/normal/front/2/4/24bbdeb0-9165-4874-a853-d19c20c250ef.jpg?1783923916"
    }
}
