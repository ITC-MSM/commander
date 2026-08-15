package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Iroh's Demonstration
 * {1}{R}
 * Sorcery — Lesson
 * Choose one —
 * • Iroh's Demonstration deals 1 damage to each creature your opponents control.
 * • Iroh's Demonstration deals 4 damage to target creature.
 */
val IrohsDemonstration = card("Iroh's Demonstration") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery — Lesson"
    oracleText = "Choose one —\n" +
        "• Iroh's Demonstration deals 1 damage to each creature your opponents control.\n" +
        "• Iroh's Demonstration deals 4 damage to target creature."

    spell {
        modal(chooseCount = 1) {
            mode("Iroh's Demonstration deals 1 damage to each creature your opponents control.") {
                effect = Effects.ForEachInGroup(
                    filter = GroupFilter.AllCreaturesOpponentsControl,
                    effect = DealDamageEffect(1, EffectTarget.Self),
                )
            }
            mode("Iroh's Demonstration deals 4 damage to target creature.") {
                val t = target("target creature", Targets.Creature)
                effect = Effects.DealDamage(4, t)
            }
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "141"
        artist = "Song Qijin"
        flavorText = "\"Did I ever tell you how I got the nickname 'The Dragon of the West?'\""
        imageUri = "https://cards.scryfall.io/normal/front/1/8/18d15fed-1f8f-4407-a221-a47ce75001a8.jpg?1764120967"
    }
}
