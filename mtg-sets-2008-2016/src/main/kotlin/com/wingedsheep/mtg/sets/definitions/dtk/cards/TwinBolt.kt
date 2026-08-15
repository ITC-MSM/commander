package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.DividedDamageEffect
import com.wingedsheep.sdk.scripting.targets.AnyTarget

/**
 * Twin Bolt
 * {1}{R}
 * Instant
 * Twin Bolt deals 2 damage divided as you choose among one or two targets.
 *
 * Canonical definition lives in DTK (earliest real-expansion printing). TDM (and CN2)
 * are reprints that add only a [com.wingedsheep.sdk.model.Printing] row.
 */
val TwinBolt = card("Twin Bolt") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Twin Bolt deals 2 damage divided as you choose among one or two targets."

    spell {
        target = AnyTarget(count = 2, minCount = 1)
        effect = DividedDamageEffect(
            totalDamage = 2,
            minTargets = 1,
            maxTargets = 2
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "164"
        artist = "Svetlin Velinov"
        flavorText = "\"Kolaghan archers are trained in Dakla, the way of the bow. They utilize their dragonlord's lightning to strike their target, no matter how small, how fast, or how far away.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/b/5bd58ec4-34a9-4fc2-b057-438492e2e06e.jpg?1562786902"
    }
}
