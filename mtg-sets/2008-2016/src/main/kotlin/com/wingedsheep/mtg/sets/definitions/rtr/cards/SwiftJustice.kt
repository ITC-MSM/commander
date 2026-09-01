package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Swift Justice
 * {W}
 * Instant
 *
 * Until end of turn, target creature gets +1/+0 and gains first strike and lifelink.
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * Three end-of-turn effects on one bound target. Each keyword is its own grant — the SDK has no
 * multi-keyword facade, and one per grant keeps the layer-6 read simple.
 */
val SwiftJustice = card("Swift Justice") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Until end of turn, target creature gets +1/+0 and gains first strike and lifelink."

    spell {
        val t = target("target creature", Targets.Creature)
        effect = Effects.Composite(
            Effects.ModifyStats(1, 0, t),
            Effects.GrantKeyword(Keyword.FIRST_STRIKE, t),
            Effects.GrantKeyword(Keyword.LIFELINK, t),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "26"
        artist = "Karl Kopinski"
        flavorText = "\"Having conviction is more important than being righteous.\"\n" +
            "—Aurelia"
        imageUri = "https://cards.scryfall.io/normal/front/a/9/a94801ba-0295-4611-abda-4c6508d69cc3.jpg?1783940373"
    }
}
