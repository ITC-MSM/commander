package com.wingedsheep.mtg.sets.definitions.ncc.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Grand Crescendo
 * {X}{W}{W}
 * Instant
 *
 * Create X 1/1 green and white Citizen creature tokens. Creatures you control gain
 * indestructible until end of turn.
 *
 * Order matters: the tokens are created first, so the indestructible grant — a one-shot over the
 * creatures you control *as the spell finishes resolving* ([Effects.ForEachInGroup]) — covers the
 * Citizens it just made.
 */
val GrandCrescendo = card("Grand Crescendo") {
    manaCost = "{X}{W}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Create X 1/1 green and white Citizen creature tokens. Creatures you control " +
        "gain indestructible until end of turn."

    spell {
        effect = Effects.Composite(
            Effects.CreateToken(
                count = DynamicAmount.XValue,
                power = 1,
                toughness = 1,
                colors = setOf(Color.GREEN, Color.WHITE),
                creatureTypes = setOf("Citizen")
            ),
            Effects.ForEachInGroup(
                GroupFilter(GameObjectFilter.Creature.youControl()),
                Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, EffectTarget.Self)
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "16"
        artist = "Raluca Marinescu"
        flavorText = "The roar of applause drowned out the melee raging in the lobby."
        imageUri = "https://cards.scryfall.io/normal/front/e/0/e09ff04d-cdcc-4798-b89a-9ad08ef52ad9.jpg"
    }
}
