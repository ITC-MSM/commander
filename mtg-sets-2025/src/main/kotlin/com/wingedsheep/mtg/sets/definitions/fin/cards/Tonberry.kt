package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter

/**
 * Tonberry
 * {B}
 * Creature — Salamander Horror
 * 2/1
 * This creature enters tapped with a stun counter on it. (If it would become untapped,
 * remove a stun counter from it instead.)
 * Chef's Knife — During your turn, this creature has first strike and deathtouch.
 *
 * "Enters tapped with a stun counter" is two self-only enters replacements: [EntersTapped]
 * (unconditional) + [EntersWithCounters] of one stun counter. Stun-counter untap consumption
 * is handled engine-side by the tap/untap atom. "During your turn …" is a pair of
 * [ConditionalStaticAbility] keyword grants on [Filters.Self], gated by [Conditions.IsYourTurn].
 */
val Tonberry = card("Tonberry") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Creature — Salamander Horror"
    power = 2
    toughness = 1
    oracleText = "This creature enters tapped with a stun counter on it. (If it would become untapped, " +
        "remove a stun counter from it instead.)\n" +
        "Chef's Knife — During your turn, this creature has first strike and deathtouch."

    // Enters tapped with a stun counter on it (self only).
    replacementEffect(EntersTapped())
    replacementEffect(
        EntersWithCounters(
            counterType = CounterTypeFilter.Named("stun"),
            count = 1,
            selfOnly = true,
        )
    )

    // Chef's Knife — during your turn, this creature has first strike and deathtouch.
    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.FIRST_STRIKE, Filters.Self),
            condition = Conditions.IsYourTurn,
        )
    }
    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.DEATHTOUCH, Filters.Self),
            condition = Conditions.IsYourTurn,
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "122"
        artist = "Leonardo Santanna"
        flavorText = "Fearful monsters that creep ever forward, knives poised with deadly intent."
        imageUri = "https://cards.scryfall.io/normal/front/1/a/1a9b8723-4383-4c14-b24d-52863af8703d.jpg?1748706219"
    }
}
