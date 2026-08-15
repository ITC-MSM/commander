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
 * Hate Weaver
 * {1}{B}
 * Creature — Zombie Wizard
 * 2/1
 * {2}: Target blue or red creature gets +1/+0 until end of turn.
 */
val HateWeaver = card("Hate Weaver") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie Wizard"
    power = 2
    toughness = 1
    oracleText = "{2}: Target blue or red creature gets +1/+0 until end of turn."

    activatedAbility {
        cost = Costs.Mana("{2}")
        val t = target(
            "target",
            TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.withAnyColor(Color.BLUE, Color.RED)))
        )
        effect = Effects.ModifyStats(power = 1, toughness = 0, target = t)
        description = "{2}: Target blue or red creature gets +1/+0 until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "108"
        artist = "Roger Raupp"
        imageUri = "https://cards.scryfall.io/normal/front/8/3/8328e131-b44d-4dd0-9ce4-454c6afe6fa6.jpg?1562921569"
    }
}
