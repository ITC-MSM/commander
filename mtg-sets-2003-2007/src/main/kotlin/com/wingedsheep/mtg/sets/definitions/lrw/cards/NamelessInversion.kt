package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Nameless Inversion
 * {1}{B}
 * Kindred Instant — Shapeshifter
 *
 * Changeling (This card is every creature type.)
 * Target creature gets +3/-3 and loses all creature types until end of turn.
 *
 * Note: "Tribal" was errata'd to "Kindred" in 2024.
 */
val NamelessInversion = card("Nameless Inversion") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Kindred Instant — Shapeshifter"
    oracleText = "Changeling (This card is every creature type.)\n" +
        "Target creature gets +3/-3 and loses all creature types until end of turn."

    keywords(Keyword.CHANGELING)

    spell {
        val creature = target("creature", Targets.Creature)
        effect = Effects.ModifyStats(3, -3, creature) then
            Effects.LoseAllCreatureTypes(creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "128"
        artist = "Jeff Miracola"
        imageUri = "https://cards.scryfall.io/normal/front/9/4/94b4e4d2-2358-48d2-9a2a-3d17afea28f5.jpg?1562358824"
    }
}
