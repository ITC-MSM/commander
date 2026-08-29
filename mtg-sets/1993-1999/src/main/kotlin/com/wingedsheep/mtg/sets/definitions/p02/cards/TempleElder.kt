package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction

/**
 * Temple Elder
 * {2}{W}
 * Creature — Human Cleric
 * 1/2
 *
 * {T}: You gain 1 life. Activate only during your turn, before attackers are declared.
 */
val TempleElder = card("Temple Elder") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric"
    oracleText = "{T}: You gain 1 life. Activate only during your turn, before attackers are declared."
    power = 1
    toughness = 2

    activatedAbility {
        cost = Costs.Tap
        restrictions = listOf(
            ActivationRestriction.OnlyDuringYourTurn,
            ActivationRestriction.BeforeStep(Step.DECLARE_ATTACKERS)
        )
        effect = Effects.GainLife(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "24"
        artist = "David Horne"
        flavorText = "\"Give us breath. Give us life. Prepare us for the day we make.\"\n—The Alaborn \"Rite of Battle\""
        imageUri = "https://cards.scryfall.io/normal/front/d/b/db4d4b14-d774-430c-a483-be04a238718d.jpg"
    }
}
