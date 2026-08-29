package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Elemental Bond
 * {2}{G}
 * Enchantment
 *
 * Whenever a creature you control with power 3 or greater enters, draw a card.
 *
 * `TriggerBinding.ANY` — the trigger watches every creature entering under your control, not the
 * enchantment itself, and power is read off the entering creature's projected state.
 */
val ElementalBond = card("Elemental Bond") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment"
    oracleText = "Whenever a creature you control with power 3 or greater enters, draw a card."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature.powerAtLeast(3).youControl(),
            binding = TriggerBinding.ANY
        )
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "174"
        artist = "David Gaillet"
        flavorText = "\"I want to help Zendikar. Show me the way.\"\n" +
            "—Nissa Revane"
        imageUri = "https://cards.scryfall.io/normal/front/5/5/554a8769-c840-4c9d-9959-b075c174457b.jpg"
    }
}
