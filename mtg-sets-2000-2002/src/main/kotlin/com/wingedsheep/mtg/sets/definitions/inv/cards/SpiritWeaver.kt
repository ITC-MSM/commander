package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Spirit Weaver
 * {1}{W}
 * Creature — Human Wizard
 * 2/1
 * {2}: Target green or blue creature gets +0/+1 until end of turn.
 */
val SpiritWeaver = card("Spirit Weaver") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Wizard"
    power = 2
    toughness = 1
    oracleText = "{2}: Target green or blue creature gets +0/+1 until end of turn."

    activatedAbility {
        cost = Costs.Mana("{2}")
        val t = target(
            "target",
            TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.withAnyColor(Color.GREEN, Color.BLUE)))
        )
        effect = Effects.ModifyStats(power = 0, toughness = 1, target = t)
        description = "{2}: Target green or blue creature gets +0/+1 until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "39"
        artist = "Matthew D. Wilson"
        imageUri = "https://cards.scryfall.io/normal/front/9/0/90b0ef47-cb22-4146-a17e-e49a6031a7e6.jpg?1562924202"
    }
}
