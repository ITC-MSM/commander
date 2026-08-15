package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Three Tree Mascot
 * {2}
 * Artifact Creature — Shapeshifter
 * 2/1
 *
 * Changeling (This card is every creature type.)
 * {1}: Add one mana of any color. Activate only once each turn.
 */
val ThreeTreeMascot = card("Three Tree Mascot") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Shapeshifter"
    power = 2
    toughness = 1
    oracleText = "Changeling (This card is every creature type.)\n{1}: Add one mana of any color. Activate only once each turn."

    keywords(Keyword.CHANGELING)

    activatedAbility {
        cost = Costs.Mana("{1}")
        effect = Effects.AddAnyColorMana()
        manaAbility = true
        timing = TimingRule.ManaAbility
        restrictions = listOf(ActivationRestriction.OncePerTurn)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "251"
        artist = "Gina Matarazzo"
        flavorText = "\"Every paw and claw should be at the celebrations in Three Tree City! Even the float is smiling!\"\n—Valley Gazetteer, four-acorn review"
        imageUri = "https://cards.scryfall.io/normal/front/a/a/aaced75b-6e07-457c-8ea2-f74d99710d15.jpg?1721427313"
    }
}
