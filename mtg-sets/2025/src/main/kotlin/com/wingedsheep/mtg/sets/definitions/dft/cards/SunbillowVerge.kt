package com.wingedsheep.mtg.sets.definitions.dft.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.effects.AddManaEffect
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Sunbillow Verge
 * Land
 *
 * {T}: Add {W}.
 * {T}: Add {R}. Activate only if you control a Mountain or a Plains.
 */
val SunbillowVerge = card("Sunbillow Verge") {
    typeLine = "Land"
    colorIdentity = "RW"
    oracleText = "{T}: Add {W}.\n{T}: Add {R}. Activate only if you control a Mountain or a Plains."

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
        restrictions = listOf(
            ActivationRestriction.OnlyIfCondition(
                Conditions.Any(
                    Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Land.withSubtype("Mountain")),
                    Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Land.withSubtype("Plains"))
                )
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "264"
        artist = "Pete Venters"
        flavorText = "\"Loot is free to go where he wants, so let's make sure the kid gets there in one piece.\"\n—Chandra Nalaar"
        imageUri = "https://cards.scryfall.io/normal/front/9/4/94ed132f-b818-4dbf-9b4a-e5acb067e0a4.jpg?1773857341"
    }
}
