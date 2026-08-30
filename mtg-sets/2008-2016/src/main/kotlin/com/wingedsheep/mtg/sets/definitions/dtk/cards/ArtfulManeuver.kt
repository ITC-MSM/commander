package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Artful Maneuver
 * {1}{W}
 * Instant
 *
 * Target creature gets +2/+2 until end of turn.
 * Rebound (If you cast this spell from your hand, exile it as it resolves. At the beginning of your next upkeep, you may cast this card from exile without paying its mana cost.)
 *
 * Rebound has a real consumer — `StackResolver` reads [Keyword.REBOUND] off `cardDef.keywords`
 * when the spell resolves — so the bare keyword is the whole of the second line and the pump
 * arrives twice, a turn apart, from one [Effects.ModifyStats]. `until end of turn` is that
 * facade's default duration, so it isn't spelled here.
 */
val ArtfulManeuver = card("Artful Maneuver") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Target creature gets +2/+2 until end of turn.\n" +
        "Rebound (If you cast this spell from your hand, exile it as it resolves. At the beginning of your next upkeep, you may cast this card from exile without paying its mana cost.)"

    keywords(Keyword.REBOUND)

    spell {
        val t = target("target", TargetObject(filter = TargetFilter.Creature))
        effect = Effects.ModifyStats(2, 2, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "4"
        artist = "Lars Grant-West"
        imageUri = "https://cards.scryfall.io/normal/front/7/f/7fcaf67e-ba97-4af9-8c47-dbca703cba35.jpg?1783938620"
    }
}
