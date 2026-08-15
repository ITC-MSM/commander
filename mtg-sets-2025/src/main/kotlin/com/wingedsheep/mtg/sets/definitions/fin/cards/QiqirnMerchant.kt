package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.DrawCardsEffect
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Qiqirn Merchant
 * {2}{U}
 * Creature — Beast Citizen
 * 1/4
 * {1}, {T}: Draw a card, then discard a card.
 * {7}, {T}, Sacrifice this creature: Draw three cards. This ability costs {1} less to
 *   activate for each Town you control.
 *
 * The second ability's cost reduction rides the existing
 * [com.wingedsheep.sdk.scripting.ActivatedAbility.genericCostReduction] field: any
 * [com.wingedsheep.sdk.scripting.values.DynamicAmount] reduces the generic-mana portion of the cost
 * at activation time, evaluated once before costs are paid (enumerator + handler stay in sync). Here
 * it's a battlefield count of Towns you control, so the {7} drops by {1} per Town (floored at {0}).
 */
val QiqirnMerchant = card("Qiqirn Merchant") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Beast Citizen"
    power = 1
    toughness = 4
    oracleText = "{1}, {T}: Draw a card, then discard a card.\n" +
        "{7}, {T}, Sacrifice this creature: Draw three cards. This ability costs {1} less to " +
        "activate for each Town you control."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap)
        effect = Effects.Composite(
            DrawCardsEffect(1),
            Patterns.Hand.discardCards(1)
        )
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{7}"), Costs.Tap, Costs.SacrificeSelf)
        effect = DrawCardsEffect(3)
        genericCostReduction =
            DynamicAmounts.battlefield(Player.You, GameObjectFilter.Land.withSubtype("Town")).count()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "65"
        artist = "Andrea Tentori Montalto"
        imageUri = "https://cards.scryfall.io/normal/front/a/7/a75a6ecc-a6a5-462c-bd92-ae57dde9b965.jpg?1748705999"
    }
}
