package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Spreading Plague
 * {4}{B}
 * Enchantment
 * Whenever a creature enters, destroy all other creatures that share a color with it.
 * They can't be regenerated.
 */
val SpreadingPlague = card("Spreading Plague") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment"
    oracleText = "Whenever a creature enters, destroy all other creatures that share a color " +
        "with it. They can't be regenerated."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature,
            binding = TriggerBinding.ANY,
        )
        effect = Effects.DestroyAll(
            filter = GameObjectFilter.Creature.sharingColorWith(EntityReference.Triggering),
            noRegenerate = true,
            excludeTriggering = true,
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "125"
        artist = "Scott Bailey"
        imageUri = "https://cards.scryfall.io/normal/front/a/c/ac86055d-ce08-4b05-a92c-45e007ca0ba4.jpg?1562929780"
    }
}
