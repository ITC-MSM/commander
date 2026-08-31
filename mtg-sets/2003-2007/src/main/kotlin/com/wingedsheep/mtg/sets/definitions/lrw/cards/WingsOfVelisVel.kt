package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Wings of Velis Vel
 * {1}{U}
 * Kindred Instant — Shapeshifter
 *
 * Changeling (This card is every creature type.)
 * Until end of turn, target creature has base power and toughness 4/4, gains all creature types,
 * and gains flying.
 *
 * "Base power and toughness 4/4" is a Layer 7b *set* — it overwrites the printed values but still
 * sits under any counters and pump the creature picks up afterwards, so `SetBasePowerAndToughness`
 * is the right primitive rather than a `ModifyStats` delta.
 *
 * "Gains all creature types" is modelled by granting Changeling — the engine expands that keyword
 * into every creature type (CR 702.73), the same way [BladesOfVelisVel] does.
 *
 * Note: "Tribal" was errata'd to "Kindred" in 2024.
 */
val WingsOfVelisVel = card("Wings of Velis Vel") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Kindred Instant — Shapeshifter"
    oracleText = "Changeling (This card is every creature type.)\n" +
        "Until end of turn, target creature has base power and toughness 4/4, gains all " +
        "creature types, and gains flying."

    keywords(Keyword.CHANGELING)

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.Composite(
            Effects.SetBasePowerAndToughness(4, 4, creature),
            Effects.GrantKeyword(Keyword.CHANGELING, creature),
            Effects.GrantKeyword(Keyword.FLYING, creature),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "97"
        artist = "Jim Pavelec"
        flavorText = "Changeling magic grants unusual wishes."
        imageUri = "https://cards.scryfall.io/normal/front/f/b/fb3c1f39-b6ac-4663-9623-bd573a1117b0.jpg?1783942894"
    }
}
