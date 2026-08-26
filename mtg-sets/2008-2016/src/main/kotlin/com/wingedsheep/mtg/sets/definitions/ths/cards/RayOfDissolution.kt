package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ray of Dissolution
 * {2}{W}
 * Instant
 *
 * Destroy target enchantment. You gain 3 life.
 */
val RayOfDissolution = card("Ray of Dissolution") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Destroy target enchantment. You gain 3 life."

    spell {
        val t = target("target", Targets.Enchantment)
        effect = Effects.Composite(
            Effects.Move(t, Zone.GRAVEYARD, byDestruction = true),
            Effects.GainLife(3)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "27"
        artist = "Terese Nielsen"
        flavorText = "The works of one god last only as long as the patience of another."
        imageUri = "https://cards.scryfall.io/normal/front/b/5/b55c31d1-6b05-4ea1-a444-51ad57ebcfa0.jpg"
    }
}
