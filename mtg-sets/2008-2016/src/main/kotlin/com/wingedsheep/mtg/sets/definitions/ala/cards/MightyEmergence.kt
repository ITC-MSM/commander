package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Mighty Emergence
 * {2}{G}
 * Enchantment
 * Whenever a creature you control with power 5 or greater enters, you may put two +1/+1 counters on it.
 *
 * The watcher is [Triggers.entersBattlefield] over `GameObjectFilter.Creature.youControl().powerAtLeast(5)`
 * with [TriggerBinding.ANY] — the enchantment can never be the creature that entered, and the printed
 * line says "a creature", not "another". The counters land on [EffectTarget.TriggeringEntity] (the
 * creature that entered, not a fresh target), and `optional = true` lowers the printed "you may" into
 * the same `Gate.MayDecide` a hand-written `MayEffect` would build.
 */
val MightyEmergence = card("Mighty Emergence") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment"
    oracleText = "Whenever a creature you control with power 5 or greater enters, you may put two +1/+1 counters on it."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature.youControl().powerAtLeast(5),
            binding = TriggerBinding.ANY
        )
        optional = true
        effect = Effects.AddCounters("+1/+1", 2, EffectTarget.TriggeringEntity)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "137"
        artist = "Steve Prescott"
        flavorText = "Why settle for mere enormity?"
        imageUri = "https://cards.scryfall.io/normal/front/8/e/8ee66318-4dbb-49a3-a3c8-ee0e1a9f02b7.jpg"
    }
}
