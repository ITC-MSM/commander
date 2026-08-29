package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Fateful End
 * {2}{R}
 * Instant
 *
 * Fateful End deals 3 damage to any target. Scry 1.
 *
 * A burn spell plus a rider: [Effects.Composite] of [Effects.DealDamage] at the [Targets.Any]
 * binding and the compact [Effects.Scry] macro, in printed order. The scry happens even if the
 * damage is prevented, because it is a separate effect in the same resolution.
 */
val FatefulEnd = card("Fateful End") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Fateful End deals 3 damage to any target. Scry 1."

    spell {
        val t = target("target", Targets.Any)
        effect = Effects.Composite(
            Effects.DealDamage(3, t),
            Effects.Scry(1)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "133"
        artist = "Chris Rallis"
        flavorText = "\"Everything will be put back in its proper place.\"\n—Klothys, god of destiny"
        imageUri = "https://cards.scryfall.io/normal/front/5/6/56455067-92c0-45b5-ac2e-525c35b41215.jpg"
    }
}
