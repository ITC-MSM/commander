package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Mad Auntie
 * {2}{B}
 * Creature — Goblin Shaman
 * 2/2
 * Other Goblin creatures you control get +1/+1.
 * {T}: Regenerate another target Goblin.
 */
val MadAuntie = card("Mad Auntie") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Goblin Shaman"
    power = 2
    toughness = 2
    oracleText = "Other Goblin creatures you control get +1/+1.\n{T}: Regenerate another target Goblin."

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(GameObjectFilter.Creature.withSubtype(Subtype.GOBLIN).youControl(), excludeSelf = true)
        )
    }

    activatedAbility {
        cost = Costs.Tap
        // Any Goblin permanent except this Mad Auntie itself — a different Mad Auntie is a legal target.
        val t = target(
            "target",
            TargetPermanent(
                filter = TargetFilter(GameObjectFilter.Permanent.withSubtype(Subtype.GOBLIN), excludeSelf = true)
            )
        )
        effect = RegenerateEffect(t)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "123"
        artist = "Wayne Reynolds"
        flavorText = "One part cunning, one part wise, and many, many parts demented."
        imageUri = "https://cards.scryfall.io/normal/front/3/9/39b1655b-ab0a-40d9-8d4d-55d13310ede1.jpg?1783942887"
        ruling(
            "2007-10-01",
            "The second ability can target any Goblin permanent except the Mad Auntie whose ability is being " +
                "activated. It can target a different Mad Auntie."
        )
    }
}
