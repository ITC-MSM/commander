package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Prey's Vengeance
 * {G}
 * Instant
 *
 * Target creature gets +2/+2 until end of turn.
 * Rebound (If you cast this spell from your hand, exile it as it resolves. At the beginning of your next upkeep, you may cast this card from exile without paying its mana cost.)
 *
 * Modeling notes:
 *  - Rebound has a real consumer — `StackResolver` reads [Keyword.REBOUND] off `cardDef.keywords`
 *    when the spell resolves — so the bare keyword is the whole of the second line and the pump
 *    arrives twice, a turn apart, from one [Effects.ModifyStats].
 *  - Artful Maneuver's shape exactly, in green: `until end of turn` is [Effects.ModifyStats]'
 *    default duration, so it isn't spelled here.
 *  - The rebound cast re-targets from scratch (CR 601.2c on the new cast), so the target is chosen
 *    again on the upkeep — nothing about the first cast's target is carried, and no context target
 *    is needed.
 */
val PreysVengeance = card("Prey's Vengeance") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Target creature gets +2/+2 until end of turn.\n" +
        "Rebound (If you cast this spell from your hand, exile it as it resolves. At the beginning of your next upkeep, you may cast this card from exile without paying its mana cost.)"

    keywords(Keyword.REBOUND)

    spell {
        val t = target("target", TargetObject(filter = TargetFilter.Creature))
        effect = Effects.ModifyStats(2, 2, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "205"
        artist = "Jesper Ejsing"
        imageUri = "https://cards.scryfall.io/normal/front/d/f/dfd0d15f-8200-46a4-a9e9-e7f0ee2aa0e4.jpg?1783941959"
    }
}
