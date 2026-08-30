package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Tread Upon
 * {1}{G}
 * Instant
 *
 * Target creature gets +2/+2 and gains trample until end of turn.
 *
 * The single-target half of [PressTheAdvantage]: one [Effects.Composite] over the pump and the
 * trample grant, both on the same bound target, with the facade's default until-end-of-turn
 * duration.
 */
val TreadUpon = card("Tread Upon") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Target creature gets +2/+2 and gains trample until end of turn."

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.Composite(
            Effects.ModifyStats(2, 2, t),
            Effects.GrantKeyword(Keyword.TRAMPLE, t)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "211"
        artist = "Efrem Palacios"
        flavorText = "\"Boasting impenetrable defenses only draws the most tenacious of attackers.\"\n—Yikaro, Atarka warrior"
        imageUri = "https://cards.scryfall.io/normal/front/8/7/87f04d27-f38a-4be2-8eb6-7dd0d3a1ac6d.jpg?1783938574"
    }
}
