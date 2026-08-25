package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Heightened Reflexes
 * {R}
 * Instant
 * Target creature gets +1/+0 until end of turn. Put a first strike counter on it.
 *
 * The pump expires, the counter doesn't: [Counters.FIRST_STRIKE] is a keyword counter, so the
 * creature keeps first strike for as long as the counter sits on it. Both halves read the same
 * named target so "it" is the creature that was targeted.
 */
val HeightenedReflexes = card("Heightened Reflexes") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Target creature gets +1/+0 until end of turn. Put a first strike counter on it."

    spell {
        val creature = target("target", TargetCreature())
        effect = Effects.Composite(
            Effects.ModifyStats(1, 0, creature),
            Effects.AddCounters(Counters.FIRST_STRIKE, 1, creature)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "123"
        artist = "Caio Monteiro"
        flavorText = "Some crystals glow in the presence of monsters, forming a convenient warning system. Some monsters have learned to outrun the light."
        imageUri = "https://cards.scryfall.io/normal/front/4/f/4f7cc8e7-3002-4ee0-869f-931438d8362d.jpg"
    }
}
