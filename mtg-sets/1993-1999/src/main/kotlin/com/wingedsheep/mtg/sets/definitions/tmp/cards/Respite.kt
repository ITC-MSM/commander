package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Respite
 * {1}{G}
 * Instant
 * Prevent all combat damage that would be dealt this turn. You gain 1 life for each attacking creature.
 */
val Respite = card("Respite") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Prevent all combat damage that would be dealt this turn. You gain 1 life for each attacking creature."

    spell {
        effect = Effects.Composite(
            Effects.PreventAllCombatDamage(),
            Effects.GainLife(
                DynamicAmounts.battlefield(Player.Each, GameObjectFilter.Creature.attacking()).count()
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "249"
        artist = "Rebecca Guay"
        flavorText = "\"If they board us we're finished,\" warned Orim. Crovax nodded. \"And if they don't . . . what then?\""
        imageUri = "https://cards.scryfall.io/normal/front/2/2/228a8d29-cc14-49c7-ae24-5847344583ed.jpg"
    }
}
