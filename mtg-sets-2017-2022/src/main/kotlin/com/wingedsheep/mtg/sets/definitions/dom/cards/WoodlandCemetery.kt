package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.effects.AddManaEffect
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Woodland Cemetery
 * Land
 * This land enters tapped unless you control a Swamp or a Forest.
 * {T}: Add {B} or {G}.
 */
val WoodlandCemetery = card("Woodland Cemetery") {
    typeLine = "Land"
    colorIdentity = "BG"
    oracleText = "This land enters tapped unless you control a Swamp or a Forest.\n{T}: Add {B} or {G}."

    replacementEffect(EntersTapped(
        unlessCondition = Conditions.Any(
            Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Land.withSubtype("Swamp")),
            Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Land.withSubtype("Forest"))
        )
    ))

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.BLACK)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "248"
        artist = "Christine Choi"
        flavorText = "They never found the body of young Josu, or that of his murderous sister.\n—\"The Fall of the House of Vess\""
        imageUri = "https://cards.scryfall.io/normal/front/b/a/ba05cf47-9823-41f9-b893-321ea89e473e.jpg?1562741876"
    }
}
