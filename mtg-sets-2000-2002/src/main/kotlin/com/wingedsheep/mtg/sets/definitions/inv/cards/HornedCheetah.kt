package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Horned Cheetah
 * {2}{G}{W}
 * Creature — Cat
 * 2/2
 * Whenever this creature deals damage, you gain that much life.
 */
val HornedCheetah = card("Horned Cheetah") {
    manaCost = "{2}{G}{W}"
    colorIdentity = "GW"
    typeLine = "Creature — Cat"
    power = 2
    toughness = 2
    oracleText = "Whenever this creature deals damage, you gain that much life."

    triggeredAbility {
        trigger = Triggers.DealsDamage
        effect = Effects.GainLife(
            amount = DynamicAmount.ContextProperty(ContextPropertyKey.TRIGGER_DAMAGE_AMOUNT)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "251"
        artist = "John Matson"
        flavorText = "\"I think she wants us to eat it,\" said Squee, staring at the oily carcass the cheetah had dragged to their fire."
        imageUri = "https://cards.scryfall.io/normal/front/a/2/a28ad983-ce91-40b6-a1ce-fe36ec7fbce8.jpg?1562927850"
    }
}
