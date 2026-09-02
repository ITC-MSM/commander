package com.wingedsheep.mtg.sets.definitions.stx.cards

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
 * Archway Commons — Strixhaven: School of Mages #263 (canonical printing)
 * (no mana cost) · Land
 *
 * This land enters tapped.
 * When this land enters, sacrifice it unless you pay {1}.
 * {T}: Add one mana of any color.
 *
 * The Gateway Plaza / Rupture Spire shape: an unconditional [EntersTapped], an enters trigger whose
 * "sacrifice it unless you pay {1}" is a [PayOrSufferEffect] with a mana atom for the pay half and
 * [SacrificeSelfEffect] for the suffer half, and a five-colour mana ability via
 * [Effects.AddManaOfChoice].
 */
val ArchwayCommons = card("Archway Commons") {
    manaCost = ""
    colorIdentity = ""
    typeLine = "Land"
    oracleText =
        "This land enters tapped.\n" +
        "When this land enters, sacrifice it unless you pay {1}.\n" +
        "{T}: Add one mana of any color."

    replacementEffect(EntersTapped())

    // When this land enters, sacrifice it unless you pay {1}.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = PayOrSufferEffect(cost = Costs.pay.Mana("{1}"), suffer = SacrificeSelfEffect)
    }

    // {T}: Add one mana of any color.
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddManaOfChoice()
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "263"
        artist = "Piotr Dura"
        imageUri = "https://cards.scryfall.io/normal/front/f/6/f6f6a2ff-7eb7-4680-af2b-e69ac88a65c9.jpg?1783927276"
    }
}
