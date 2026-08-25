package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Checkpoint Officer
 * {1}{W}
 * Creature — Human Soldier
 * 1/2
 *
 * {1}{W}, {T}: Tap target creature.
 */
val CheckpointOfficer = card("Checkpoint Officer") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    power = 1
    toughness = 2
    oracleText = "{1}{W}, {T}: Tap target creature."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{W}"), Costs.Tap)
        val t = target("target", Targets.Creature)
        effect = Effects.Tap(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "5"
        artist = "Manuel Castañón"
        flavorText = "After what he viewed as Lukka's shocking betrayal, General Kudro enacted stringent security measures at every entry point into Drannith."
        imageUri = "https://cards.scryfall.io/normal/front/4/a/4a0e12f5-7b15-4a8c-b045-35bcbf1fbb90.jpg"
    }
}
