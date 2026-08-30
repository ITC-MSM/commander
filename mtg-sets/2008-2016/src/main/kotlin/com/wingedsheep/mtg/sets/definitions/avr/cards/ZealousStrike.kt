package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Zealous Strike
 * {1}{W}
 * Instant
 *
 * Target creature gets +2/+2 and gains first strike until end of turn.
 *
 * The ordinary combat trick: one [Effects.Composite] of [Effects.ModifyStats] and
 * [Effects.GrantKeyword] over the same bound target, both taking the default
 * `Duration.EndOfTurn`.
 */
val ZealousStrike = card("Zealous Strike") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Target creature gets +2/+2 and gains first strike until end of turn."

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.Composite(
            Effects.ModifyStats(2, 2, t),
            Effects.GrantKeyword(Keyword.FIRST_STRIKE, t),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "41"
        artist = "Bud Cook"
        flavorText = "\"Cower, fiend. The night is yours no longer.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/e/ae8a01fb-dd47-44de-b528-8b7ca4b3388b.jpg?1783940724"
    }
}
