package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Sentry of the Underworld
 * {3}{W}{B}
 * Creature — Griffin Skeleton
 * 3 / 3
 *
 * Flying, vigilance
 * {W}{B}, Pay 3 life: Regenerate this creature.
 *
 * "Regenerate this creature" is [RegenerateEffect] on [EffectTarget.Self]; there is no
 * `Effects.Regenerate` facade — the effect class is the shipped spelling (Cudgel Troll).
 */
val SentryOfTheUnderworld = card("Sentry of the Underworld") {
    manaCost = "{3}{W}{B}"
    colorIdentity = "WB"
    typeLine = "Creature — Griffin Skeleton"
    power = 3
    toughness = 3
    oracleText = "Flying, vigilance\n{W}{B}, Pay 3 life: Regenerate this creature."

    keywords(Keyword.FLYING, Keyword.VIGILANCE)

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{W}{B}"), Costs.PayLife(3))
        effect = RegenerateEffect(EffectTarget.Self)
        description = "{W}{B}, Pay 3 life: Regenerate this creature."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "202"
        artist = "Dave Kendall"
        flavorText = "When Athreos gathers the newly dead to be ferried across the Five Rivers That Ring the World, he sends skeletal griffins to fetch those who stray."
        imageUri = "https://cards.scryfall.io/normal/front/0/1/018a9c98-1cb4-4c53-a765-c15808c4ab44.jpg"
    }
}
