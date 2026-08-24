package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Mogg Raider
 * {R}
 * Creature — Goblin
 * 1/1
 * Sacrifice a Goblin: Target creature gets +1/+1 until end of turn.
 */
val MoggRaider = card("Mogg Raider") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin"
    power = 1
    toughness = 1
    oracleText = "Sacrifice a Goblin: Target creature gets +1/+1 until end of turn."

    activatedAbility {
        cost = Costs.Sacrifice(GameObjectFilter.Permanent.withSubtype(Subtype.GOBLIN))
        val t = target("target", Targets.Creature)
        effect = Effects.ModifyStats(1, 1, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "191"
        artist = "Brian Snõddy"
        flavorText = "The evisceration of one mogg always cheers up the rest."
        imageUri = "https://cards.scryfall.io/normal/front/9/4/94e9cc0a-c210-4525-8c7f-9c6306cc21b0.jpg"
    }
}
