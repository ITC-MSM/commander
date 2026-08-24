package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.PayOrSufferEffect
import com.wingedsheep.sdk.scripting.effects.SacrificeSelfEffect

/**
 * Gateway Plaza
 * Land — Gate
 * This land enters tapped.
 * When this land enters, sacrifice it unless you pay {1}.
 * {T}: Add one mana of any color.
 */
val GatewayPlaza = card("Gateway Plaza") {
    manaCost = ""
    typeLine = "Land — Gate"
    oracleText = "This land enters tapped.\n" +
        "When this land enters, sacrifice it unless you pay {1}.\n" +
        "{T}: Add one mana of any color."

    replacementEffect(EntersTapped())
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = PayOrSufferEffect(cost = Costs.pay.Mana("{1}"), suffer = SacrificeSelfEffect)
    }
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddAnyColorMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "247"
        artist = "Jedd Chevrier"
        flavorText = "The Chamber of the Guildpact stands as a reminder that even the bitterest struggles can end in cooperation."
        imageUri = "https://cards.scryfall.io/normal/front/7/d/7d3d5162-126d-4430-9884-8e79afa974c2.jpg?1783934103"
    }
}
