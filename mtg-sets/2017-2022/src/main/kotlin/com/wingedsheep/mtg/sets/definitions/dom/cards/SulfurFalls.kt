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
 * Sulfur Falls
 * Land
 * This land enters tapped unless you control an Island or a Mountain.
 * {T}: Add {U} or {R}.
 */
val SulfurFalls = card("Sulfur Falls") {
    typeLine = "Land"
    colorIdentity = "UR"
    oracleText = "This land enters tapped unless you control an Island or a Mountain.\n{T}: Add {U} or {R}."

    replacementEffect(EntersTapped(
        unlessCondition = Conditions.Any(
            Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Land.withSubtype("Island")),
            Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Land.withSubtype("Mountain"))
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
        effect = AddManaEffect(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "247"
        artist = "Cliff Childs"
        flavorText = "\"We have inherited the mysteries of the Thran, but few of the answers.\" —Jhoira"
        imageUri = "https://cards.scryfall.io/normal/front/6/b/6bc412fc-e9d4-4283-bd4a-811384e79b5c.jpg?1562737296"
    }
}
