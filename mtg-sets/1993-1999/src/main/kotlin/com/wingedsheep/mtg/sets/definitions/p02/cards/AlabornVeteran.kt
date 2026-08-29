package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction

/**
 * Alaborn Veteran
 * {2}{W}
 * Creature — Human Knight
 * 2/2
 *
 * {T}: Target creature gets +2/+2 until end of turn. Activate only during your turn, before
 * attackers are declared.
 */
val AlabornVeteran = card("Alaborn Veteran") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Knight"
    oracleText = "{T}: Target creature gets +2/+2 until end of turn. Activate only during your turn, before attackers are declared."
    power = 2
    toughness = 2

    activatedAbility {
        cost = Costs.Tap
        restrictions = listOf(
            ActivationRestriction.OnlyDuringYourTurn,
            ActivationRestriction.BeforeStep(Step.DECLARE_ATTACKERS)
        )
        val t = target("target", Targets.Creature)
        effect = Effects.ModifyStats(2, 2, t)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "5"
        artist = "Henry Van Der Linde"
        imageUri = "https://cards.scryfall.io/normal/front/7/9/79d7992d-9acd-4eaa-b6e0-a47acb880548.jpg"
    }
}
