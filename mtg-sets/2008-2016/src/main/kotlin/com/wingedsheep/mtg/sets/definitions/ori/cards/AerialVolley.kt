package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Aerial Volley
 * {G}
 * Instant
 *
 * Aerial Volley deals 3 damage divided as you choose among one, two, or three target creatures with flying.
 *
 * "one, two, or three target creatures" is the divided-damage target shape: a single `count = 3,
 * minCount = 1` requirement rather than three requirements, with [Effects.DividedDamage] doing the
 * assignment at cast time.
 */
val AerialVolley = card("Aerial Volley") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Aerial Volley deals 3 damage divided as you choose among one, two, or three target creatures with flying."

    spell {
        target(
            "target creatures with flying",
            TargetCreature(filter = TargetFilter.Creature.withKeyword(Keyword.FLYING), count = 3, minCount = 1)
        )
        effect = Effects.DividedDamage(total = 3, minTargets = 1, maxTargets = 3)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "168"
        artist = "Lake Hurwitz"
        flavorText = "Drakes can swerve to avoid a single arrow, but they can't dodge the whole sky."
        imageUri = "https://cards.scryfall.io/normal/front/c/5/c5178301-f766-48d3-af07-6bd6f822c725.jpg"
    }
}
