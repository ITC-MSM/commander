package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.solvedTriggeredAbility
import com.wingedsheep.sdk.dsl.toSolve
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.conditions.Compare
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.effects.SearchDestination
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Case of the Shattered Pact — Murders at Karlov Manor #1
 * {2} · Enchantment — Case · Uncommon
 *
 * When this Case enters, search your library for a basic land card, reveal it, put it into your
 * hand, then shuffle.
 * To solve — There are five colors among permanents you control.
 * Solved — At the beginning of combat on your turn, target creature you control gains flying,
 * double strike, and vigilance until end of turn.
 *
 * The colorless Case, and the only one whose "to solve" clause is a five-colour board check rather
 * than a per-turn tracker. That check is [DynamicAmounts.colorsAmongPermanents] compared against
 * `Fixed(5)` — the same aggregate Coalition Victory uses, so the ruling that a single multicoloured
 * permanent counts toward *every* colour it has falls out for free, and so does the corollary that
 * colorless permanents (including this Case) never contribute. The filter is `Permanent`, not
 * `Creature`: lands, artifacts, and the Case's own five-colour enablers all count.
 *
 * The ETB search is mandatory — no `optional = true`. A player with no basic land left still
 * searches and shuffles; the reveal is what makes an empty-handed search public.
 */
val CaseOfTheShatteredPact = card("Case of the Shattered Pact") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Enchantment — Case"
    oracleText = "When this Case enters, search your library for a basic land card, reveal it, " +
        "put it into your hand, then shuffle.\n" +
        "To solve — There are five colors among permanents you control. (If unsolved, solve at " +
        "the beginning of your end step.)\n" +
        "Solved — At the beginning of combat on your turn, target creature you control gains " +
        "flying, double strike, and vigilance until end of turn."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.BasicLand,
            destination = SearchDestination.HAND,
            reveal = true
        )
    }

    toSolve(
        Compare(
            DynamicAmounts.colorsAmongPermanents(Player.You, GameObjectFilter.Permanent),
            ComparisonOperator.GTE,
            DynamicAmount.Fixed(5)
        )
    )

    solvedTriggeredAbility {
        trigger = Triggers.BeginCombat
        target = TargetCreature(filter = TargetFilter.Creature.youControl())
        effect = Effects.Composite(
            Effects.GrantKeyword(Keyword.FLYING),
            Effects.GrantKeyword(Keyword.DOUBLE_STRIKE),
            Effects.GrantKeyword(Keyword.VIGILANCE)
        )
        description = "Solved — At the beginning of combat on your turn, target creature you " +
            "control gains flying, double strike, and vigilance until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "1"
        artist = "Peter Polach"
        imageUri = "https://cards.scryfall.io/normal/front/2/a/2a70f0ae-d49b-4cc8-9f76-895039c3dc39.jpg?1783912930"
    }
}
