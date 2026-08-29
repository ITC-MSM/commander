package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Festival of Trokin
 * {W}
 * Sorcery
 * You gain 2 life for each creature you control.
 *
 * "2 life for each" is the battlefield count wrapped in [DynamicAmount.Multiply] — the count
 * carries the player reference, so the filter itself takes no controller predicate.
 */
val FestivalOfTrokin = card("Festival of Trokin") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "You gain 2 life for each creature you control."

    spell {
        effect = Effects.GainLife(
            DynamicAmount.Multiply(
                DynamicAmounts.battlefield(Player.You, GameObjectFilter.Creature).count(),
                2
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "16"
        artist = "Jeffrey R. Busch"
        flavorText = "Everyone loves a good sale."
        imageUri = "https://cards.scryfall.io/normal/front/f/5/f5bd783b-d4cd-4a53-8fec-a5ead7c14738.jpg"
    }
}
