package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Viashino Slaughtermaster
 * {1}{R}
 * Creature — Lizard Warrior
 * 1/1
 * Double strike
 * {B}{G}: This creature gets +1/+1 until end of turn. Activate only once each turn.
 *
 * Double strike is a printed [Keyword]. The pump is [Effects.ModifyStats] onto
 * [EffectTarget.Self] — its default duration is already `Duration.EndOfTurn` — and the printed
 * "only once each turn" is [ActivationRestriction.OncePerTurn] on the ability itself, not a
 * condition on the effect.
 */
val ViashinoSlaughtermaster = card("Viashino Slaughtermaster") {
    manaCost = "{1}{R}"
    colorIdentity = "BGR"
    typeLine = "Creature — Lizard Warrior"
    power = 1
    toughness = 1
    oracleText = "Double strike\n" +
        "{B}{G}: This creature gets +1/+1 until end of turn. Activate only once each turn."

    keywords(Keyword.DOUBLE_STRIKE)

    activatedAbility {
        cost = Costs.Mana("{B}{G}")
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
        restrictions = listOf(ActivationRestriction.OncePerTurn)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "73"
        artist = "Raymond Swanland"
        flavorText = "\"I'll fight two at once, and then lick their guts from my blades.\""
        imageUri = "https://cards.scryfall.io/normal/front/e/6/e60c30ee-616b-4b7d-97f9-cab1d6218e3a.jpg"
    }
}
