package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Sigardian Paladin
 * {2}{G}{W}
 * Creature — Human Knight
 * 4/4
 *
 * As long as you've put one or more +1/+1 counters on a creature this turn, this creature has
 * trample and lifelink.
 * {1}{G}{W}: Target creature you control with a +1/+1 counter on it gains trample and lifelink
 * until end of turn.
 *
 * The gate is turn *history*, not a board state: [Conditions.PutCounterKindOnCreatureThisTurn]
 * reads the per-player record of what was placed this turn, so the ability stays on even after the
 * creature that received the counter has left the battlefield, lost its counters, or stopped being
 * a creature — all three of which the first ruling calls out. A board scan for "a creature with a
 * +1/+1 counter" would answer no in every one of those cases.
 *
 * "On **a** creature" is also why this is not the per-permanent `receivedCounterThisTurn`
 * predicate (Kid Loki, Beast, Erudite Aerialist): any creature counts, not this one.
 *
 * Two grants under one condition, [GrantKeyword] taking a single keyword — the Nezumi Bladeblesser
 * shape. The activated ability's target keeps the counter requirement in its *filter* (a creature
 * without one is not a legal target); once it has resolved the grant outlives the counter, which is
 * what "until end of turn" already means.
 */
val SigardianPaladin = card("Sigardian Paladin") {
    manaCost = "{2}{G}{W}"
    colorIdentity = "GW"
    typeLine = "Creature — Human Knight"
    power = 4
    toughness = 4
    oracleText = "As long as you've put one or more +1/+1 counters on a creature this turn, this " +
        "creature has trample and lifelink.\n" +
        "{1}{G}{W}: Target creature you control with a +1/+1 counter on it gains trample and " +
        "lifelink until end of turn."

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.TRAMPLE, GroupFilter.source()),
            condition = Conditions.PutCounterKindOnCreatureThisTurn(Counters.PLUS_ONE_PLUS_ONE),
        )
    }

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.LIFELINK, GroupFilter.source()),
            condition = Conditions.PutCounterKindOnCreatureThisTurn(Counters.PLUS_ONE_PLUS_ONE),
        )
    }

    activatedAbility {
        cost = Costs.Mana("{1}{G}{W}")
        val t = target(
            "target creature you control with a +1/+1 counter on it",
            TargetObject(
                filter = TargetFilter(
                    GameObjectFilter.Creature
                        .youControl()
                        .withCounter(Counters.PLUS_ONE_PLUS_ONE),
                ),
            ),
        )
        effect = Effects.Composite(
            Effects.GrantKeyword(Keyword.TRAMPLE, t),
            Effects.GrantKeyword(Keyword.LIFELINK, t),
        )
        description = "{1}{G}{W}: Target creature you control with a +1/+1 counter on it gains " +
            "trample and lifelink until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "247"
        artist = "Slawomir Maniak"
        imageUri = "https://cards.scryfall.io/normal/front/3/4/34228fdd-e466-488e-84b2-4c595e758688.jpg?1783924787"
        ruling(
            "2021-11-19",
            "Sigardian Paladin's first ability applies as long as you put a +1/+1 counter on a " +
                "permanent this turn and that permanent was a creature at the time you put the " +
                "counter on. It doesn't matter if that creature later left the battlefield, lost " +
                "its counters, or somehow stopped being a creature.",
        )
        ruling(
            "2021-11-19",
            "Sigardian Paladin's last ability can target only a creature with a +1/+1 counter on " +
                "it. Once that ability has resolved, the creature has trample and lifelink until " +
                "end of turn even if it somehow loses its counters or stops being a creature " +
                "(although trample doesn't help a noncreature permanent much).",
        )
    }
}
