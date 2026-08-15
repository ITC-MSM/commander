package com.wingedsheep.mtg.sets.definitions.m12.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Titanic Growth
 * {1}{G}
 * Instant
 * Target creature gets +4/+4 until end of turn.
 *
 * Canonical printing lives in Magic 2012 (M12), the card's earliest real-expansion printing.
 * Later reprints (e.g. Wilds of Eldraine) contribute only a [com.wingedsheep.sdk.model.Printing] row.
 */
val TitanicGrowth = card("Titanic Growth") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Target creature gets +4/+4 until end of turn."

    spell {
        val t = target("target", TargetCreature(filter = TargetFilter.Creature))
        effect = Effects.ModifyStats(4, 4, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "198"
        artist = "Ryan Pancoast"
        flavorText = "The pup looked over the treetops, eyeing the man who just yesterday had kicked her. " +
            "Suddenly, her hunger was infused with pure delight."
        imageUri = "https://cards.scryfall.io/normal/front/d/b/db3c8982-e1c2-48be-8094-683d00c2e52b.jpg?1783941054"
    }
}
