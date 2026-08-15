package com.wingedsheep.mtg.sets.definitions.m20.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Cryptic Caves
 * Land
 * {T}: Add {C}.
 * {1}, {T}, Sacrifice this land: Draw a card. Activate only if you control five or more lands.
 *
 * The land counts itself towards the five — it is still on the battlefield while the ability's
 * activation legality is checked (the sacrifice is paid as a cost afterwards), so
 * [Conditions.YouControlAtLeast] over [GameObjectFilter.Land] is the faithful reading.
 */
val CrypticCaves = card("Cryptic Caves") {
    manaCost = ""
    colorIdentity = ""
    typeLine = "Land"
    oracleText = "{T}: Add {C}.\n" +
        "{1}, {T}, Sacrifice this land: Draw a card. Activate only if you control five or more lands."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap, Costs.SacrificeSelf)
        restrictions = listOf(
            ActivationRestriction.OnlyIfCondition(
                Conditions.YouControlAtLeast(5, GameObjectFilter.Land)
            )
        )
        effect = Effects.DrawCards(1)
        description = "{1}, {T}, Sacrifice this land: Draw a card. " +
            "Activate only if you control five or more lands."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "244"
        artist = "Sung Choi"
        flavorText = "Only when you've given up the search will the caves yield their secrets."
        imageUri = "https://cards.scryfall.io/normal/front/f/d/fde9e9cb-68ab-4856-8ad6-30f66666dd93.jpg?1783932938"
    }
}
