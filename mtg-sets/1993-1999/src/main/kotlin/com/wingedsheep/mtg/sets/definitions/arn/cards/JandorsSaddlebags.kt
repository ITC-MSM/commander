package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Jandor's Saddlebags
 * {2}
 * Artifact
 * {3}, {T}: Untap target creature.
 */
val JandorsSaddlebags = card("Jandor's Saddlebags") {
    manaCost = "{2}"
    typeLine = "Artifact"
    oracleText = "{3}, {T}: Untap target creature."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}"), Costs.Tap)
        val creature = target("target creature", Targets.Creature)
        effect = Effects.Untap(creature)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "65"
        artist = "Dameon Willich"
        flavorText = "Each day of their journey, Jandor opened the saddlebags and found them full of mutton, quinces, cheese, date rolls, wine, and all manner of delicious and satisfying foods."
        imageUri = "https://cards.scryfall.io/normal/front/b/c/bc4f4b92-7d4e-4b03-8cb4-e6b356c338b4.jpg?1562930191"
    }
}
