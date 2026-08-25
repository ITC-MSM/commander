package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Ajani's Welcome
 * {W}
 * Enchantment
 * Whenever a creature you control enters, you gain 1 life.
 *
 * The trigger watches *any* creature entering under your control, not the enchantment itself, so
 * the binding is [TriggerBinding.ANY] rather than the `entersBattlefield` default of `SELF`.
 */
val AjanisWelcome = card("Ajani's Welcome") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "Whenever a creature you control enters, you gain 1 life."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature.youControl(),
            binding = TriggerBinding.ANY
        )
        effect = Effects.GainLife(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "6"
        artist = "Eric Deschamps"
        flavorText = "\"You cannot defend others if your own well-being is neglected.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/9/c9045fcb-b633-4c35-8058-6234311551ae.jpg"
    }
}
