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
 * Willowrush Verge
 * Land
 *
 * {T}: Add {U}.
 * {T}: Add {G}. Activate only if you control a Forest or an Island.
 */
val WillowrushVerge = card("Willowrush Verge") {
    typeLine = "Land"
    colorIdentity = "GU"
    oracleText = "{T}: Add {U}.\n{T}: Add {G}. Activate only if you control a Forest or an Island."

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.BLUE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
        restrictions = listOf(
            ActivationRestriction.OnlyIfCondition(
                Conditions.Any(
                    Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Land.withSubtype("Forest")),
                    Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Land.withSubtype("Island"))
                )
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "270"
        artist = "Aaron Miller"
        flavorText = "\"If nothing else, that Loot is one brave little rat.\"\n—Winter"
        imageUri = "https://cards.scryfall.io/normal/front/7/5/758d93d5-3f66-4395-a928-000485396c87.jpg?1773857344"
    }
}
