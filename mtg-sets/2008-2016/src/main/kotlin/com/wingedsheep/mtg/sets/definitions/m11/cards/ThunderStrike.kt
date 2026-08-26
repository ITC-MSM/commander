package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Thunder Strike
 * {1}{R}
 * Instant
 *
 * Target creature gets +2/+0 and gains first strike until end of turn. (It deals combat damage
 * before creatures without first strike.)
 *
 * The one-keyword sibling of Whirling Strike: a [Effects.Composite] of the pump and the grant,
 * both on the same target slot and both taking the default `Duration.EndOfTurn`.
 */
val ThunderStrike = card("Thunder Strike") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Target creature gets +2/+0 and gains first strike until end of turn. " +
        "(It deals combat damage before creatures without first strike.)"

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.Composite(
            Effects.ModifyStats(2, 0, t),
            Effects.GrantKeyword(Keyword.FIRST_STRIKE, t)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "157"
        artist = "Wayne Reynolds"
        flavorText = "Lightning reflexes, thunderous might."
        imageUri = "https://cards.scryfall.io/normal/front/1/f/1f94f88b-d928-4364-9126-231eabf14086.jpg?1783941802"
    }
}
