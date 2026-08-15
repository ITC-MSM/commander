package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * El-Hajjâj
 * {1}{B}{B}
 * Creature — Human Wizard
 * 1/1
 * Whenever this creature deals damage, you gain that much life.
 */
val ElHajjaj = card("El-Hajjâj") {
    manaCost = "{1}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Wizard"
    power = 1
    toughness = 1
    oracleText = "Whenever this creature deals damage, you gain that much life."

    triggeredAbility {
        trigger = Triggers.dealsDamage(binding = TriggerBinding.SELF)
        effect = Effects.GainLife(DynamicAmount.ContextProperty(ContextPropertyKey.TRIGGER_DAMAGE_AMOUNT))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "24"
        artist = "Drew Tucker"
        imageUri = "https://cards.scryfall.io/normal/front/c/4/c4b610d3-2005-4347-bcda-c30b5b7972e5.jpg?1562931818"
    }
}
