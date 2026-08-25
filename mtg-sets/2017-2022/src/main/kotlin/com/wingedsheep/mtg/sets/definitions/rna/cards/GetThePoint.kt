package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Get the Point — Ravnica Allegiance #176
 * {3}{B}{R} · Instant
 *
 * Destroy then scry, in that order — the scry happens even if the creature is gone by the time
 * the spell resolves, because it is a separate step of the same resolution.
 */
val GetThePoint = card("Get the Point") {
    manaCost = "{3}{B}{R}"
    colorIdentity = "BR"
    typeLine = "Instant"
    oracleText = "Destroy target creature. Scry 1."

    spell {
        val creature = target("target", Targets.Creature)
        effect = Effects.Composite(listOf(
            Effects.Destroy(creature),
            Effects.Scry(1)
        ))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "176"
        artist = "Steve Argyle"
        flavorText = "\"Vraska sees the grandeur in death but misses the hilarity.\"\n" +
        "—Judith"
        imageUri = "https://cards.scryfall.io/normal/front/8/2/821c4ab5-eb75-445a-bbec-e50af54dba7a.jpg"
    }
}
