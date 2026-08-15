package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Savor
 * {1}{B}
 * Instant
 *
 * Target creature gets -2/-2 until end of turn. Create a Food token.
 */
val Savor = card("Savor") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Target creature gets -2/-2 until end of turn. Create a Food token. (It's an artifact with \"{2}, {T}, Sacrifice this token: You gain 3 life.\")"

    spell {
        val creature = target("target creature to get -2/-2", Targets.Creature)
        effect = Effects.ModifyStats(-2, -2, creature)
            .then(Effects.CreateFood(1))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "109"
        artist = "Kev Walker"
        flavorText = "Edibility is in the eye of the beholder."
        imageUri = "https://cards.scryfall.io/normal/front/1/3/1397f689-dca1-4d35-864b-92c5606afb9a.jpg?1721426493"
    }
}
