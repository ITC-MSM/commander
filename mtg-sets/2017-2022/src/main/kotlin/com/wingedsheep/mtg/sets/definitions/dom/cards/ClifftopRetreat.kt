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
 * Clifftop Retreat
 * Land
 * This land enters tapped unless you control a Mountain or a Plains.
 * {T}: Add {R} or {W}.
 */
val ClifftopRetreat = card("Clifftop Retreat") {
    typeLine = "Land"
    colorIdentity = "WR"
    oracleText = "This land enters tapped unless you control a Mountain or a Plains.\n{T}: Add {R} or {W}."

    replacementEffect(EntersTapped(
        unlessCondition = Conditions.Any(
            Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Land.withSubtype("Mountain")),
            Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Land.withSubtype("Plains"))
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
        effect = AddManaEffect(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "239"
        artist = "Christine Choi"
        flavorText = "The sunlight falls pristine on the temple at Epityr, softened by the remembered shadows of angelic saviors' wings."
        imageUri = "https://cards.scryfall.io/normal/front/e/0/e0b52b9c-7278-46b4-9f3c-3a7fc0c7e526.jpg?1562744267"
    }
}
