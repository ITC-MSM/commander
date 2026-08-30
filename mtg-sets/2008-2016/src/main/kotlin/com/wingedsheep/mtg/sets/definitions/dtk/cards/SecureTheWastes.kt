package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Secure the Wastes
 * {X}{W}
 * Instant
 *
 * Create X 1/1 white Warrior creature tokens.
 *
 * One token effect with a dynamic count rather than X repetitions of a fixed one: the
 * [DynamicAmount.XValue] overload of `Effects.CreateToken` reads the announced X at resolution, so
 * a cost-increase or a copied spell sees the value that was actually paid.
 */
val SecureTheWastes = card("Secure the Wastes") {
    manaCost = "{X}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Create X 1/1 white Warrior creature tokens."

    spell {
        effect = Effects.CreateToken(
            count = DynamicAmount.XValue,
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Warrior")
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "36"
        artist = "Scott Murphy"
        flavorText = "\"The Shifting Wastes provide our clan eternal protection. It is our duty to return the favor.\"\n—Kadri, Dromoka warrior"
        imageUri = "https://cards.scryfall.io/normal/front/9/1/915d2f5f-b228-4190-ade9-52e2a8056847.jpg?1783938613"
    }
}
