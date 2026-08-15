package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Clot Sliver
 * {1}{B}
 * Creature — Sliver
 * 1/1
 * All Slivers have "{2}: Regenerate this permanent."
 */
val ClotSliver = card("Clot Sliver") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Sliver"
    power = 1
    toughness = 1
    oracleText = "All Slivers have \"{2}: Regenerate this permanent.\""

    val sliverFilter = GroupFilter(GameObjectFilter.Creature.withSubtype("Sliver"))

    staticAbility {
        ability = GrantActivatedAbility(
            ability = ActivatedAbility(
                id = AbilityId.generate(),
                cost = Costs.Mana("{2}"),
                effect = RegenerateEffect(EffectTarget.Self)
            ),
            filter = sliverFilter
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "112"
        artist = "Jeff Laubenstein"
        flavorText = "\"One would think I would be accustomed to unexpected returns.\"\n—Hanna, *Weatherlight* navigator"
        imageUri = "https://cards.scryfall.io/normal/front/f/d/fdead1f4-a6e4-4370-80ae-811881a90d01.jpg?1562057819"
    }
}
