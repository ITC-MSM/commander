package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.AnyTarget

/**
 * Staggershock
 * {2}{R}
 * Instant
 *
 * Staggershock deals 2 damage to any target.
 * Rebound (If you cast this spell from your hand, exile it as it resolves. At the beginning of your next upkeep, you may cast this card from exile without paying its mana cost.)
 *
 * Modeling notes:
 *  - Rebound has a real consumer — `StackResolver` reads [Keyword.REBOUND] off `cardDef.keywords`
 *    when the spell resolves — so the bare keyword is the whole of the second line and the two
 *    damage lands twice, a turn apart, from one [Effects.DealDamage].
 *  - "any target" is [AnyTarget], the creature-or-player-or-planeswalker requirement, not a
 *    creature filter; the rebound cast picks a fresh one on the upkeep.
 *  - No `damageSource` override: the spell deals the damage itself, which is the facade's default.
 */
val Staggershock = card("Staggershock") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Staggershock deals 2 damage to any target.\n" +
        "Rebound (If you cast this spell from your hand, exile it as it resolves. At the beginning of your next upkeep, you may cast this card from exile without paying its mana cost.)"

    keywords(Keyword.REBOUND)

    spell {
        val t = target("any target", AnyTarget())
        effect = Effects.DealDamage(2, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "166"
        artist = "Raymond Swanland"
        imageUri = "https://cards.scryfall.io/normal/front/7/5/75624ab3-ddbd-4fe8-8a07-7d1f78ec8a84.jpg?1783941971"
    }
}
