package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Fear of Lost Teeth
 * {B}
 * Enchantment Creature — Nightmare
 * 1/1
 * When this creature dies, it deals 1 damage to any target and you gain 1 life.
 */
val FearOfLostTeeth = card("Fear of Lost Teeth") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Enchantment Creature — Nightmare"
    oracleText = "When this creature dies, it deals 1 damage to any target and you gain 1 life."
    power = 1
    toughness = 1

    triggeredAbility {
        trigger = Triggers.Dies
        val damaged = target("any target", Targets.Any)
        effect = Effects.Composite(
            Effects.DealDamage(1, damaged),
            Effects.GainLife(1)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "97"
        artist = "Oriana Menendez"
        flavorText = "As it drew closer, Sam felt her own teeth wriggle in their sockets, as if they wanted to leap out and join the thing."
        imageUri = "https://cards.scryfall.io/normal/front/2/5/259045fe-f349-4be1-bf29-465d084ed35e.jpg?1726286211"
    }
}
