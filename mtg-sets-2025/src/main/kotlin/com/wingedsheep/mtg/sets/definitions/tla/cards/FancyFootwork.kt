package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Fancy Footwork
 * {2}{W}
 * Instant — Lesson
 *
 * Untap one or two target creatures. They each get +2/+2 until end of turn.
 */
val FancyFootwork = card("Fancy Footwork") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Instant — Lesson"
    oracleText = "Untap one or two target creatures. They each get +2/+2 until end of turn."

    spell {
        // "one or two target creatures" = count 2, minCount 1.
        target = TargetCreature(count = 2, minCount = 1)
        // Apply to each chosen creature: untap, then +2/+2 until end of turn.
        effect = ForEachTargetEffect(
            effects = listOf(
                Effects.Untap(EffectTarget.ContextTarget(0)),
                Effects.ModifyStats(2, 2, EffectTarget.ContextTarget(0))
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "19"
        artist = "Mizutametori"
        flavorText = "\"Dancing isn't something you think about. It's a form of self-expression that no one can ever take away from you.\"\n—Aang"
        imageUri = "https://cards.scryfall.io/normal/front/9/c/9c59f8c5-4063-4e5a-b684-fde175c4a981.jpg?1764120000"
    }
}
