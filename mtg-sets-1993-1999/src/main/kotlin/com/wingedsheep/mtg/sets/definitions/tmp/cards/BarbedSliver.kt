package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Barbed Sliver
 * {2}{R}
 * Creature — Sliver
 * 2/2
 * All Sliver creatures have "{2}: This creature gets +1/+0 until end of turn."
 */
val BarbedSliver = card("Barbed Sliver") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Sliver"
    power = 2
    toughness = 2
    oracleText = "All Sliver creatures have \"{2}: This creature gets +1/+0 until end of turn.\""

    val sliverFilter = GroupFilter(GameObjectFilter.Creature.withSubtype("Sliver"))

    staticAbility {
        ability = GrantActivatedAbility(
            ability = ActivatedAbility(
                id = AbilityId.generate(),
                cost = Costs.Mana("{2}"),
                effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
            ),
            filter = sliverFilter
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "163"
        artist = "Scott Kirschner"
        flavorText = "Spans of spines leapt from one sliver to the next, forming a deadly hedge around the *Weatherlight*."
        imageUri = "https://cards.scryfall.io/normal/front/1/9/19bddea7-daa7-4bdb-9b91-f7fcbc0d7a57.jpg?1562052790"
    }
}
