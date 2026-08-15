package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Earth Rumble Wrestlers
 * {3}{R/G}
 * Creature — Human Warrior Performer
 * 3/4
 *
 * Reach
 * This creature gets +1/+0 and has trample as long as you control a land
 * creature or a land entered the battlefield under your control this turn.
 *
 * The conditional pump and trample share one "as long as" condition modeled as an
 * [Conditions.Any] (OR) of two checks: you control a land that is also a creature
 * (an earthbent land), or a land entered under your control this turn. The latter
 * uses the turn-tracked [DynamicAmounts.landsEnteredUnderControlThisTurn] count so
 * it stays true even if that land has since left the battlefield. The buff is split
 * across two continuous static abilities (stats + keyword) that share the condition.
 */
val EarthRumbleWrestlers = card("Earth Rumble Wrestlers") {
    manaCost = "{3}{R/G}"
    colorIdentity = "RG"
    typeLine = "Creature — Human Warrior Performer"
    power = 3
    toughness = 4
    oracleText = "Reach\n" +
        "This creature gets +1/+0 and has trample as long as you control a land creature or a land " +
        "entered the battlefield under your control this turn."

    keywords(Keyword.REACH)

    val landThreshold = Conditions.Any(
        // "you control a land creature"
        Conditions.YouControl(GameObjectFilter.Land and GameObjectFilter.Creature),
        // "a land entered the battlefield under your control this turn"
        Conditions.CompareAmounts(
            DynamicAmounts.landsEnteredUnderControlThisTurn(Player.You),
            ComparisonOperator.GTE,
            DynamicAmount.Fixed(1),
        ),
    )

    staticAbility {
        ability = ModifyStats(1, 0, Filters.Self)
        condition = landThreshold
    }

    staticAbility {
        ability = GrantKeyword(Keyword.TRAMPLE, GroupFilter.source())
        condition = landThreshold
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "218"
        artist = "Thomas Chamberlain-Keen"
        imageUri = "https://cards.scryfall.io/normal/front/7/4/74fca0b8-12e1-405f-b999-160d65fe0ade.jpg?1764121573"
    }
}
