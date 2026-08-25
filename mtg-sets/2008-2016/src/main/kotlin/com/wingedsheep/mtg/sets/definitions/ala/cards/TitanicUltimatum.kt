package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Titanic Ultimatum
 * {R}{R}{G}{G}{G}{W}{W}
 * Sorcery
 * Until end of turn, creatures you control get +5/+5 and gain first strike, trample, and lifelink.
 *
 * The Naya ultimatum is Overrun's shape with three keywords instead of one, so it is a single
 * [Effects.ForEachInGroup] over [GroupFilter.AllCreaturesYouControl] whose body is one
 * [Effects.Composite] — the printed sentence names its group once, and gathering it once is what
 * keeps a power-reading filter from matching a different set on a second pass.
 * `Patterns.Group.pumpAndGrantToAll` is the two-clause facade for this sentence but carries only a
 * single keyword, so the body is spelled out: [Effects.ModifyStats]`(5, 5)` plus one
 * [Effects.GrantKeyword] per keyword, each bound to [EffectTarget.Self] — the per-iteration member —
 * and each defaulting to `Duration.EndOfTurn`, the fronted "until end of turn".
 */
val TitanicUltimatum = card("Titanic Ultimatum") {
    manaCost = "{R}{R}{G}{G}{G}{W}{W}"
    colorIdentity = "GRW"
    typeLine = "Sorcery"
    oracleText = "Until end of turn, creatures you control get +5/+5 and gain first strike, trample, and lifelink."

    spell {
        effect = Effects.ForEachInGroup(
            GroupFilter.AllCreaturesYouControl,
            Effects.Composite(
                Effects.ModifyStats(5, 5, EffectTarget.Self),
                Effects.GrantKeyword(Keyword.FIRST_STRIKE, EffectTarget.Self),
                Effects.GrantKeyword(Keyword.TRAMPLE, EffectTarget.Self),
                Effects.GrantKeyword(Keyword.LIFELINK, EffectTarget.Self)
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "204"
        artist = "Steve Prescott"
        flavorText = "\"Retribution is best delivered by claws and rage, with both magnified.\"\n—Ajani"
        imageUri = "https://cards.scryfall.io/normal/front/8/d/8d28fd77-1f7c-47b4-b9ff-55835a7f2526.jpg"
    }
}
