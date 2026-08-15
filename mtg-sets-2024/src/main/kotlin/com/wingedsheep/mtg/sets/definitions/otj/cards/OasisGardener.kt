package com.wingedsheep.mtg.sets.definitions.otj.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Oasis Gardener
 * {3}
 * Artifact Creature — Scarecrow
 * 2/2
 *
 * When this creature enters, you gain 2 life.
 * {T}: Add one mana of any color.
 */
val OasisGardener = card("Oasis Gardener") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Scarecrow"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, you gain 2 life.\n{T}: Add one mana of any color."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(2)
        description = "you gain 2 life."
    }

    activatedAbility {
        cost = AbilityCost.Tap
        effect = Effects.AddAnyColorMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "246"
        artist = "Kristina Carroll"
        flavorText = "\"I think the blasted thing's actually attracting more birds, but all the same, " +
            "the garden's never looked better.\"\n—Jay Marigold, homesteader"
        imageUri = "https://cards.scryfall.io/normal/front/e/e/ee0dc663-4bfb-46d4-af79-d0143c799487.jpg?1712356276"
    }
}
