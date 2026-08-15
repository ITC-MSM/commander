package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaOfChoiceEffect
import com.wingedsheep.sdk.scripting.values.ManaColorSet

/**
 * Shire Scarecrow
 * {2}
 * Artifact Creature — Scarecrow
 * 0/3
 *
 * Defender
 * {1}: Add one mana of any color. Activate only once each turn.
 */
val ShireScarecrow = card("Shire Scarecrow") {
    manaCost = "{2}"
    typeLine = "Artifact Creature — Scarecrow"
    power = 0
    toughness = 3
    oracleText = "Defender\n{1}: Add one mana of any color. Activate only once each turn."

    keywords(Keyword.DEFENDER)

    activatedAbility {
        cost = Costs.Mana("{1}")
        effect = AddManaOfChoiceEffect(ManaColorSet.AnyColor, 1)
        manaAbility = true
        timing = TimingRule.ManaAbility
        restrictions = listOf(ActivationRestriction.OncePerTurn)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "249"
        artist = "Alexander Mokhov"
        flavorText = "\"How bright your garden looks!\"\n—Gandalf"
        imageUri = "https://cards.scryfall.io/normal/front/b/6/b60b4f2e-ff47-4762-a803-30e81b665c09.jpg?1686970272"
    }
}
