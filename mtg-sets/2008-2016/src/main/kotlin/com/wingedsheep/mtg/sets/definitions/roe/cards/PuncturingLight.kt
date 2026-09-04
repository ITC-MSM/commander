package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Puncturing Light
 * {1}{W}
 * Instant
 *
 * Destroy target attacking or blocking creature with power 3 or less.
 *
 * Modeling notes:
 *  - The whole card is its target requirement plus `Effects.Destroy`, which is the
 *    move-to-graveyard-by-destruction effect Assay compiles "destroy" to (so regeneration and
 *    indestructible apply as they should).
 *  - "attacking or blocking" is the single `TargetFilter.AttackingOrBlockingCreature` predicate —
 *    one `StatePredicate.Or`, not two separate clauses — narrowed by `.powerAtMost(3)` for
 *    "with power 3 or less". Power is read from projected state, so a creature pumped above 3 in
 *    response stops being a legal target.
 *  - Both halves live on the target requirement rather than being checked on resolution: the card
 *    says "target … creature with power 3 or less", so the restriction is part of legality.
 */
val PuncturingLight = card("Puncturing Light") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Destroy target attacking or blocking creature with power 3 or less."

    spell {
        val creature = target(
            "target attacking or blocking creature with power 3 or less",
            TargetCreature(filter = TargetFilter.AttackingOrBlockingCreature.powerAtMost(3))
        )
        effect = Effects.Destroy(creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "41"
        artist = "Zoltan Boros & Gabor Szikszai"
        flavorText = "\"The vampires knew what was coming. I know it. And they did nothing. They deserve to feel the same agony they've caused all of us.\"\n—Anitan, Ondu cleric"
        imageUri = "https://cards.scryfall.io/normal/front/e/5/e52d260a-e1ca-4228-855e-2e104b86fd6c.jpg?1783942003"
    }
}
