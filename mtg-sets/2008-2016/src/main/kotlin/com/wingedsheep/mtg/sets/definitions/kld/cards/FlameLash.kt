package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Flame Lash
 * {3}{R}
 * Instant
 *
 * Flame Lash deals 4 damage to any target.
 */
val FlameLash = card("Flame Lash") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Flame Lash deals 4 damage to any target."

    spell {
        val any = target("any", Targets.Any)
        effect = Effects.DealDamage(4, any)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "266"
        artist = "Viktor Titov"
        flavorText = "\"This is just my warm-up.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/c/ac44e3cb-cc69-4222-87bc-ffa54b7ab34a.jpg?1783937138"
        inBooster = false
    }
}
