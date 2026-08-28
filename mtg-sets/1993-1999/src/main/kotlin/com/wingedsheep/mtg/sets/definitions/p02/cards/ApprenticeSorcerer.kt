package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction

/**
 * Apprentice Sorcerer
 * {2}{U}
 * Creature — Human Wizard Sorcerer
 * 1/1
 *
 * {T}: This creature deals 1 damage to any target. Activate only during your turn, before
 * attackers are declared.
 */
val ApprenticeSorcerer = card("Apprentice Sorcerer") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard Sorcerer"
    oracleText = "{T}: This creature deals 1 damage to any target. Activate only during your turn, before attackers are declared."
    power = 1
    toughness = 1

    activatedAbility {
        cost = Costs.Tap
        restrictions = listOf(
            ActivationRestriction.OnlyDuringYourTurn,
            ActivationRestriction.BeforeStep(Step.DECLARE_ATTACKERS)
        )
        val t = target("target", Targets.Any)
        effect = Effects.DealDamage(1, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "32"
        artist = "Christopher Rush"
        imageUri = "https://cards.scryfall.io/normal/front/9/e/9e1fd317-5500-40e8-ad79-323832815f81.jpg"
    }
}
