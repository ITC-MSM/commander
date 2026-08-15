package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect

/**
 * Thwip!
 * {W}
 * Instant
 * Target creature gets +2/+2 and gains flying until end of turn. If it's a Spider, you gain 2 life.
 */
val Thwip = card("Thwip!") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Target creature gets +2/+2 and gains flying until end of turn. If it's a Spider, you gain 2 life."

    spell {
        val t = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(2, 2, t)
            .then(Effects.GrantKeyword(Keyword.FLYING, t))
            .then(
                ConditionalEffect(
                    condition = Conditions.TargetMatchesFilter(
                        GameObjectFilter.Creature.withSubtype(Subtype.SPIDER), targetIndex = 0
                    ),
                    effect = Effects.GainLife(2)
                )
            )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "20"
        artist = "Lordigan"
        flavorText = "\"Yes! This is what being Spider-Man is all about! Swinging through the sky—a New York sky—patrolling the city. " +
            "Every moment a chance to make a difference. To fight the good fight.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/2/b2dac88e-1204-4640-94ce-e1aff434ea06.jpg?1757376864"
    }
}
