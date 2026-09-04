package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Virulent Swipe
 * {B}
 * Instant
 *
 * Target creature gets +2/+0 and gains deathtouch until end of turn.
 * Rebound (If you cast this spell from your hand, exile it as it resolves. At the beginning of your next upkeep, you may cast this card from exile without paying its mana cost.)
 *
 * Modeling notes:
 *  - Rebound has a real consumer — `StackResolver` reads [Keyword.REBOUND] off `cardDef.keywords`
 *    when the spell resolves — so the bare keyword is the whole of the second line and the whole
 *    pump-plus-deathtouch package arrives twice, a turn apart.
 *  - "gets +2/+0 **and** gains deathtouch" is one sentence over one target, so it is a single
 *    [Effects.Composite] of [Effects.ModifyStats] and [Effects.GrantKeyword] both bound to the
 *    same `target` handle — not two separate targets. Both default to "until end of turn", which
 *    is what the printed line says.
 *  - DEATHTOUCH is a live keyword in `rules-engine` (the combat-damage and state-based-action
 *    paths both read it), so the grant needs no lowering.
 */
val VirulentSwipe = card("Virulent Swipe") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Target creature gets +2/+0 and gains deathtouch until end of turn.\n" +
        "Rebound (If you cast this spell from your hand, exile it as it resolves. At the beginning of your next upkeep, you may cast this card from exile without paying its mana cost.)"

    keywords(Keyword.REBOUND)

    spell {
        val t = target("target", TargetObject(filter = TargetFilter.Creature))
        effect = Effects.Composite(
            Effects.ModifyStats(2, 0, t),
            Effects.GrantKeyword(Keyword.DEATHTOUCH, t)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "131"
        artist = "Raymond Swanland"
        imageUri = "https://cards.scryfall.io/normal/front/b/6/b61d7f4b-e3c3-49f1-a600-6e6ac71a5515.jpg?1783941979"
    }
}
