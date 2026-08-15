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
 * Wastewood Verge
 * Land
 *
 * {T}: Add {G}.
 * {T}: Add {B}. Activate only if you control a Swamp or a Forest.
 */
val WastewoodVerge = card("Wastewood Verge") {
    typeLine = "Land"
    colorIdentity = "BG"
    oracleText = "{T}: Add {G}.\n{T}: Add {B}. Activate only if you control a Swamp or a Forest."

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.BLACK)
        manaAbility = true
        timing = TimingRule.ManaAbility
        restrictions = listOf(
            ActivationRestriction.OnlyIfCondition(
                Conditions.Any(
                    Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Land.withSubtype("Swamp")),
                    Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Land.withSubtype("Forest"))
                )
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "268"
        artist = "Bartek Fedyczak"
        flavorText = "\"I'm worried about Loot. He has great power but lacks the experience to know what dangers await him.\"\n—Vraska"
        imageUri = "https://cards.scryfall.io/normal/front/5/c/5ceacc7d-d407-4f82-af58-9bdf8426924e.jpg?1773857343"
    }
}
