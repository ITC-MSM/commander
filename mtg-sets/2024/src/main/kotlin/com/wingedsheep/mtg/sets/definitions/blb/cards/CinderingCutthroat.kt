package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Cindering Cutthroat
 * {2}{B/R}
 * Creature — Lizard Assassin
 * 3/2
 *
 * This creature enters with a +1/+1 counter on it if an opponent lost life this turn.
 * {1}{B/R}: This creature gains menace until end of turn.
 */
val CinderingCutthroat = card("Cindering Cutthroat") {
    manaCost = "{2}{B/R}"
    colorIdentity = "BR"
    typeLine = "Creature — Lizard Assassin"
    oracleText = "This creature enters with a +1/+1 counter on it if an opponent lost life this turn.\n" +
        "{1}{B/R}: This creature gains menace until end of turn."
    power = 3
    toughness = 2

    // "Enters with a counter" is a replacement effect (rule 614.1c), not an ETB trigger
    replacementEffect(EntersWithCounters(
        counterType = CounterTypeFilter.PlusOnePlusOne,
        count = 1,
        selfOnly = true,
        condition = Conditions.OpponentLostLifeThisTurn
    ))

    // {1}{B/R}: This creature gains menace until end of turn
    activatedAbility {
        cost = Costs.Mana("{1}{B/R}")
        effect = Effects.GrantKeyword(Keyword.MENACE, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "208"
        artist = "Wayne Reynolds"
        flavorText = "\"We don't 'play' with fire. It is strictly business.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/2/b2ea10dd-21ea-4622-be27-79d03a802b85.jpg?1721427019"
    }
}
