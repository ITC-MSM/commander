package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Mana Geode
 * {3}
 * Artifact
 *
 * When this artifact enters, scry 1.
 * {T}: Add one mana of any color.
 */
val ManaGeode = card("Mana Geode") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "When this artifact enters, scry 1.\n{T}: Add one mana of any color."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Scry(1)
        description = "When this artifact enters, scry 1."
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddAnyColorMana()
        manaAbility = true
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "241"
        artist = "Raoul Vitale"
        flavorText = "\"I don't care if it's an all-powerful relic or a street vendor's lucky charm. If it brings you courage, wear it.\"\n—Saheeli Rai"
        imageUri = "https://cards.scryfall.io/normal/front/1/7/17315a12-a7f8-45ba-ac3b-a62c789e75d0.jpg?1783933371"
    }
}
