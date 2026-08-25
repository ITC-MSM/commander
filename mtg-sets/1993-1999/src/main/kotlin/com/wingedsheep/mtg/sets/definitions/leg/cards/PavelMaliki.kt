package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Pavel Maliki
 * {4}{B}{R}
 * Legendary Creature — Human
 * 5/3
 *
 * {B}{R}: Pavel Maliki gets +1/+0 until end of turn.
 */
val PavelMaliki = card("Pavel Maliki") {
    manaCost = "{4}{B}{R}"
    colorIdentity = "BR"
    typeLine = "Legendary Creature — Human"
    power = 5
    toughness = 3
    oracleText = "{B}{R}: Pavel Maliki gets +1/+0 until end of turn."

    activatedAbility {
        cost = Costs.Mana("{B}{R}")
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "248"
        artist = "Andi Rusu"
        flavorText = "We all know the legend: Pavel wanders the realms, helping those in greatest need. But is " +
            "this a measure of his generosity, or of his obligation to atone?"
        imageUri = "https://cards.scryfall.io/normal/front/3/0/304f9d39-3ea2-4274-b23e-e4eaabbc1c4b.jpg?1783948035"
    }
}
