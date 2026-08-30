package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Uncanny Speed
 * {1}{R}
 * Instant
 *
 * Target creature gets +3/+0 and gains haste until end of turn.
 *
 * Zealous Strike's red sibling — one [Effects.Composite] of [Effects.ModifyStats] and
 * [Effects.GrantKeyword] over the same bound target, both until end of turn.
 */
val UncannySpeed = card("Uncanny Speed") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Target creature gets +3/+0 and gains haste until end of turn."

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.Composite(
            Effects.ModifyStats(3, 0, t),
            Effects.GrantKeyword(Keyword.HASTE, t),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "163"
        artist = "Raymond Swanland"
        flavorText = "\"To survive, we must embrace the savagery we knew in our race's infancy.\"\n—Edgar Markov"
        imageUri = "https://cards.scryfall.io/normal/front/1/d/1d7b747e-446a-4c25-9834-0be8476dc22d.jpg?1783940673"
    }
}
