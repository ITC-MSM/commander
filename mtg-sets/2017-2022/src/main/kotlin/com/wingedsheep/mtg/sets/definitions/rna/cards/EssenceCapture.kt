package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Essence Capture — Ravnica Allegiance #37
 * {U}{U} · Instant
 *
 * Two targets, the second "up to one" — an [TargetCreature] with `optional = true`, so the
 * spell is still castable (and still counters) with no creature of yours on the battlefield.
 * The optional target is declared last, which is where an optional target must sit.
 */
val EssenceCapture = card("Essence Capture") {
    manaCost = "{U}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Counter target creature spell. Put a +1/+1 counter on up to one target creature you control."

    spell {
        target("target", Targets.CreatureSpell)
        val ally = target(
            "target 1",
            TargetCreature(optional = true, filter = TargetFilter.CreatureYouControl)
        )
        effect = Effects.Composite(listOf(
            Effects.CounterSpell(),
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, ally)
        ))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "37"
        artist = "Mathias Kollros"
        flavorText = "\"It's not enough to defeat our foes. We must learn from them, too.\"\n" +
        "—Vannifar"
        imageUri = "https://cards.scryfall.io/normal/front/c/e/ce137910-0f0e-4f94-9b95-6e0eeeba164e.jpg"
    }
}
