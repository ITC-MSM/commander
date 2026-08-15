package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GrantKeyword

/**
 * Karakyk Guardian — Tarkir: Dragonstorm #198
 * {3}{G}{U}{R} · Creature — Dragon · 6/5
 *
 * Flying, vigilance, trample
 * This creature has hexproof if it hasn't dealt damage yet.
 *
 * The conditional hexproof is a [ConditionalStaticAbility] wrapping a self-targeted
 * [GrantKeyword]; the gate is `Not(SourceHasDealtDamage)`, evaluated during state
 * projection. Once this creature has dealt damage at least once since entering the
 * battlefield, the hexproof drops off.
 */
val KarakykGuardian = card("Karakyk Guardian") {
    manaCost = "{3}{G}{U}{R}"
    colorIdentity = "GUR"
    typeLine = "Creature — Dragon"
    power = 6
    toughness = 5
    oracleText = "Flying, vigilance, trample\n" +
        "This creature has hexproof if it hasn't dealt damage yet. " +
        "(It can't be the target of spells or abilities your opponents control.)"

    keywords(Keyword.FLYING, Keyword.VIGILANCE, Keyword.TRAMPLE)

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.HEXPROOF, Filters.Self),
            condition = Conditions.Not(Conditions.SourceHasDealtDamage)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "198"
        artist = "Joe Slucher"
        imageUri = "https://cards.scryfall.io/normal/front/a/4/a4c77b08-c3f6-4458-8636-f226f9843b6d.jpg?1743233806"
    }
}
