package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Pyre Spawn
 * {4}{R}{R}
 * Creature — Elemental
 * 6/4
 *
 * When this creature dies, it deals 3 damage to any target.
 */
val PyreSpawn = card("Pyre Spawn") {
    manaCost = "{4}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental"
    oracleText = "When this creature dies, it deals 3 damage to any target."
    power = 6
    toughness = 4

    triggeredAbility {
        trigger = Triggers.Dies
        val damaged = target("any target", Targets.Any)
        effect = Effects.DealDamage(3, damaged)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "173"
        artist = "Nicholas Gregory"
        flavorText = "Memories of the Harvesttide tragedy festered until they grew into an incarnate rage that set the world ablaze."
        imageUri = "https://cards.scryfall.io/normal/front/8/0/80255777-de00-4ffa-a8a0-f522bf4198fb.jpg?1783924827"
    }
}
