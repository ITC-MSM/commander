package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Flatten
 * {3}{B}
 * Instant
 *
 * Target creature gets -4/-4 until end of turn.
 *
 * A single clause, so a bare [Effects.ModifyStats] and no wrapping composite — the negative
 * modifiers are the whole card, and "until end of turn" is the facade's default duration.
 */
val Flatten = card("Flatten") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Target creature gets -4/-4 until end of turn."

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.ModifyStats(-4, -4, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "100"
        artist = "Raoul Vitale"
        flavorText = "Like their dragonlord, the Kolaghan take no trophies. They find true fulfillment only in the battle itself, in clash of steel and thunder of hooves."
        imageUri = "https://cards.scryfall.io/normal/front/a/2/a2acae67-5238-40bb-a173-6fc858264a6c.jpg?1783938598"
    }
}
