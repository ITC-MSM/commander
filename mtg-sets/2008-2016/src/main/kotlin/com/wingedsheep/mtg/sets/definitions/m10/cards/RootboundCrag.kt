package com.wingedsheep.mtg.sets.definitions.m10.cards

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
 * Rootbound Crag
 * Land
 * This land enters tapped unless you control a Mountain or a Forest.
 * {T}: Add {R} or {G}.
 */
val RootboundCrag = card("Rootbound Crag") {
    typeLine = "Land"
    colorIdentity = "RG"
    oracleText = "This land enters tapped unless you control a Mountain or a Forest.\n{T}: Add {R} or {G}."

    replacementEffect(EntersTapped(
        unlessCondition = Conditions.Any(
            Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Land.withSubtype("Mountain")),
            Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Land.withSubtype("Forest"))
        )
    ))

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.RED)
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
        collectorNumber = "227"
        artist = "Matt Stewart"
        imageUri = "https://cards.scryfall.io/normal/front/5/4/5433b11b-efe9-4d94-8f71-6bf7c403494d.jpg?1561980328"
    }
}
