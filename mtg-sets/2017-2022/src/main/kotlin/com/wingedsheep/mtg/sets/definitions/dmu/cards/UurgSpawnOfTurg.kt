package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Uurg, Spawn of Turg
 * {B}{B}{G}
 * Legendary Creature — Frog Beast
 * * / 5
 * Uurg's power is equal to the number of land cards in your graveyard.
 * At the beginning of your upkeep, surveil 1. (Look at the top card of your library. You may put that card into your graveyard.)
 * {B}{G}, Sacrifice a land: You gain 2 life.
 */
val UurgSpawnOfTurg = card("Uurg, Spawn of Turg") {
    manaCost = "{B}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Legendary Creature — Frog Beast"
    oracleText = "Uurg's power is equal to the number of land cards in your graveyard.\nAt the beginning of your upkeep, surveil 1. (Look at the top card of your library. You may put that card into your graveyard.)\n{B}{G}, Sacrifice a land: You gain 2 life."

    // Characteristic-defining ability (CR 604.3) — functions in every zone.
    dynamicPower(
        DynamicAmount.Count(
            player = Player.You,
            zone = Zone.GRAVEYARD,
            filter = GameObjectFilter.Land
        )
    )
    toughness = 5

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = Effects.Surveil(1)
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{B}{G}"), Costs.Sacrifice(GameObjectFilter.Land))
        effect = Effects.GainLife(2)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "225"
        artist = "Nicholas Gregory"
        imageUri = "https://cards.scryfall.io/normal/front/d/d/dd3fc36c-682b-4352-a66f-eddd2baf0bf6.jpg?1783921273"
    }
}
