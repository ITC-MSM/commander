package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedBy
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Scuttling Doom Engine
 * {6}
 * Artifact Creature — Construct
 * 6/6
 * This creature can't be blocked by creatures with power 2 or less.
 * When this creature dies, it deals 6 damage to target opponent or planeswalker.
 */
val ScuttlingDoomEngine = card("Scuttling Doom Engine") {
    manaCost = "{6}"
    typeLine = "Artifact Creature — Construct"
    power = 6
    toughness = 6
    oracleText =
        "This creature can't be blocked by creatures with power 2 or less.\n" +
        "When this creature dies, it deals 6 damage to target opponent or planeswalker."

    staticAbility {
        ability = CantBeBlockedBy(GameObjectFilter.Creature.powerAtMost(2))
    }

    triggeredAbility {
        trigger = Triggers.Dies
        val t = target("target opponent or planeswalker", Targets.OpponentOrPlaneswalker)
        effect = Effects.DealDamage(6, t)
        description = "When this creature dies, it deals 6 damage to target opponent or planeswalker."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "229"
        artist = "Filip Burburan"
        flavorText = "A masterwork of spite, inspired by madness."
        imageUri = "https://cards.scryfall.io/normal/front/6/3/6352ded6-14f5-47d6-b1cb-518f270e44e7.jpg?1783939155"
    }
}
