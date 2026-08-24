package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Righteous Blow
 * {W}
 * Instant
 * Righteous Blow deals 2 damage to target attacking or blocking creature.
 */
val RighteousBlow = card("Righteous Blow") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Righteous Blow deals 2 damage to target attacking or blocking creature."

    spell {
        val creature = target(
            "target",
            TargetObject(filter = TargetFilter(GameObjectFilter.Creature.attackingOrBlocking()))
        )
        effect = Effects.DealDamage(2, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "34"
        artist = "Clint Cearley"
        flavorText = "\"Monsters will no longer find safety under the mists of Morkrut!\"\n—Bruna, Light of Alabaster"
        imageUri = "https://cards.scryfall.io/normal/front/9/b/9b640fdc-7a19-475e-858f-e159f61e154e.jpg?1783940729"
    }
}
