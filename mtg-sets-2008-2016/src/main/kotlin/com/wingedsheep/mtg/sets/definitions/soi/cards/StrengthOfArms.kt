package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Strength of Arms
 * {W}
 * Instant
 * Target creature gets +2/+2 until end of turn. If you control an Equipment, create a 1/1 white
 * Human Soldier creature token.
 */
val StrengthOfArms = card("Strength of Arms") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Target creature gets +2/+2 until end of turn. If you control an Equipment, " +
        "create a 1/1 white Human Soldier creature token."

    spell {
        target = Targets.Creature
        effect = Effects.ModifyStats(2, 2, EffectTarget.ContextTarget(0))
            .then(
                ConditionalEffect(
                    condition = Conditions.YouControlAtLeast(
                        1,
                        GameObjectFilter.Artifact.withSubtype(Subtype.EQUIPMENT)
                    ),
                    effect = Effects.CreateToken(
                        power = 1,
                        toughness = 1,
                        colors = setOf(Color.WHITE),
                        creatureTypes = setOf("Human", "Soldier")
                    )
                )
            )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "40"
        artist = "John Stanko"
        flavorText = "\"We fight not for Avacyn, but for her ideals; not for the church, but for " +
            "its people.\"\n—Thalia, Guardian of Thraben"
        imageUri = "https://cards.scryfall.io/normal/front/5/2/52c5c5cf-0ed6-4953-a03a-af51038e3f54.jpg?1782712132"
    }
}
