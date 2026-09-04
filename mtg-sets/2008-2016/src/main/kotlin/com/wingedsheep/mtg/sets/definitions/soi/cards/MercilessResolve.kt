package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Merciless Resolve (Shadows over Innistrad #123)
 * {2}{B}
 * Instant
 *
 * As an additional cost to cast this spell, sacrifice a creature or land.
 * Draw two cards.
 *
 * The sacrifice is an additional *cost* (CR 601.2h), so it is paid as the spell is cast: the
 * creature is already gone when the spell resolves, and countering the spell doesn't give it back.
 */
val MercilessResolve = card("Merciless Resolve") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "As an additional cost to cast this spell, sacrifice a creature or land.\n" +
        "Draw two cards."

    additionalCost(Costs.additional.SacrificePermanent(GameObjectFilter.CreatureOrLand))

    spell {
        effect = Effects.DrawCards(2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "123"
        artist = "Chase Stone"
        flavorText = "\"You sought to anger me, Nahiri. Soon you will see how well you have succeeded.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/5/356af156-a059-416c-b78b-d9058b742818.jpg?1783937770"
    }
}
