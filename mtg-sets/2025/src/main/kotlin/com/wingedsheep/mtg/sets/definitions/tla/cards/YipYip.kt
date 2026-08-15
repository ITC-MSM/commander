package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Yip Yip!
 * {W}
 * Instant — Lesson
 * Target creature you control gets +2/+2 until end of turn. If that creature is an Ally,
 * it also gains flying until end of turn.
 */
val YipYip = card("Yip Yip!") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Instant — Lesson"
    oracleText = "Target creature you control gets +2/+2 until end of turn. If that creature is an Ally, " +
        "it also gains flying until end of turn."

    spell {
        val t = target("target creature you control", TargetCreature(filter = TargetFilter.Creature.youControl()))
        effect = Effects.Composite(
            Effects.ModifyStats(2, 2, t),
            ConditionalEffect(
                condition = Conditions.TargetMatchesFilter(GameObjectFilter.Any.withSubtype(Subtype.ALLY)),
                effect = Effects.GrantKeyword(Keyword.FLYING, t),
            ),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "43"
        artist = "Cinkai"
        flavorText = "\"You better throw in an extra 'yip'! We gotta move!\"\n—Sokka"
        imageUri = "https://cards.scryfall.io/normal/front/4/3/43f4b10e-165b-4100-82f3-728e1b0c78ed.jpg?1764120182"
    }
}
