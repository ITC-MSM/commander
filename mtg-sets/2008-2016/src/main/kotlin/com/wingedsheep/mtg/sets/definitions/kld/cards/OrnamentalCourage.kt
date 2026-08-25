package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ornamental Courage
 * {G}
 * Instant
 *
 * Untap target creature. It gets +1/+3 until end of turn.
 *
 * "It" is the same bound `target` handle as the untap, so both halves resolve against the one
 * creature chosen on announcement.
 */
val OrnamentalCourage = card("Ornamental Courage") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Untap target creature. It gets +1/+3 until end of turn."

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.Composite(
            Effects.Untap(t),
            Effects.ModifyStats(1, 3, t)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "164"
        artist = "Anthony Palumbo"
        flavorText = "Inspired by the thorns and brambles of the untamed wilds, elvish armor is both beautiful and formidable."
        imageUri = "https://cards.scryfall.io/normal/front/e/c/ec0c49ab-da04-4461-8440-d6c9086443c6.jpg?1783937174"
    }
}
