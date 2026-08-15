package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Tainted Treats
 * {1}{B}{G}
 * Instant
 *
 * Destroy target artifact or creature. If its mana value was 4 or
 * less, create a Food token.
 */
val TaintedTreats = card("Tainted Treats") {
    manaCost = "{1}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Instant"
    oracleText = "Destroy target artifact or creature. If its mana value was 4 or less, create a Food token. (It's an artifact with \"{2}, {T}, Sacrifice this token: You gain 3 life.\")"

    spell {
        val target = target(
            "artifact or creature",
            TargetPermanent(filter = TargetFilter.CreatureOrArtifact)
        )
        effect = Effects.Move(target, Zone.GRAVEYARD, byDestruction = true)
            .then(
                ConditionalEffect(
                    condition = Conditions.TargetSpellManaValueAtMost(DynamicAmount.Fixed(4)),
                    effect = Effects.CreateFood()
                )
            )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "170"
        artist = "Jakob Eirich"
        flavorText = "\"Spray them with the anti-mutagen? No, no. I'm afraid ingestion is the only course.\"\n—Dr. Jordan Perry"
        imageUri = "https://cards.scryfall.io/normal/front/f/a/fa684608-ecd3-43e2-99a1-71318f133e29.jpg?1771587064"
    }
}
