package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Chain to Memory
 * {U}
 * Instant
 *
 * Target creature gets -4/-0 until end of turn. Scry 2.
 *
 * Two printed sentences, so the spell effect is a two-element [Effects.Composite] in printed order:
 * the [Effects.ModifyStats] pump on the bound target, then the compact [Effects.Scry] macro. The
 * toughness modifier is an explicit `0` rather than omitted — "-4/-0" spells both halves.
 */
val ChainToMemory = card("Chain to Memory") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Target creature gets -4/-0 until end of turn. Scry 2."

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.Composite(
            Effects.ModifyStats(-4, 0, t),
            Effects.Scry(2)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "46"
        artist = "Paul Scott Canavan"
        flavorText = "Those who do not learn from their mistakes are bound to relive them."
        imageUri = "https://cards.scryfall.io/normal/front/f/a/fa0e1a22-8f27-4c5b-a65c-c35abd2ff05b.jpg"
    }
}
