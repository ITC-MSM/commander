package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Explosive Impact
 * {5}{R}
 * Instant
 *
 * Explosive Impact deals 5 damage to any target.
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * "Any target" is [Targets.Any] — creature, player or planeswalker in one requirement.
 */
val ExplosiveImpact = card("Explosive Impact") {
    manaCost = "{5}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Explosive Impact deals 5 damage to any target."

    spell {
        val t = target("any target", Targets.Any)
        effect = Effects.DealDamage(5, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "94"
        artist = "Steve Argyle"
        flavorText = "\"Such boorish noise is what passes for subtlety among the Boros.\"\n" +
            "—Vraska"
        imageUri = "https://cards.scryfall.io/normal/front/3/a/3a3e2b45-b086-4ffd-aa1a-1d03046e0d61.jpg?1783940356"
    }
}
