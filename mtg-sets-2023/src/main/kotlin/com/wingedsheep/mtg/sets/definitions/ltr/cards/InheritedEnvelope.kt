package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Inherited Envelope
 * {3}
 * Artifact
 *
 * When this artifact enters, the Ring tempts you.
 * {T}: Add one mana of any color.
 */
val InheritedEnvelope = card("Inherited Envelope") {
    manaCost = "{3}"
    typeLine = "Artifact"
    oracleText = "When this artifact enters, the Ring tempts you.\n{T}: Add one mana of any color."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.TheRingTemptsYou()
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddAnyColorMana()
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "242"
        artist = "Ramazan Kazaliev"
        flavorText = "\"Keep it secret, and keep it safe!\"\n—Gandalf"
        imageUri = "https://cards.scryfall.io/normal/front/b/5/b590d028-ea6a-4550-b5e2-ba328a81bbc0.jpg?1686970192"
    }
}
