package com.wingedsheep.mtg.sets.definitions.usg.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Greater Good
 * {2}{G}{G}
 * Enchantment
 * Sacrifice a creature: Draw cards equal to the sacrificed creature's power,
 * then discard three cards.
 *
 * The sacrificed creature's power is read from last known information, which
 * `DynamicAmounts.sacrificedPower()` resolves via `EntityReference.Sacrificed`.
 */
val GreaterGood = card("Greater Good") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment"
    oracleText = "Sacrifice a creature: Draw cards equal to the sacrificed creature's power, then discard three cards."

    activatedAbility {
        cost = Costs.Sacrifice(GameObjectFilter.Creature)
        effect = Effects.DrawCards(DynamicAmounts.sacrificedPower()) then Effects.Discard(3)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "257"
        artist = "Pete Venters"
        flavorText = "\"We have more sprouts than they have hands.\"\n—Gamelen, Citanul elder"
        imageUri = "https://cards.scryfall.io/normal/front/1/2/12befd35-2dc6-4852-a153-75b553042643.jpg?1562898942"
    }
}
