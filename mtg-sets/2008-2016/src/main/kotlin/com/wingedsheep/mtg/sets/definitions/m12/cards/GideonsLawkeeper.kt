package com.wingedsheep.mtg.sets.definitions.m12.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Gideon's Lawkeeper
 * {W}
 * Creature — Human Soldier
 * 1/1
 * {W}, {T}: Tap target creature.
 */
val GideonsLawkeeper = card("Gideon's Lawkeeper") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    power = 1
    toughness = 1
    oracleText = "{W}, {T}: Tap target creature."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{W}"), Costs.Tap)
        val creature = target("creature", Targets.Creature)
        effect = Effects.Tap(creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "18"
        artist = "Steve Prescott"
        flavorText = "\"The essence of a lawful society is swift deterrence.\""
        imageUri = "https://cards.scryfall.io/normal/front/1/c/1c71eb81-a077-4c85-a4ce-4ad664486bee.jpg?1783941103"
    }
}
