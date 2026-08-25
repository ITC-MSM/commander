package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Bonders' Enclave
 * Land
 *
 * {T}: Add {C}.
 * {3}, {T}: Draw a card. Activate only if you control a creature with power 4 or greater.
 *
 * The draw ability's restriction reads projected power through the filter's `powerAtLeast`
 * predicate, so a pumped or animated permanent turns it on.
 */
val BondersEnclave = card("Bonders' Enclave") {
    colorIdentity = ""
    typeLine = "Land"
    oracleText = "{T}: Add {C}.\n{3}, {T}: Draw a card. Activate only if you control a creature with power 4 or greater."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}"), Costs.Tap)
        effect = Effects.DrawCards(1)
        restrictions = listOf(
            ActivationRestriction.OnlyIfCondition(
                Conditions.YouControl(GameObjectFilter.Creature.powerAtLeast(4))
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "245"
        artist = "Cliff Childs"
        flavorText = "There is a sanctuary that reveals itself only to those graced by the *eludha*—the mystical connection between bonder and monster."
        imageUri = "https://cards.scryfall.io/normal/front/4/f/4fe9388d-b1ee-4f35-9fbd-5f504528b398.jpg"
    }
}
