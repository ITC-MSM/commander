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
 * Drowned Catacomb
 * Land
 *
 * This land enters tapped unless you control an Island or a Swamp.
 * {T}: Add {U} or {B}.
 */
val DrownedCatacomb = card("Drowned Catacomb") {
    typeLine = "Land"
    colorIdentity = "BU"
    oracleText = "This land enters tapped unless you control an Island or a Swamp.\n" +
        "{T}: Add {U} or {B}."

    replacementEffect(EntersTapped(
        unlessCondition = Conditions.Any(
            Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Land.withSubtype("Island")),
            Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Land.withSubtype("Swamp"))
        )
    ))

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.BLUE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.BLACK)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "224"
        artist = "Dave Kendall"
        imageUri = "https://cards.scryfall.io/normal/front/b/d/bdc6e950-38dd-46da-a1c3-58dc7495a9f9.jpg"
    }
}
