package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Brown Ouphe
 * {G}
 * Creature — Ouphe
 * 1/1
 *
 * {1}{G}, {T}: Counter target activated ability from an artifact source.
 * (Mana abilities can't be targeted.)
 */
val BrownOuphe = card("Brown Ouphe") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Ouphe"
    power = 1
    toughness = 1
    oracleText = "{1}{G}, {T}: Counter target activated ability from an artifact source. " +
        "(Mana abilities can't be targeted.)"

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{G}"), Costs.Tap)
        target = TargetObject(
            filter = TargetFilter.ActivatedAbilityOnStack
                .abilitySourceMatches(GameObjectFilter.Artifact)
        )
        effect = Effects.CounterAbility()
        description = "{1}{G}, {T}: Counter target activated ability from an artifact source."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "228"
        artist = "Daniel Gelon"
        flavorText = "\"Ouphes love trinkets and love to take them apart. I only wish they wouldn't do so with the magical ones.\"\n—Taaveti of Kelsinko, Elvish Hunter"
        imageUri = "https://cards.scryfall.io/normal/front/e/2/e26ce35b-ba65-451d-a5ed-e1db6f1d0c6f.jpg?1783947480"
        ruling("6/8/2016", "Activated abilities contain a colon and are generally written as '[Cost]: [Effect].' Some keywords are activated abilities and have colons in their reminder text.")
    }
}
