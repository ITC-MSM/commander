package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Gather Courage
 * {G}
 * Instant
 * Convoke
 * Target creature gets +2/+2 until end of turn.
 *
 * Canonical printing: Ravnica: City of Guilds, the card's earliest real-expansion printing.
 */
val GatherCourage = card("Gather Courage") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText =
        "Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)\n" +
        "Target creature gets +2/+2 until end of turn."

    keywords(Keyword.CONVOKE)

    spell {
        val t = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(2, 2, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "165"
        artist = "Brian Despain"
        imageUri = "https://cards.scryfall.io/normal/front/9/3/93311f80-0d0e-4005-ba43-5dbfe438d127.jpg?1783943638"
    }
}
