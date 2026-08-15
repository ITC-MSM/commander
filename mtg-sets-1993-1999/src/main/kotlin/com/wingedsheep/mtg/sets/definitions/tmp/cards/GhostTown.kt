package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.conditions.IsNotYourTurn
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ghost Town
 * Land
 *
 * {T}: Add {C}.
 * {0}: Return this land to its owner's hand. Activate only if it's not your turn.
 */
val GhostTown = card("Ghost Town") {
    typeLine = "Land"
    oracleText = "{T}: Add {C}.\n" +
        "{0}: Return this land to its owner's hand. Activate only if it's not your turn."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Mana("{0}")
        effect = Effects.ReturnToHand(EffectTarget.Self)
        restrictions = listOf(ActivationRestriction.OnlyIfCondition(IsNotYourTurn))
        description = "{0}: Return this land to its owner's hand. Activate only if it's not your turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "318"
        artist = "Tom Wänerstrand"
        imageUri = "https://cards.scryfall.io/normal/front/4/2/4218cdda-3a62-43fb-aaf7-7ac836392796.jpg?1562053761"
    }
}
