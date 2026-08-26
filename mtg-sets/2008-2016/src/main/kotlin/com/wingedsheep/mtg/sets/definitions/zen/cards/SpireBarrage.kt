package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Spire Barrage
 * {4}{R}
 * Sorcery
 * Spire Barrage deals damage to any target equal to the number of Mountains you control.
 */
val SpireBarrage = card("Spire Barrage") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Spire Barrage deals damage to any target equal to the number of Mountains you control."

    spell {
        val anyTarget = target("any target", Targets.Any)
        effect = Effects.DealDamage(
            DynamicAmounts.battlefield(Player.You, GameObjectFilter.Land.withSubtype("Mountain")).count(),
            anyTarget,
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "150"
        artist = "Ryan Pancoast"
        flavorText = "Goblin lessons include the 2,071 tips for survival. Frek only remembered 2,070."
        imageUri = "https://cards.scryfall.io/normal/front/f/8/f8c8fe2a-3203-4545-bb25-b1f107b13cca.jpg"
    }
}
