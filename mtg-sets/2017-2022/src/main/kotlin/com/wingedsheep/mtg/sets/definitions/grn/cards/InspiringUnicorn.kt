package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Inspiring Unicorn
 * {2}{W}{W}
 * Creature — Unicorn
 * 2/2
 *
 * Whenever this creature attacks, creatures you control get +1/+1 until end of turn.
 */
val InspiringUnicorn = card("Inspiring Unicorn") {
    manaCost = "{2}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Unicorn"
    oracleText = "Whenever this creature attacks, creatures you control get +1/+1 until end of turn."
    power = 2
    toughness = 2

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.youControl()),
            Effects.ModifyStats(1, 1, EffectTarget.Self),
        )
        description = "Whenever this creature attacks, creatures you control get +1/+1 until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "16"
        artist = "Even Amundsen"
        flavorText = "There are two lives: the life you live before you see a unicorn, and the life you live after."
        imageUri = "https://cards.scryfall.io/normal/front/c/e/ce8b745e-1e62-4c54-9e73-e2d0a40568a0.jpg?1783934199"
    }
}
